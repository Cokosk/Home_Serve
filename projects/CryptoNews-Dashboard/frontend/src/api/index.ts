import axios from 'axios'
import { ElMessage } from 'element-plus'

const api = axios.create({
  baseURL: '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 响应拦截器
api.interceptors.response.use(
  (response) => {
    const { data } = response
    if (data.code !== 200) {
      ElMessage.error(data.message || '请求失败')
      return Promise.reject(new Error(data.message))
    }
    return data
  },
  (error) => {
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

// 加密货币相关接口
export const cryptoApi = {
  // 获取热力图数据
  getHeatmap: (limit = 50) => api.get('/crypto/heatmap', { params: { limit } }),
  
  // 获取币种详情
  getDetail: (symbol: string) => api.get(`/crypto/${symbol}`),
  
  // 获取历史价格
  getHistory: (symbol: string, days = 7) => api.get(`/crypto/${symbol}/history`, { params: { days } }),
  
  // 刷新价格
  refresh: () => api.post('/crypto/refresh'),
  
  // 市场概览
  getOverview: () => api.get('/crypto/market/overview')
}

// 新闻相关接口
export const newsApi = {
  // 获取新闻列表
  getList: (page = 1, size = 20, sentiment?: string, coin?: string) => 
    api.get('/news/list', { params: { page, size, sentiment, coin } }),
  
  // 获取新闻详情
  getDetail: (id: number) => api.get(`/news/${id}`),
  
  // 触发采集
  collect: () => api.post('/news/collect'),
  
  // 情感趋势
  getSentimentTrend: (days = 7) => api.get('/news/sentiment/trend', { params: { days } })
}

// AI 分析接口
export const aiApi = {
  // 分析情感
  analyzeSentiment: (text: string) => api.post('/ai/analyze/sentiment', { text }),
  
  // 投资建议
  getAdvice: (symbol: string) => api.post('/ai/advice', { symbol }),
  
  // 市场分析
  getMarketAnalysis: () => api.get('/ai/analysis/market'),
  
  // 今日摘要
  getTodayDigest: () => api.get('/ai/digest/today')
}

export default api