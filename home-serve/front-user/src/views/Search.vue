<template>
  <div class="search-page">
    <!-- 搜索栏 -->
    <div class="search-header">
      <van-search 
        v-model="keyword" 
        placeholder="搜索服务" 
        shape="round"
        show-action
        autofocus
        @search="handleSearch"
        @cancel="goBack"
      />
    </div>

    <!-- 搜索历史 -->
    <div class="search-history" v-if="!keyword && historyList.length">
      <div class="section-header">
        <span class="section-title">搜索历史</span>
        <van-icon name="delete-o" @click="clearHistory" color="#999" />
      </div>
      <div class="history-tags">
        <van-tag 
          v-for="(item, index) in historyList" 
          :key="index"
          size="medium"
          type="primary"
          plain
          @click="searchByHistory(item)"
        >
          {{ item }}
        </van-tag>
      </div>
    </div>

    <!-- 热门搜索 -->
    <div class="hot-search" v-if="!keyword">
      <div class="section-header">
        <span class="section-title">🔥 热门搜索</span>
      </div>
      <div class="hot-list">
        <div 
          class="hot-item" 
          v-for="(item, index) in hotList" 
          :key="index"
          @click="searchByHot(item)"
        >
          <span class="hot-rank" :class="{ top: index < 3 }">{{ index + 1 }}</span>
          <span class="hot-text">{{ item }}</span>
          <span class="hot-badge" v-if="index < 3">热</span>
        </div>
      </div>
    </div>

    <!-- 搜索结果 -->
    <div class="search-results" v-if="keyword && services.length">
      <div class="result-count">找到 {{ services.length }} 个相关服务</div>
      <div class="service-list">
        <div 
          class="service-card" 
          v-for="service in services" 
          :key="service.id"
          @click="goToDetail(service.id)"
        >
          <div class="service-icon">{{ getServiceIcon(service.categoryId) }}</div>
          <div class="service-info">
            <div class="service-name" v-html="highlightKeyword(service.name)"></div>
            <div class="service-desc">{{ service.description }}</div>
            <div class="service-price">
              <span class="price-symbol">¥</span>
              <span class="price-value">{{ service.price }}</span>
              <span class="price-unit">/次</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 无结果 -->
    <div class="no-result" v-if="keyword && searched && services.length === 0">
      <div class="no-result-icon">🔍</div>
      <div class="no-result-text">未找到"{{ keyword }}"相关服务</div>
      <div class="no-result-tip">换个关键词试试</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { serviceApi } from '../api/service'

const router = useRouter()
const route = useRoute()

const keyword = ref('')
const services = ref([])
const historyList = ref([])
const searched = ref(false)

const hotList = ref([
  '日常保洁',
  '空调清洗',
  '深度保洁',
  '油烟机清洗',
  '月嫂',
  '搬家服务',
  '家电维修',
  '窗帘清洗'
])

// 服务图标
const serviceIcons = {
  1: '🧹',
  2: '🔧',
  3: '👶',
  4: '🛠️'
}

const getServiceIcon = (categoryId) => serviceIcons[categoryId] || '📋'

// 高亮关键词
const highlightKeyword = (text) => {
  if (!keyword.value) return text
  const reg = new RegExp(keyword.value, 'gi')
  return text.replace(reg, '<span class="highlight">$&</span>')
}

// 搜索
const handleSearch = async () => {
  if (!keyword.value.trim()) return
  
  // 保存搜索历史
  saveHistory(keyword.value)
  
  searched.value = true
  try {
    const res = await serviceApi.getList({ keyword: keyword.value })
    if (res.code === 200) {
      services.value = res.data || []
    }
  } catch (e) {
    console.error('搜索失败:', e)
  }
}

// 历史搜索
const searchByHistory = (text) => {
  keyword.value = text
  handleSearch()
}

// 热门搜索
const searchByHot = (text) => {
  keyword.value = text
  handleSearch()
}

// 保存历史
const saveHistory = (text) => {
  const list = historyList.value.filter(item => item !== text)
  historyList.value = [text, ...list].slice(0, 10)
  localStorage.setItem('searchHistory', JSON.stringify(historyList.value))
}

// 清除历史
const clearHistory = () => {
  historyList.value = []
  localStorage.removeItem('searchHistory')
}

// 返回
const goBack = () => {
  router.back()
}

// 跳转详情
const goToDetail = (id) => {
  router.push(`/service/${id}`)
}

onMounted(() => {
  const savedHistory = localStorage.getItem('searchHistory')
  if (savedHistory) {
    historyList.value = JSON.parse(savedHistory)
  }
  
  if (route.query.keyword) {
    keyword.value = route.query.keyword
    handleSearch()
  }
})
</script>

<style scoped>
.search-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.search-header {
  background: #fff;
  padding-top: 10px;
}

.search-history,
.hot-search {
  background: #fff;
  margin-top: 10px;
  padding: 14px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.section-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.history-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.history-tags :deep(.van-tag) {
  cursor: pointer;
}

.hot-list {
  display: flex;
  flex-direction: column;
}

.hot-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
}

.hot-item:last-child {
  border-bottom: none;
}

.hot-rank {
  width: 20px;
  height: 20px;
  border-radius: 4px;
  background: #f0f0f0;
  color: #999;
  font-size: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.hot-rank.top {
  background: linear-gradient(135deg, #FF6B6B, #FF8E53);
  color: #fff;
}

.hot-text {
  flex: 1;
  font-size: 14px;
  color: #333;
}

.hot-badge {
  font-size: 10px;
  padding: 2px 6px;
  background: #FFF0E8;
  color: #FF6B35;
  border-radius: 4px;
}

/* 搜索结果 */
.search-results {
  padding: 12px;
}

.result-count {
  font-size: 13px;
  color: #999;
  margin-bottom: 12px;
}

.service-card {
  display: flex;
  background: #fff;
  border-radius: 12px;
  padding: 12px;
  margin-bottom: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.service-icon {
  width: 70px;
  height: 70px;
  background: linear-gradient(135deg, #FFE5E5, #FFE8DC);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  flex-shrink: 0;
}

.service-info {
  flex: 1;
  padding-left: 12px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.service-name {
  font-size: 15px;
  font-weight: 600;
  color: #333;
}

.service-name :deep(.highlight) {
  color: #FF6B35;
}

.service-desc {
  font-size: 12px;
  color: #999;
}

.service-price {
  display: flex;
  align-items: baseline;
}

.price-symbol {
  font-size: 12px;
  color: #FF6B35;
  font-weight: 600;
}

.price-value {
  font-size: 18px;
  color: #FF6B35;
  font-weight: 700;
}

.price-unit {
  font-size: 11px;
  color: #999;
  margin-left: 2px;
}

/* 无结果 */
.no-result {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
}

.no-result-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

.no-result-text {
  font-size: 16px;
  color: #333;
  margin-bottom: 8px;
}

.no-result-tip {
  font-size: 13px;
  color: #999;
}
</style>
