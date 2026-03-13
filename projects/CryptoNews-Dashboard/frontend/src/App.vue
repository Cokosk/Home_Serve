<template>
  <div class="app-container">
    <el-container>
      <!-- 侧边栏 -->
      <el-aside width="220px" class="sidebar">
        <div class="logo">
          <span class="logo-icon">📊</span>
          <span class="logo-text">CryptoNews</span>
        </div>
        <el-menu
          :default-active="activeMenu"
          router
          background-color="#1a1a2e"
          text-color="#a0a0a0"
          active-text-color="#00d9ff"
        >
          <el-menu-item index="/">
            <el-icon><DataLine /></el-icon>
            <span>市场概览</span>
          </el-menu-item>
          <el-menu-item index="/heatmap">
            <el-icon><Grid /></el-icon>
            <span>热力图</span>
          </el-menu-item>
          <el-menu-item index="/news">
            <el-icon><Document /></el-icon>
            <span>新闻中心</span>
          </el-menu-item>
          <el-menu-item index="/analysis">
            <el-icon><TrendCharts /></el-icon>
            <span>AI 分析</span>
          </el-menu-item>
          <el-menu-item index="/settings">
            <el-icon><Setting /></el-icon>
            <span>设置</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 主内容区 -->
      <el-container>
        <el-header class="header">
          <div class="header-left">
            <h2>{{ pageTitle }}</h2>
          </div>
          <div class="header-right">
            <el-button @click="refreshData" :loading="loading" circle>
              <el-icon><Refresh /></el-icon>
            </el-button>
            <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="bell-badge">
              <el-button circle>
                <el-icon><Bell /></el-icon>
              </el-button>
            </el-badge>
          </div>
        </el-header>
        <el-main class="main">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const loading = ref(false)
const unreadCount = ref(0)

const activeMenu = computed(() => route.path)
const pageTitle = computed(() => {
  const titles: Record<string, string> = {
    '/': '市场概览',
    '/heatmap': '涨跌热力图',
    '/news': '新闻中心',
    '/analysis': 'AI 投资分析',
    '/settings': '系统设置'
  }
  return titles[route.path] || 'CryptoNews Dashboard'
})

const refreshData = async () => {
  loading.value = true
  // TODO: 触发数据刷新
  setTimeout(() => {
    loading.value = false
  }, 1000)
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
  background: #0f0f1a;
  color: #e0e0e0;
}

.app-container {
  height: 100vh;
}

.sidebar {
  background: #1a1a2e;
  border-right: 1px solid #2a2a4a;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border-bottom: 1px solid #2a2a4a;
}

.logo-icon {
  font-size: 24px;
}

.logo-text {
  font-size: 18px;
  font-weight: 600;
  background: linear-gradient(135deg, #00d9ff, #00ff88);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.el-menu {
  border-right: none !important;
}

.el-menu-item {
  margin: 4px 8px;
  border-radius: 8px;
}

.el-menu-item:hover {
  background: #2a2a4a !important;
}

.el-menu-item.is-active {
  background: linear-gradient(135deg, rgba(0, 217, 255, 0.1), rgba(0, 255, 136, 0.1)) !important;
}

.header {
  background: #1a1a2e;
  border-bottom: 1px solid #2a2a4a;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.header-left h2 {
  font-size: 18px;
  font-weight: 500;
  color: #e0e0e0;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.bell-badge {
  margin-left: 8px;
}

.main {
  background: #0f0f1a;
  padding: 20px;
}

/* Element Plus 暗色主题覆盖 */
.el-button {
  background: #2a2a4a;
  border-color: #3a3a5a;
  color: #e0e0e0;
}

.el-button:hover {
  background: #3a3a5a;
  border-color: #4a4a6a;
}

.el-card {
  background: #1a1a2e;
  border-color: #2a2a4a;
}
</style>