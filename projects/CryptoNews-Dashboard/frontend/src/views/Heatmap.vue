<template>
  <div class="heatmap">
    <el-card class="heatmap-card">
      <template #header>
        <div class="card-header">
          <span>📊 加密货币涨跌热力图</span>
          <div class="controls">
            <el-select v-model="timeRange" size="small" style="width: 120px;">
              <el-option label="24小时" value="24h" />
              <el-option label="7天" value="7d" />
            </el-select>
            <el-button size="small" @click="refreshHeatmap" :loading="loading">
              刷新
            </el-button>
          </div>
        </div>
      </template>
      
      <div v-if="loading" class="loading-container">
        <el-icon class="is-loading" :size="40"><Loading /></el-icon>
        <p>加载中...</p>
      </div>
      
      <div v-else-if="cryptoData.length === 0" class="empty-container">
        <el-empty description="暂无数据" />
      </div>
      
      <div v-else class="heatmap-container">
        <v-chart :option="chartOption" autoresize style="height: 600px;" />
      </div>
    </el-card>

    <!-- 图例说明 -->
    <el-card class="legend-card">
      <div class="legend">
        <div class="legend-item">
          <div class="color-box strong-positive"></div>
          <span>大涨 (+10%以上)</span>
        </div>
        <div class="legend-item">
          <div class="color-box positive"></div>
          <span>上涨 (0~+10%)</span>
        </div>
        <div class="legend-item">
          <div class="color-box neutral"></div>
          <span>持平 (-2%~+2%)</span>
        </div>
        <div class="legend-item">
          <div class="color-box negative"></div>
          <span>下跌 (-10%~0)</span>
        </div>
        <div class="legend-item">
          <div class="color-box strong-negative"></div>
          <span>大跌 (-10%以下)</span>
        </div>
      </div>
    </el-card>
    
    <!-- 币种详情弹窗 -->
    <el-dialog v-model="detailVisible" :title="selectedCoin?.name" width="600px">
      <div v-if="coinDetail" class="coin-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="当前价格">${{ formatPrice(coinDetail.currentPrice) }}</el-descriptions-item>
          <el-descriptions-item label="市值">${{ formatLargeNumber(coinDetail.marketCap) }}</el-descriptions-item>
          <el-descriptions-item label="24h涨跌">
            <span :class="coinDetail.change24h >= 0 ? 'positive' : 'negative'">
              {{ coinDetail.change24h >= 0 ? '+' : '' }}{{ coinDetail.change24h?.toFixed(2) }}%
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="24h交易量">${{ formatLargeNumber(coinDetail.volume24h) }}</el-descriptions-item>
          <el-descriptions-item label="历史最高">${{ formatPrice(coinDetail.ath) }}</el-descriptions-item>
          <el-descriptions-item label="距最高价">{{ coinDetail.athChangePercentage?.toFixed(2) }}%</el-descriptions-item>
          <el-descriptions-item label="流通量">{{ formatLargeNumber(coinDetail.circulatingSupply) }}</el-descriptions-item>
          <el-descriptions-item label="总供应量">{{ formatLargeNumber(coinDetail.totalSupply) }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button type="primary" @click="goToAnalysis">查看 AI 分析</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { TreemapChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent } from 'echarts/components'
import { cryptoApi } from '@/api'
import { Loading } from '@element-plus/icons-vue'

use([CanvasRenderer, TreemapChart, TitleComponent, TooltipComponent])

const router = useRouter()

const loading = ref(false)
const timeRange = ref('24h')
const cryptoData = ref<any[]>([])
const detailVisible = ref(false)
const selectedCoin = ref<any>(null)
const coinDetail = ref<any>(null)

// 获取变化字段
const changeField = computed(() => timeRange.value === '24h' ? 'change24h' : 'change7d')

// 颜色映射
const getColor = (change: number) => {
  if (change >= 10) return '#00ff88'
  if (change >= 2) return '#00cc6a'
  if (change >= 0) return '#009955'
  if (change >= -2) return '#4a4a6a'
  if (change >= -10) return '#ff6b6b'
  return '#ff4757'
}

// 热力图配置
const chartOption = computed(() => ({
  tooltip: {
    formatter: (params: any) => {
      const data = params.data
      return `
        <strong>${data.name} (${data.symbol})</strong><br/>
        价格: $${formatPrice(data.price)}<br/>
        市值: $${formatLargeNumber(data.marketCap)}<br/>
        ${timeRange.value === '24h' ? '24h' : '7d'}涨跌: ${data.change >= 0 ? '+' : ''}${data.change?.toFixed(2)}%
      `
    }
  },
  series: [
    {
      type: 'treemap',
      data: cryptoData.value.map(item => ({
        name: item.name,
        symbol: item.symbol,
        value: item.marketCap,
        price: item.price,
        change: item[changeField.value],
        itemStyle: {
          color: getColor(item[changeField.value] || 0),
          borderColor: '#1a1a2e',
          borderWidth: 2,
          gapWidth: 2
        }
      })),
      roam: false,
      nodeClick: 'link',
      breadcrumb: {
        show: false
      },
      label: {
        show: true,
        formatter: (params: any) => {
          const change = params.data.change || 0
          return `${params.data.symbol}\n${change >= 0 ? '+' : ''}${change.toFixed(2)}%`
        },
        fontSize: 14,
        fontWeight: 'bold',
        color: '#fff'
      }
    }
  ]
}))

// 加载热力图数据
const loadHeatmap = async () => {
  loading.value = true
  try {
    const res = await cryptoApi.getHeatmap(50) as any
    if (res.data?.cryptos) {
      cryptoData.value = res.data.cryptos
    }
  } catch (e) {
    console.error('加载热力图失败', e)
  } finally {
    loading.value = false
  }
}

// 刷新
const refreshHeatmap = async () => {
  try {
    await cryptoApi.refresh()
    await loadHeatmap()
  } catch (e) {
    console.error('刷新失败', e)
  }
}

// 查看币种详情
const viewCoinDetail = async (coin: any) => {
  selectedCoin.value = coin
  try {
    const res = await cryptoApi.getDetail(coin.symbol) as any
    coinDetail.value = res.data
    detailVisible.value = true
  } catch (e) {
    console.error('获取详情失败', e)
  }
}

// 跳转分析页
const goToAnalysis = () => {
  if (selectedCoin.value) {
    router.push(`/analysis?symbol=${selectedCoin.value.symbol}`)
  }
  detailVisible.value = false
}

// 格式化价格
const formatPrice = (price: number) => {
  if (!price) return '0'
  if (price < 0.01) return price.toFixed(6)
  if (price < 1) return price.toFixed(4)
  return price.toLocaleString('en-US', { maximumFractionDigits: 2 })
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
  loadHeatmap()
})
</script>

<style scoped>
.heatmap-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.controls {
  display: flex;
  gap: 12px;
}

.loading-container, .empty-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px;
  color: #a0a0a0;
}

.heatmap-container {
  background: #0f0f1a;
  border-radius: 8px;
  padding: 16px;
}

.legend-card {
  margin-top: 20px;
}

.legend {
  display: flex;
  justify-content: center;
  gap: 32px;
  flex-wrap: wrap;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #a0a0a0;
}

.color-box {
  width: 20px;
  height: 20px;
  border-radius: 4px;
}

.strong-positive { background: #00ff88; }
.positive { background: #00cc6a; }
.neutral { background: #4a4a6a; }
.negative { background: #ff6b6b; }
.strong-negative { background: #ff4757; }

.positive { color: #00ff88; }
.negative { color: #ff4757; }

.coin-detail {
  padding: 20px 0;
}
</style>