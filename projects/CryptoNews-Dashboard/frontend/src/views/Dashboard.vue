<template>
  <div class="dashboard">
    <!-- 市场概览卡片 -->
    <el-row :gutter="20" class="overview-cards">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-label">总市值</div>
          <div class="stat-value">${{ formatLargeNumber(marketOverview.totalMarketCap) }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-label">24h 交易量</div>
          <div class="stat-value">${{ formatLargeNumber(marketOverview.totalVolume) }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-label">BTC 主导率</div>
          <div class="stat-value">{{ marketOverview.btcDominance?.toFixed(1) }}%</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-label">活跃币种</div>
          <div class="stat-value">{{ formatNumber(marketOverview.activeCryptos) }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 主内容区 -->
    <el-row :gutter="20" class="main-content">
      <!-- 热门币种 -->
      <el-col :span="12">
        <el-card class="section-card">
          <template #header>
            <div class="card-header">
              <span>🔥 热门币种</span>
              <el-button text @click="goToHeatmap">查看热力图</el-button>
            </div>
          </template>
          <div v-if="loading" class="loading-container">
            <el-icon class="is-loading" :size="32"><Loading /></el-icon>
          </div>
          <div v-else class="coin-list">
            <div v-for="coin in hotCoins" :key="coin.symbol" class="coin-item">
              <div class="coin-left">
                <span class="coin-rank">#{{ coin.marketCapRank }}</span>
                <div class="coin-info">
                  <span class="coin-symbol">{{ coin.symbol }}</span>
                  <span class="coin-name">{{ coin.name }}</span>
                </div>
              </div>
              <div class="coin-price">
                <span class="price">${{ formatPrice(coin.price) }}</span>
                <span :class="['change', coin.change24h >= 0 ? 'positive' : 'negative']">
                  {{ coin.change24h >= 0 ? '+' : '' }}{{ coin.change24h?.toFixed(2) }}%
                </span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 最新新闻 -->
      <el-col :span="12">
        <el-card class="section-card">
          <template #header>
            <div class="card-header">
              <span>📰 最新资讯</span>
              <el-button text @click="goToNews">查看全部</el-button>
            </div>
          </template>
          <div v-if="newsLoading" class="loading-container">
            <el-icon class="is-loading" :size="32"><Loading /></el-icon>
          </div>
          <div v-else class="news-list">
            <div v-for="news in latestNews" :key="news.id" class="news-item" @click="openNews(news.url)">
              <div class="news-title">{{ news.title }}</div>
              <div class="news-meta">
                <span class="source">{{ news.source }}</span>
                <span class="time">{{ formatTime(news.publishedAt) }}</span>
                <el-tag 
                  v-if="news.sentimentLabel"
                  :type="getSentimentType(news.sentimentLabel)" 
                  size="small"
                >
                  {{ getSentimentText(news.sentimentLabel) }}
                </el-tag>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- AI 市场分析 -->
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card class="section-card ai-card">
          <template #header>
            <div class="card-header">
              <span>🤖 AI 市场洞察</span>
              <el-button text @click="goToAnalysis">详细分析</el-button>
            </div>
          </template>
          <div v-if="digestLoading" class="loading-container">
            <el-icon class="is-loading" :size="32"><Loading /></el-icon>
          </div>
          <div v-else class="ai-insight">
            <p>{{ aiInsight }}</p>
            <div class="ai-actions">
              <el-button type="primary" @click="goToAnalysis">
                获取详细投资建议
              </el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { cryptoApi, newsApi, aiApi } from '@/api'
import { Loading } from '@element-plus/icons-vue'
import dayjs from 'dayjs'

const router = useRouter()

const loading = ref(true)
const newsLoading = ref(true)
const digestLoading = ref(true)

const marketOverview = ref({
  totalMarketCap: 0,
  totalVolume: 0,
  btcDominance: 0,
  activeCryptos: 0
})

const hotCoins = ref<any[]>([])
const latestNews = ref<any[]>([])
const aiInsight = ref('正在分析市场数据...')

// 加载市场概览
const loadMarketOverview = async () => {
  try {
    const res = await cryptoApi.getOverview() as any
    if (res.data) {
      marketOverview.value = res.data
    }
  } catch (e) {
    console.error('加载市场概览失败', e)
  }
}

// 加载热门币种
const loadHotCoins = async () => {
  loading.value = true
  try {
    const res = await cryptoApi.getHeatmap(10) as any
    if (res.data?.cryptos) {
      hotCoins.value = res.data.cryptos
    }
  } catch (e) {
    console.error('加载热门币种失败', e)
  } finally {
    loading.value = false
  }
}

// 加载最新新闻
const loadLatestNews = async () => {
  newsLoading.value = true
  try {
    const res = await newsApi.getList(1, 5) as any
    if (res.data?.list) {
      latestNews.value = res.data.list
    }
  } catch (e) {
    console.error('加载新闻失败', e)
  } finally {
    newsLoading.value = false
  }
}

// 加载 AI 洞察
const loadAIInsight = async () => {
  digestLoading.value = true
  try {
    const res = await aiApi.getTodayDigest() as any
    if (res.data?.summary) {
      aiInsight.value = res.data.summary
    }
  } catch (e) {
    console.error('加载 AI 洞察失败', e)
    aiInsight.value = '暂无市场洞察数据'
  } finally {
    digestLoading.value = false
  }
}

// 格式化大数字
const formatLargeNumber = (num: number) => {
  if (!num) return '0'
  if (num >= 1e12) return (num / 1e12).toFixed(2) + 'T'
  if (num >= 1e9) return (num / 1e9).toFixed(2) + 'B'
  if (num >= 1e6) return (num / 1e6).toFixed(2) + 'M'
  return num.toLocaleString()
}

// 格式化数字
const formatNumber = (num: number) => {
  if (!num) return '0'
  return num.toLocaleString()
}

// 格式化价格
const formatPrice = (price: number) => {
  if (!price) return '0'
  if (price < 0.01) return price.toFixed(6)
  if (price < 1) return price.toFixed(4)
  return price.toLocaleString('en-US', { maximumFractionDigits: 2 })
}

// 格式化时间
const formatTime = (time: string) => {
  return dayjs(time).format('MM-DD HH:mm')
}

// 获取情感类型
const getSentimentType = (label: string) => {
  if (label === 'POSITIVE') return 'success'
  if (label === 'NEGATIVE') return 'danger'
  return 'info'
}

// 获取情感文本
const getSentimentText = (label: string) => {
  if (label === 'POSITIVE') return '利好'
  if (label === 'NEGATIVE') return '利空'
  return '中性'
}

// 跳转
const goToHeatmap = () => router.push('/heatmap')
const goToNews = () => router.push('/news')
const goToAnalysis = () => router.push('/analysis')

// 打开新闻
const openNews = (url: string) => {
  window.open(url, '_blank')
}

onMounted(() => {
  loadMarketOverview()
  loadHotCoins()
  loadLatestNews()
  loadAIInsight()
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.overview-cards {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
  padding: 10px 0;
}

.stat-label {
  font-size: 13px;
  color: #a0a0a0;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #e0e0e0;
}

.section-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.loading-container {
  display: flex;
  justify-content: center;
  padding: 40px;
}

.coin-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.coin-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: #0f0f1a;
  border-radius: 8px;
}

.coin-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.coin-rank {
  font-size: 12px;
  color: #606060;
  width: 30px;
}

.coin-symbol {
  font-weight: 600;
  color: #00d9ff;
  margin-right: 8px;
}

.coin-name {
  color: #a0a0a0;
  font-size: 13px;
}

.price {
  font-weight: 500;
  margin-right: 12px;
}

.change {
  font-size: 13px;
}

.change.positive {
  color: #00ff88;
}

.change.negative {
  color: #ff4757;
}

.news-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.news-item {
  padding: 12px;
  background: #0f0f1a;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.news-item:hover {
  background: #1a1a2e;
}

.news-title {
  font-size: 14px;
  color: #e0e0e0;
  margin-bottom: 8px;
  line-height: 1.4;
}

.news-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: #a0a0a0;
}

.ai-card {
  background: linear-gradient(135deg, #1a1a2e 0%, #2a1a3e 100%);
}

.ai-insight {
  padding: 16px;
  background: rgba(0, 217, 255, 0.05);
  border-radius: 8px;
  border-left: 3px solid #00d9ff;
}

.ai-insight p {
  line-height: 1.6;
  margin-bottom: 16px;
  color: #c0c0c0;
}

.ai-actions {
  display: flex;
  justify-content: flex-end;
}
</style>