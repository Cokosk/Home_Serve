import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Dashboard',
    component: () => import('@/views/Dashboard.vue'),
    meta: { title: '市场概览' }
  },
  {
    path: '/heatmap',
    name: 'Heatmap',
    component: () => import('@/views/Heatmap.vue'),
    meta: { title: '涨跌热力图' }
  },
  {
    path: '/news',
    name: 'News',
    component: () => import('@/views/News.vue'),
    meta: { title: '新闻中心' }
  },
  {
    path: '/analysis',
    name: 'Analysis',
    component: () => import('@/views/Analysis.vue'),
    meta: { title: 'AI 分析' }
  },
  {
    path: '/settings',
    name: 'Settings',
    component: () => import('@/views/Settings.vue'),
    meta: { title: '设置' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router