<template>
  <div class="home">
    <!-- 顶部搜索栏 -->
    <div class="search-bar">
      <van-search 
        v-model="searchText" 
        placeholder="搜索服务" 
        shape="round"
        @search="handleSearch"
        @focus="goToSearch"
      >
        <template #left-icon>
          <van-icon name="search" color="#FF6B35" />
        </template>
      </van-search>
    </div>

    <!-- 轮播图 -->
    <div class="banner-section">
      <van-swipe class="banner-swipe" :autoplay="3000" indicator-color="white">
        <van-swipe-item v-for="banner in banners" :key="banner.id">
          <div class="banner-item" :style="{ background: banner.bg }">
            <div class="banner-content">
              <div class="banner-title">{{ banner.title }}</div>
              <div class="banner-desc">{{ banner.desc }}</div>
              <div class="banner-tag" v-if="banner.tag">{{ banner.tag }}</div>
            </div>
            <div class="banner-decoration">
              <div class="deco-circle"></div>
              <div class="deco-circle small"></div>
            </div>
          </div>
        </van-swipe-item>
      </van-swipe>
    </div>

    <!-- 服务分类 -->
    <div class="category-section">
      <div class="section-header">
        <span class="section-title">🦞 服务分类</span>
      </div>
      <!-- 分类骨架屏 -->
      <div class="category-list" v-if="categoriesLoading">
        <SkeletonLoader type="category" v-for="i in 5" :key="i" />
      </div>
      <div class="category-list" v-else>
        <div 
          class="category-item" 
          v-for="category in categories" 
          :key="category.id"
          @click="goToServices(category.id)"
        >
          <div class="category-icon">
            {{ getCategoryEmoji(category.icon) }}
          </div>
          <div class="category-name">{{ category.name }}</div>
        </div>
      </div>
    </div>

    <!-- 热门服务 -->
    <div class="hot-section">
      <div class="section-header">
        <span class="section-title">🔥 热门服务</span>
        <span class="more" @click="goToServices()">
          查看全部
          <van-icon name="arrow" />
        </span>
      </div>
      
      <!-- 服务骨架屏 -->
      <div class="service-list" v-if="servicesLoading">
        <SkeletonLoader type="service-card" v-for="i in 3" :key="i" />
      </div>
      
      <div v-else>
        <van-pull-refresh v-model="refreshing" @refresh="onRefresh" success-text="刷新成功">
          <van-list
            v-model:loading="loading"
            :finished="finished"
            finished-text=""
            @load="loadMore"
          >
            <div 
              class="service-card" 
              v-for="service in hotServices" 
              :key="service.id"
              @click="goToServiceDetail(service.id)"
            >
              <div class="service-image">
                <div class="service-icon">{{ getServiceIcon(service) }}</div>
                <div class="service-badge" v-if="service.badge">{{ service.badge }}</div>
              </div>
              <div class="service-info">
                <div class="service-name">{{ service.name }}</div>
                <div class="service-desc">{{ service.description || '专业服务，品质保障' }}</div>
                <div class="service-tags" v-if="service.tags && service.tags.length">
                  <span class="tag" v-for="tag in service.tags.slice(0, 2)" :key="tag">{{ tag }}</span>
                </div>
                <div class="service-meta">
                  <div class="service-price">
                    <span class="price-symbol">¥</span>
                    <span class="price-value">{{ service.price }}</span>
                    <span class="price-unit">/次</span>
                  </div>
                  <div class="service-sales">
                    <van-icon name="fire-o" color="#FF6B35" />
                    已售{{ service.sales || 100 }}+
                  </div>
                </div>
              </div>
            </div>
            <EmptyState 
              v-if="hotServices.length === 0 && !servicesLoading" 
              type="service" 
              title="暂无热门服务"
              description="下拉刷新试试"
            />
          </van-list>
        </van-pull-refresh>
      </div>
    </div>

    <!-- 底部导航 -->
    <van-tabbar v-model="activeTab" active-color="#FF6B35" inactive-color="#999">
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
import { showToast } from 'vant'
import SkeletonLoader from '../components/SkeletonLoader.vue'
import EmptyState from '../components/EmptyState.vue'

const router = useRouter()
const searchText = ref('')
const activeTab = ref(0)
const categories = ref([])
const hotServices = ref([])
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)
const categoriesLoading = ref(true)
const servicesLoading = ref(true)
const page = ref(1)

