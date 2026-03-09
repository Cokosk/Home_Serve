<template>
  <div class="home">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <van-search v-model="searchText" placeholder="搜索服务" />
    </div>

    <!-- 服务分类 -->
    <div class="category-section">
      <div class="section-title">🦞 服务分类</div>
      <div class="category-list">
        <div 
          class="category-item" 
          v-for="category in categories" 
          :key="category.id"
          @click="goToServices(category.id)"
        >
          <div class="category-icon" :class="'icon-' + category.icon">
            {{ getCategoryEmoji(category.icon) }}
          </div>
          <div class="category-name">{{ category.name }}</div>
        </div>
      </div>
    </div>

    <!-- 热门服务 -->
    <div class="hot-section">
      <div class="section-title">🔥 热门服务</div>
      <div class="service-list">
        <div 
          class="service-card" 
          v-for="service in hotServices" 
          :key="service.id"
          @click="goToServiceDetail(service.id)"
        >
          <div class="service-image">
            <div class="service-icon">{{ getServiceIcon(service) }}</div>
          </div>
          <div class="service-info">
            <div class="service-name">{{ service.name }}</div>
            <div class="service-desc">{{ service.description || '专业服务，品质保障' }}</div>
            <div class="service-price">
              <span class="price-label">🦞</span>
              <span class="price-value">¥{{ service.price }}</span>
              <span class="price-unit">/次</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部导航 -->
    <van-tabbar v-model="activeTab">
      <van-tabbar-item icon="home-o" to="/">首页</van-tabbar-item>
      <van-tabbar-item icon="apps-o" to="/services">服务</van-tabbar-item>
      <van-tabbar-item icon="orders-o" to="/orders">订单</van-tabbar-item>
      <van-tabbar-item icon="user-o" to="/user">我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { serviceApi } from '../api/service'

const router = useRouter()
const searchText = ref('')
const activeTab = ref(0)
const categories = ref([])
const hotServices = ref([])

// 分类图标映射（融入小龙虾元素）
const categoryIconMap = {
  cleaning: { emoji: '🧹', label: '保洁' },
  appliance: { emoji: '🔧', label: '家电' },
  nanny: { emoji: '👶', label: '月嫂' },
  repair: { emoji: '🛠️', label: '维修' },
  default: { emoji: '📋', label: '服务' }
}

// 获取分类 emoji
const getCategoryEmoji = (icon) => {
  return categoryIconMap[icon]?.emoji || categoryIconMap.default.emoji
}

// 获取服务图标
const getServiceIcon = (service) => {
  return getCategoryEmoji(service.icon) || '🦞'
}

// 加载分类
const loadCategories = async () => {
  const res = await serviceApi.getCategory()
  if (res.code === 200) {
    categories.value = res.data
  }
}

// 加载热门服务
const loadHotServices = async () => {
  const res = await serviceApi.getHot()
  if (res.code === 200) {
    hotServices.value = res.data
  }
}

// 跳转到服务列表
const goToServices = (categoryId) => {
  router.push({ path: '/services', query: { categoryId } })
}

// 跳转到服务详情
const goToServiceDetail = (serviceId) => {
  router.push({ path: `/service/${serviceId}` })
}

onMounted(() => {
  loadCategories()
  loadHotServices()
})
</script>

<style scoped>
.home {
  padding-bottom: 60px;
  background: #f8f8f8;
}

.search-bar {
  padding: 10px 12px;
  background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%);
}

.search-bar :deep(.van-search) {
  padding: 0;
}

.search-bar :deep(.van-search__content) {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  padding: 16px 12px 12px;
  background: #fff;
  color: #333;
  display: flex;
  align-items: center;
  gap: 6px;
}

.category-section {
  background: #fff;
  margin-bottom: 10px;
}

.category-list {
  display: flex;
  overflow-x: auto;
  padding: 8px 8px 16px;
  background: #fff;
  -webkit-overflow-scrolling: touch;
  gap: 4px;
}

.category-list::-webkit-scrollbar {
  display: none;
}

.category-item {
  flex-shrink: 0;
  width: 72px;
  text-align: center;
  padding: 12px 4px;
  cursor: pointer;
  border-radius: 12px;
  transition: all 0.2s ease;
}

.category-item:active {
  background: #f5f5f5;
  transform: scale(0.95);
}

.category-icon {
  width: 48px;
  height: 48px;
  margin: 0 auto 8px;
  background: linear-gradient(135deg, #FFF5F5 0%, #FFF0E8 100%);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  box-shadow: 0 2px 8px rgba(255, 107, 107, 0.15);
}

.category-name {
  font-size: 12px;
  color: #333;
  line-height: 1.4;
  font-weight: 500;
}

.hot-section {
  background: #f8f8f8;
}

.service-list {
  padding: 8px 12px;
}

.service-card {
  display: flex;
  background: #fff;
  border-radius: 12px;
  margin-bottom: 10px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.service-image {
  width: 90px;
  height: 90px;
  flex-shrink: 0;
  background: linear-gradient(135deg, #FFE5E5 0%, #FFE8DC 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.service-icon {
  font-size: 36px;
}

.service-info {
  flex: 1;
  padding: 12px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.service-name {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  line-height: 1.4;
}

.service-desc {
  font-size: 12px;
  color: #999;
  margin: 4px 0;
}

.service-price {
  display: flex;
  align-items: baseline;
  gap: 2px;
}

.price-label {
  font-size: 14px;
}

.price-value {
  font-size: 18px;
  color: #FF6B35;
  font-weight: 700;
}

.price-unit {
  font-size: 12px;
  color: #999;
}
</style>