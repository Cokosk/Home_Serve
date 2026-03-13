import axios from 'axios'
import { showToast, showLoadingToast, closeToast } from 'vant'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求计数器
let requestCount = 0

const showLoading = () => {
  if (requestCount === 0) {
    showLoadingToast({ message: '加载中...', forbidClick: true, duration: 0 })
  }
  requestCount++
}

const hideLoading = () => {
  requestCount--
  if (requestCount <= 0) {
    requestCount = 0
    closeToast()
  }
}

// 请求拦截器
request.interceptors.request.use(
  config => {
    if (config.showLoading !== false) {
      showLoading()
    }
    const token = localStorage.getItem('worker_token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    hideLoading()
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    hideLoading()
    const res = response.data
    
    if (res.code && res.code !== 200) {
      if (response.config.showError !== false) {
        showToast({ message: res.message || '请求失败', position: 'top' })
      }
      if (res.code === 401) {
        localStorage.removeItem('worker_token')
        window.location.href = '/login'
      }
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  error => {
    hideLoading()
    let message = '网络错误，请稍后重试'
    if (error.response) {
      switch (error.response.status) {
        case 401:
          message = '登录已过期'
          localStorage.removeItem('worker_token')
          window.location.href = '/login'
          break
        case 500:
          message = '服务器错误'
          break
      }
    }
    if (error.config?.showError !== false) {
      showToast({ message, position: 'top' })
    }
    return Promise.reject(error)
  }
)

export default request
