<template>
  <div class="analysis">
    <!-- AI 摘要卡片 -->
    <el-card class="digest-card">
      <template #header>
        <div class="card-header">
          <span>🤖 今日市场摘要</span>
          <el-button size="small" @click="refreshDigest" :loading="digestLoading">
            刷新
          </el-button>
        </div>
      </template>
      <div class="digest-content">
        <p>{{ todayDigest.summary }}</p>
        <div class="digest-stats">
          <span>📊 今日新闻: {{ todayDigest.totalNews }} 条</span>
          <span>📈 市场概况: {{ todayDigest.marketOverview }}</span>
        </div>
      </div>
    </el-card>

    <!-- 投资建议生成 -->
    <el-card class="advice-card">
      <template #header>
        <span>💡 AI 投资建议</span>
      </template>
      
      <el-form inline class="advice-form">
        <el-form-item label="选择币种">
          <el-select v-model="selectedCoin" placeholder="选择币种" style="width: 200px;">
            <el-option label="Bitcoin (BTC)" value="BTC" />
            <el-option label="Ethereum (ETH)" value="ETH" />
            <el-option label="Solana (SOL)" value="SOL" />
            <el-option label="Dogecoin (DOGE)" value="DOGE" />
            <el-option label="Cardano (ADA)" value="ADA" />
            <el-option label="Ripple (XRP)" value="XRP" />
            <el-option label="Chainlink (LINK)" value="LINK" />
            <el-option label="Avalanche (AVAX)" value="AVAX" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button 
            type="primary" 
            @click="generateAdvice" 
            :loading="adviceLoading"
          >
            生成建议
          </el-button>
        </el-form-item>
      </el-form>

      <div v-if="advice" class="advice-result">
        <el-alert type="warning" :closable="false" class="risk-alert">
          <template #title>
            ⚠️ 风险提示
          </template>
          {{ advice.riskWarning }}
        </el-alert>
        
        <div class="advice-content">
          <h4>📊 综合分析</h4>
          <p>{{ advice.analysis }}</p>
          
          <h4>📈 市场情绪</h4>
          <div class="sentiment-indicator">
            <el-progress 
              :percentage="advice.sentimentScore" 
              :color="getSentimentColor(advice.sentimentScore)"
            />
            <span class="sentiment-label">{{ advice.sentimentLabel }}</span>
          </div>
          
          <h4>💡 操作建议</h4>
          <el-tag :type="getSuggestionType(advice.suggestion)" size="large">
            {{ advice.suggestion }}
          </el-tag>
          
          <h4 v-if="advice.keyFactors?.length">🔍 关键因素</h4>
          <ul class="key-factors">
            <li v-for="(factor, index) in advice.keyFactors" :key="index">
              {{ factor }}
            </li>
          </ul>
        </div>
        
        <el-divider />
        
        <p class="disclaimer">{{ advice.disclaimer }}</p>
      </div>
    </el-card>

    <!-- 市场分析 -->
    <el-card class="market-card">
      <template #header>
        <div class="card-header">
          <span>🌍 市场综合分析</span>
          <el-button size="small" @click="loadMarketAnalysis" :loading="marketLoading">
            刷新
          </el-button>
        </div>
      </template>
      
      <el-row :gutter="20" v-if="marketAnalysis">
        <el-col :span="8">
          <div class="stat-box">
            <div class="stat-label">市场情绪</div>
            <div class="stat-value">{{ marketAnalysis.overallSentiment }}</div>
            <el-progress 
              :percentage="marketAnalysis.sentimentScore" 
              :color="getSentimentColor(marketAnalysis.sentimentScore)"
            />
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-box">
            <div class="stat-label">BTC 主导率</div>
            <div class="stat-value">{{ marketAnalysis.btcDominance?.toFixed(1) }}%</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-box">
            <div class="stat-label">总市值</div>
            <div class="stat-value">${{ formatLargeNumber(marketAnalysis.globalMarketCap) }}</div>
          </div>
        </el-col>
      </el-row>
      
      <el-divider />
      
      <h4>🔥 热门币种</h4>
      <div class="hot-coins">
        <div v-for="coin in marketAnalysis?.hotCoins" :key="coin.symbol" class="hot-coin-item">
          <span class="coin-symbol">{{ coin.symbol }}</span>
          <span :class="['coin-change', coin.change24h >= 0 ? 'positive' : 'negative']">
            {{ coin.change24h >= 0 ? '+' : '' }}{{ coin.change24h?.toFixed(2) }}%
          </span>
        </div>
      </div>
      
      <el-divider />
      
      <h4>📰 重要新闻</h4>
      <div class="key-news">
        <div v-for="news in marketAnalysis?.keyNews" :key="news.title" class="news-item">
          <span class="news-title">{{ news.title }}</span>
          <el-tag :type="getSentimentTagType(news.sentiment)" size="small">
            {{ news.sentiment }}
          </el-tag>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { aiApi } from '@/api'

const route = useRoute()

const digestLoading = ref(false)
const adviceLoading = ref(false)
const marketLoading = ref(false)
const selectedCoin = ref(route.query.symbol as string || '')

const todayDigest = reactive({
  summary: '加载中...',
  totalNews: 0,
  marketOverview: ''
})