// 轮播图数据
const banners = ref([
  { id: 1, title: '新人专享', desc: '首单立减20元', tag: '限时', bg: 'linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%)' },
  { id: 2, title: '品质保障', desc: '专业服务人员', tag: '认证', bg: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
  { id: 3, title: '限时特惠', desc: '保洁服务8折起', tag: '优惠', bg: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
  { id: 4, title: '春季大促', desc: '家电清洗5折', tag: '热门', bg: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' }
])

// 分类图标映射
const categoryIconMap = {
  cleaning: { emoji: '🧹', label: '保洁' },
  appliance: { emoji: '🔧', label: '家电' },
  nanny: { emoji: '👶', label: '月嫂' },
  repair: { emoji: '🛠️', label: '维修' },
  move: { emoji: '🚚', label: '搬家' },
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
  categoriesLoading.value = true
  try {
    const res = await serviceApi.getCategory({ showLoading: false })
    if (res.code === 200) {
      categories.value = res.data || []
    }
  } catch (error) {
    console.error('加载分类失败:', error)
  } finally {
    categoriesLoading.value = false
  }
}

// 加载热门服务
const loadHotServices = async () => {
  servicesLoading.value = true
  try {
    const res = await serviceApi.getHot({ showLoading: false })
    if (res.code === 200) {
      hotServices.value = res.data || []
    }
  } catch (error) {
    console.error('加载服务失败:', error)
  } finally {
    servicesLoading.value = false
  }
}

// 加载更多
const loadMore = async () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
    finished.value = true
  }, 500)
}

// 下拉刷新
const onRefresh = async () => {
  await Promise.all([loadCategories(), loadHotServices()])
  refreshing.value = false
  showToast('刷新成功')
}

// 搜索
const handleSearch = () => {
  if (searchText.value.trim()) {
    router.push({ path: '/services', query: { keyword: searchText.value } })
  }
}

// 跳转搜索页
const goToSearch = () => {
  router.push('/search')
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
  background: #f5f5f5;
  min-height: 100vh;
}

.search-bar {
  padding: 10px 12px;
  background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%);
}

.search-bar :deep(.van-search__content) {
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

/* 轮播图 */
.banner-section {
  padding: 0 12px 12px;
  background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%);
}

.banner-swipe {
  height: 140px;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.banner-item {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  position: relative;
  overflow: hidden;
}

.banner-content {
  z-index: 2;
}

.banner-title {
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 8px;
  color: #fff;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.banner-desc {
  font-size: 14px;
  opacity: 0.95;
  color: #fff;
}

.banner-tag {
  display: inline-block;
  margin-top: 10px;
  padding: 4px 12px;
  background: rgba(255, 255, 255, 0.25);
  border-radius: 20px;
  font-size: 12px;
  color: #fff;
  backdrop-filter: blur(4px);
}

.banner-decoration {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
}

.deco-circle {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.15);
  position: absolute;
  right: 0;
  top: -40px;
}

.deco-circle.small {
  width: 40px;
  height: 40px;
  right: 60px;
  top: 10px;
}

/* 分类 */
.category-section {
  background: #fff;
  margin: 12px;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 12px 8px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.more {
  font-size: 12px;
  color: #999;
  font-weight: 400;
  display: flex;
  align-items: center;
  gap: 2px;
}

.category-list {
  display: flex;
  overflow-x: auto;
  padding: 0 8px 14px;
  gap: 4px;
}

.category-list::-webkit-scrollbar {
  display: none;
}

.category-item {
  flex-shrink: 0;
  width: 68px;
  text-align: center;
  padding: 10px 4px;
  border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
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
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.15);
  transition: transform 0.3s;
}

.category-item:active .category-icon {
  transform: scale(0.9);
}

.category-name {
  font-size: 12px;
  color: #333;
  line-height: 1.4;
  font-weight: 500;
}

/* 热门服务 */
.hot-section {
  background: #fff;
  margin: 12px;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.service-list {
  padding: 0 12px 12px;
}

.service-card {
  display: flex;
  background: #fafafa;
  border-radius: 12px;
  margin-bottom: 10px;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
}

.service-card:active {
  transform: scale(0.98);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.service-image {
  width: 90px;
  height: 90px;
  flex-shrink: 0;
  background: linear-gradient(135deg, #FFE5E5 0%, #FFE8DC 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.service-icon {
  font-size: 36px;
}

.service-badge {
  position: absolute;
  top: 6px;
  left: 6px;
  padding: 2px 6px;
  background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%);
  color: #fff;
  font-size: 10px;
  border-radius: 6px;
  font-weight: 500;
}

.service-info {
  flex: 1;
  padding: 10px 12px;
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
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.service-tags {
  display: flex;
  gap: 4px;
  margin: 4px 0;
}

.tag {
  font-size: 10px;
  padding: 2px 6px;
  background: #FFF5F5;
  color: #FF6B35;
  border-radius: 4px;
}

.service-meta {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
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
  font-size: 10px;
  color: #999;
  margin-left: 2px;
}

.service-sales {
  font-size: 11px;
  color: #999;
  display: flex;
  align-items: center;
  gap: 2px;
}
</style>
