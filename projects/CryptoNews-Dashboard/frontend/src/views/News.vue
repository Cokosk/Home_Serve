<template>
  <div class="news">
    <!-- 筛选栏 -->
    <el-card class="filter-card">
      <el-form inline>
        <el-form-item label="情感">
          <el-select v-model="filter.sentiment" placeholder="全部" clearable style="width: 120px;">
            <el-option label="利好" value="POSITIVE" />
            <el-option label="中性" value="NEUTRAL" />
            <el-option label="利空" value="NEGATIVE" />
          </el-select>
        </el-form-item>
        <el-form-item label="币种">
          <el-select v-model="filter.coin" placeholder="全部" clearable style="width: 120px;">
            <el-option label="BTC" value="BTC" />
            <el-option label="ETH" value="ETH" />
            <el-option label="SOL" value="SOL" />
            <el-option label="DOGE" value="DOGE" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">搜索</el-button>
          <el-button @click="resetFilter">重置</el-button>
          <el-button type="success" @click="triggerCollect" :loading="collecting">
            采集新闻
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-row :gutter="20">
      <!-- 新闻列表 -->
      <el-col :span="18">
        <el-card class="news-list-card">
          <div v-if="loading" class="loading-container">
            <el-icon class="is-loading" :size="40"><Loading /></el-icon>
          </div>
          
          <div v-else-if="newsList.length === 0" class="empty-container">
            <el-empty description="暂无新闻" />
          </div>
          
          <div v-else class="news-list">
            <div 
              v-for="news in newsList" 
              :key="news.id" 
              class="news-item"
              @click="viewDetail(news)"
            >
              <div class="news-content">
                <div class="news-header">
                  <span class="source">{{ news.source }}</span>
                  <span class="time">{{ formatTime(news.publishedAt) }}</span>
                  <el-tag :type="getSentimentType(news.sentimentLabel)" size="small">
                    {{ getSentimentText(news.sentimentLabel) }}
                  </el-tag>
                </div>
                <h3 class="news-title">{{ news.title }}</h3>
                <p class="news-summary">{{ news.summary }}</p>
                <div class="news-footer">
                  <div class="related-coins">
                    <el-tag 
                      v-for="coin in parseCoins(news.relatedCoins)" 
                      :key="coin"
                      size="small"
                      type="info"
                    >
                      {{ coin }}
                    </el-tag>
                  </div>
                  <el-button text size="small">
                    查看详情 →
                  </el-button>
                </div>
              </div>
            </div>
          </div>

          <div class="pagination">
            <el-pagination
              v-model:current-page="pagination.page"
              v-model:page-size="pagination.size"
              :total="pagination.total"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next"
              @change="loadNews"
            />
          </div>
        </el-card>
      </el-col>

      <!-- 情感趋势 -->
      <el-col :span="6">
        <el-card class="trend-card">
          <template #header>
            <span>📈 情感趋势</span>
          </template>
          <v-chart v-if="sentimentTrend.length > 0" :option="trendChartOption" autoresize style="height: 250px;" />
          <el-empty v-else description="暂无数据" :image-size="80" />
        </el-card>
        
        <el-card class="stats-card">
          <template #header>
            <span>📊 情感分布</span>
          </template>
          <div class="sentiment-stats">
            <div class="stat-item">
              <span class="label">利好</span>
              <span class="value positive">{{ sentimentStats.positive }}</span>
            </div>
            <div class="stat-item">
              <span class="label">中性</span>
              <span class="value neutral">{{ sentimentStats.neutral }}</span>
            </div>
            <div class="stat-item">
              <span class="label">利空</span>
              <span class="value negative">{{ sentimentStats.negative }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart } from 'echarts/charts'
import { GridComponent, TooltipComponent } from 'echarts/components'
import { newsApi } from '@/api'
import { Loading } from '@element-plus/icons-vue'
import dayjs from 'dayjs'

use([CanvasRenderer, LineChart, GridComponent, TooltipComponent])

const filter = reactive({
  sentiment: '',
  coin: ''
})

const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

const loading = ref(false)
const collecting = ref(false)
const newsList = ref<any[]>([])
const sentimentTrend = ref<any[]>([])
const sentimentStats = reactive({
  positive: 0,
  neutral: 0,
  negative: 0
})