const advice = ref<any>(null)
const marketAnalysis = ref<any>(null)

// 加载今日摘要
const loadTodayDigest = async () => {
  digestLoading.value = true
  try {
    const res = await aiApi.getTodayDigest() as any
    if (res.data) {
      todayDigest.summary = res.data.summary
      todayDigest.totalNews = res.data.totalNews
      todayDigest.marketOverview = res.data.marketOverview
    }
  } catch (e) {
    console.error('加载摘要失败', e)
  } finally {
    digestLoading.value = false
  }
}

// 生成投资建议
const generateAdvice = async () => {
  if (!selectedCoin.value) {
    return
  }
  
  adviceLoading.value = true
  try {
    const res = await aiApi.getAdvice(selectedCoin.value) as any
    advice.value = res.data
  } catch (e) {
    console.error('生成建议失败', e)
  } finally {
    adviceLoading.value = false
  }
}

// 加载市场分析
const loadMarketAnalysis = async () => {
  marketLoading.value = true
  try {
    const res = await aiApi.getMarketAnalysis() as any
    marketAnalysis.value = res.data
  } catch (e) {
    console.error('加载市场分析失败', e)
  } finally {
    marketLoading.value = false
  }
}

// 刷新摘要
const refreshDigest = async () => {
  await loadTodayDigest()
}

// 获取情绪颜色
const getSentimentColor = (score: number) => {
  if (score >= 70) return '#00ff88'
  if (score >= 50) return '#ffcc00'
  return '#ff4757'
}

// 获取建议类型
const getSuggestionType = (suggestion: string) => {
  if (suggestion?.includes('持有') || suggestion?.includes('乐观')) return 'success'
  if (suggestion?.includes('观望')) return 'info'
  if (suggestion?.includes('减仓') || suggestion?.includes('谨慎')) return 'warning'
  return 'info'
}

// 获取情感标签类型
const getSentimentTagType = (sentiment: string) => {
  if (sentiment === 'POSITIVE' || sentiment === '利好') return 'success'
  if (sentiment === 'NEGATIVE' || sentiment === '利空') return 'danger'
  return 'info'
}

// 格式化大数字
const formatLargeNumber = (num: number) => {
  if (!num) return '0'
  if (num >= 1e12) return (num / 1e12).toFixed(2) + 'T'
  if (num >= 1e9) return (num / 1e9).toFixed(2) + 'B'
  if (num >= 1e6) return (num / 1e6).toFixed(2) + 'M'
  return num.toLocaleString()
}

onMounted(() => {
  loadTodayDigest()
  loadMarketAnalysis()
  
  // 如果有传入币种，自动生成建议
  if (selectedCoin.value) {
    generateAdvice()
  }
})
</script>

<style scoped>
.digest-card, .advice-card, .market-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.digest-content {
  padding: 16px;
  background: rgba(0, 217, 255, 0.05);
  border-radius: 8px;
  border-left: 3px solid #00d9ff;
}

.digest-content p {
  line-height: 1.6;
  color: #c0c0c0;
  margin-bottom: 12px;
}

.digest-stats {
  display: flex;
  gap: 24px;
  font-size: 13px;
  color: #a0a0a0;
}

.advice-form {
  margin-bottom: 20px;
}

.advice-result {
  margin-top: 20px;
}

.risk-alert {
  margin-bottom: 20px;
}

.advice-content {
  padding: 16px;
  background: #0f0f1a;
  border-radius: 8px;
}

.advice-content h4 {
  margin: 16px 0 8px;
  color: #00d9ff;
  font-size: 14px;
}

.advice-content h4:first-child {
  margin-top: 0;
}

.advice-content p {
  color: #c0c0c0;
  line-height: 1.6;
}

.sentiment-indicator {
  display: flex;
  align-items: center;
  gap: 16px;
}

.sentiment-indicator .el-progress {
  flex: 1;
}

.sentiment-label {
  color: #e0e0e0;
  font-weight: 500;
}

.key-factors {
  list-style: none;
  padding: 0;
}

.key-factors li {
  padding: 8px 0;
  color: #a0a0a0;
  border-bottom: 1px solid #2a2a4a;
}

.key-factors li:last-child {
  border-bottom: none;
}

.disclaimer {
  font-size: 12px;
  color: #808080;
  text-align: center;
}

.stat-box {
  text-align: center;
  padding: 16px;
  background: #0f0f1a;
  border-radius: 8px;
}

.stat-label {
  font-size: 13px;
  color: #a0a0a0;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 20px;
  font-weight: 600;
  color: #e0e0e0;
  margin-bottom: 8px;
}

.hot-coins {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.hot-coin-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: #0f0f1a;
  border-radius: 8px;
}

.coin-symbol {
  font-weight: 500;
  color: #00d9ff;
}

.coin-change.positive { color: #00ff88; }
.coin-change.negative { color: #ff4757; }

.key-news {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.key-news .news-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: #0f0f1a;
  border-radius: 8px;
}

.key-news .news-title {
  flex: 1;
  color: #c0c0c0;
  font-size: 14px;
}

.positive { color: #00ff88; }
.negative { color: #ff4757; }
</style>