// 趋势图表配置
const trendChartOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  xAxis: {
    type: 'category',
    data: sentimentTrend.value.map(t => t.date),
    axisLine: { lineStyle: { color: '#3a3a5a' } },
    axisLabel: { color: '#a0a0a0', fontSize: 10 }
  },
  yAxis: {
    type: 'value',
    min: -1,
    max: 1,
    axisLine: { lineStyle: { color: '#3a3a5a' } },
    axisLabel: { color: '#a0a0a0' },
    splitLine: { lineStyle: { color: '#2a2a4a' } }
  },
  series: [{
    data: sentimentTrend.value.map(t => t.avgSentiment),
    type: 'line',
    smooth: true,
    lineStyle: { color: '#00d9ff', width: 2 },
    areaStyle: {
      color: {
        type: 'linear',
        x: 0, y: 0, x2: 0, y2: 1,
        colorStops: [
          { offset: 0, color: 'rgba(0, 217, 255, 0.3)' },
          { offset: 1, color: 'rgba(0, 217, 255, 0.05)' }
        ]
      }
    }
  }]
}))

// 加载新闻列表
const loadNews = async () => {
  loading.value = true
  try {
    const res = await newsApi.getList(
      pagination.page, 
      pagination.size, 
      filter.sentiment, 
      filter.coin
    ) as any
    
    if (res.data) {
      newsList.value = res.data.list
      pagination.total = res.data.total
    }
  } catch (e) {
    console.error('加载新闻失败', e)
  } finally {
    loading.value = false
  }
}

// 加载情感趋势
const loadSentimentTrend = async () => {
  try {
    const res = await newsApi.getSentimentTrend(7) as any
    if (res.data) {
      sentimentTrend.value = res.data
    }
  } catch (e) {
    console.error('加载趋势失败', e)
  }
}

// 手动采集
const triggerCollect = async () => {
  collecting.value = true
  try {
    const res = await newsApi.collect() as any
    ElMessage.success(`采集完成，共采集 ${res.data?.collected || 0} 条新闻`)
    await loadNews()
  } catch (e) {
    console.error('采集失败', e)
  } finally {
    collecting.value = false
  }
}

// 导入 ElMessage
import { ElMessage } from 'element-plus'

const search = () => {
  pagination.page = 1
  loadNews()
}

const resetFilter = () => {
  filter.sentiment = ''
  filter.coin = ''
  search()
}

const viewDetail = (news: any) => {
  window.open(news.url, '_blank')
}

const getSentimentType = (label: string) => {
  if (label === 'POSITIVE') return 'success'
  if (label === 'NEGATIVE') return 'danger'
  return 'info'
}

const getSentimentText = (label: string) => {
  if (label === 'POSITIVE') return '利好'
  if (label === 'NEGATIVE') return '利空'
  return '中性'
}

const formatTime = (time: string) => {
  return dayjs(time).format('MM-DD HH:mm')
}

const parseCoins = (coins: any) => {
  if (!coins) return []
  if (Array.isArray(coins)) return coins
  if (typeof coins === 'string') {
    try {
      return JSON.parse(coins)
    } catch {
      return []
    }
  }
  return []
}

onMounted(() => {
  loadNews()
  loadSentimentTrend()
})
</script>

<style scoped>
.filter-card {
  margin-bottom: 20px;
}

.loading-container, .empty-container {
  display: flex;
  justify-content: center;
  padding: 40px;
}

.news-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.news-item {
  padding: 20px;
  background: #0f0f1a;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.news-item:hover {
  background: #1a1a2e;
  transform: translateX(4px);
}

.news-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
  font-size: 13px;
  color: #a0a0a0;
}

.news-title {
  font-size: 16px;
  font-weight: 500;
  color: #e0e0e0;
  margin-bottom: 8px;
  line-height: 1.4;
}

.news-summary {
  font-size: 14px;
  color: #a0a0a0;
  line-height: 1.6;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.news-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.related-coins {
  display: flex;
  gap: 8px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #2a2a4a;
}

.trend-card, .stats-card {
  margin-bottom: 20px;
}

.sentiment-stats {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background: #0f0f1a;
  border-radius: 6px;
}

.stat-item .label {
  color: #a0a0a0;
}

.stat-item .value {
  font-weight: 600;
}

.positive { color: #00ff88; }
.neutral { color: #a0a0a0; }
.negative { color: #ff4757; }
</style>