<template>
  <div class="home">
    <!-- 顶部导航 -->
    <van-nav-bar title="勤家家政 - 服务者端">
      <template #right>
        <van-icon name="bell" :badge="noticeCount || ''" @click="goToNotice" />
      </template>
    </van-nav-bar>

    <!-- 统计卡片 -->
    <div class="stats-section">
      <van-row gutter="12">
        <van-col span="8">
          <div class="stat-card income">
            <div class="stat-icon">💰</div>
            <div class="stat-value">
              <span class="currency">¥</span>{{ stats.todayIncome }}
            </div>
            <div class="stat-label">今日收入</div>
            <div class="stat-trend up" v-if="stats.incomeTrend > 0">
              <van-icon name="arrow-up" /> {{ stats.incomeTrend }}%
            </div>
          </div>
        </van-col>
        <van-col span="8">
          <div class="stat-card pending">
            <div class="stat-icon">⏳</div>
            <div class="stat-value">{{ stats.pendingOrders }}</div>
            <div class="stat-label">待处理</div>
          </div>
        </van-col>
        <van-col span="8">
          <div class="stat-card completed">
            <div class="stat-icon">✅</div>
            <div class="stat-value">{{ stats.completedOrders }}</div>
            <div class="stat-label">已完成</div>
          </div>
        </van-col>
      </van-row>
    </div>

    <!-- 抢单池提醒 -->
    <transition name="slide">
      <div class="grab-notice" v-if="grabCount > 0" @click="goToGrab">
        <div class="notice-content">
          <div class="notice-icon-wrapper">
            <span class="notice-icon">🔔</span>
            <span class="pulse-ring"></span>
          </div>
          <div class="notice-text">
            <span class="main-text">有新订单可抢</span>
            <span class="sub-text">当前 {{ grabCount }} 个订单待抢单</span>
          </div>
        </div>
        <van-icon name="arrow" class="notice-arrow" />
      </div>
    </transition>

    <!-- 快捷入口 -->
    <div class="quick-actions">
      <div class="section-title">
        <span>快捷入口</span>
      </div>
      <div class="action-grid">
        <div class="action-item" @click="goToGrab">
          <div class="action-icon grab">
            <van-icon name="shopping-cart-o" />
          </div>
          <span class="action-text">抢单池</span>
          <span class="action-badge" v-if="grabCount">{{ grabCount }}</span>
        </div>
        <div class="action-item" @click="goToOrders">
          <div class="action-icon order">
            <van-icon name="orders-o" />
          </div>
          <span class="action-text">我的订单</span>
        </div>
        <div class="action-item" @click="goToIncome">
          <div class="action-icon income">
            <van-icon name="balance-o" />
          </div>
          <span class="action-text">收入统计</span>
        </div>
        <div class="action-item" @click="goToAttendance">
          <div class="action-icon attendance">
            <van-icon name="clock-o" />
          </div>
          <span class="action-text">考勤打卡</span>
        </div>
      </div>
    </div>

    <!-- 今日任务 -->
    <div class="today-tasks">
      <div class="section-header">
        <span class="section-title">今日任务</span>
        <span class="task-count" v-if="todayOrders.length">{{ todayOrders.length }} 个任务</span>
      </div>
      
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh" success-text="刷新成功">
        <van-list
          v-model:loading="loading"
          :finished="finished"
          finished-text=""
          @load="loadMore"
        >
          <div 
            class="task-card" 
            v-for="order in todayOrders" 
            :key="order.id"
            @click="goToOrder(order.id)"
          >
            <div class="task-header">
              <div class="task-service">
                <span class="service-icon">{{ getServiceIcon(order.serviceId) }}</span>
                <span class="task-type">{{ order.serviceName }}</span>
              </div>
              <span class="task-status" :class="getStatusClass(order.status)">
                {{ getStatusText(order.status) }}
              </span>
            </div>
            <div class="task-info">
              <div class="info-item">
                <van-icon name="location-o" class="info-icon" />
                <span>{{ order.address }}</span>
              </div>
              <div class="info-item">
                <van-icon name="clock-o" class="info-icon" />
                <span>{{ order.appointmentTime }}</span>
              </div>
            </div>
            <div class="task-footer">
              <div class="task-price">¥{{ order.price }}</div>
              <van-button 
                v-if="order.status === 1" 
                size="small" 
                type="primary"
                @click.stop="startService(order)"
              >
                开始服务
              </van-button>
            </div>
          </div>
          
          <van-empty v-if="todayOrders.length === 0 && !loading" description="今日暂无任务">
            <van-button type="primary" size="small" @click="goToGrab">去抢单</van-button>
          </van-empty>
        </van-list>
      </van-pull-refresh>
    </div>

    <!-- 底部导航 -->
    <van-tabbar v-model="activeTab" route active-color="#667eea">
      <van-tabbar-item icon="home-o" to="/">首页</van-tabbar-item>
      <van-tabbar-item icon="shopping-cart-o" to="/grab" :badge="grabCount || ''">抢单</van-tabbar-item>
      <van-tabbar-item icon="orders-o" to="/orders">订单</van-tabbar-item>
      <van-tabbar-item icon="user-o" to="/user">我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { orderApi } from '../api/order'
import { showToast } from 'vant'

const router = useRouter()
const activeTab = ref(0)
const grabCount = ref(0)
const noticeCount = ref(0)
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)
const todayOrders = ref([])

const stats = ref({
  todayIncome: 0,
  pendingOrders: 0,
  completedOrders: 0,
  incomeTrend: 0
})

// 服务图标
const serviceIcons = {
  1: '🧹',
  2: '🔧',
  3: '👶',
  4: '🛠️'
}

const getServiceIcon = (serviceId) => serviceIcons[serviceId] || '📋'

// 加载抢单池
const loadGrabPool = async () => {
  try {
    const res = await orderApi.getGrabPool({ showLoading: false })
    if (res.code === 200) {
      grabCount.value = res.data?.length || 0
    }
  } catch (error) {
    console.error('加载抢单池失败:', error)
  }
}

// 加载统计数据
const loadStats = async () => {
  stats.value = {
    todayIncome: 328,
    pendingOrders: 3,
    completedOrders: 5,
    incomeTrend: 12.5
  }
}

// 加载今日任务
const loadTodayOrders = async () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
    finished.value = true
  }, 500)
}

// 下拉刷新
const onRefresh = async () => {
  await Promise.all([loadGrabPool(), loadStats(), loadTodayOrders()])
  refreshing.value = false
  showToast('刷新成功')
}

// 加载更多
const loadMore = () => {
  loading.value = false
  finished.value = true
}

// 获取状态样式
const getStatusClass = (status) => {
  const classes = {
    0: 'pending',
    1: 'processing',
    2: 'completed'
  }
  return classes[status] || 'pending'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    0: '待处理',
    1: '进行中',
    2: '已完成'
  }
  return texts[status] || '未知'
}

// 跳转到抢单
const goToGrab = () => router.push('/grab')

// 跳转到订单
const goToOrders = () => router.push('/orders')

// 跳转到收入
const goToIncome = () => router.push('/income')

// 跳转到考勤
const goToAttendance = () => router.push('/attendance')

// 跳转到通知
const goToNotice = () => router.push('/notice')

// 跳转到订单详情
const goToOrder = (id) => router.push(`/order/${id}`)

// 开始服务
const startService = (order) => {
  showToast('开始服务')
}

onMounted(() => {
  loadGrabPool()
  loadStats()
  loadTodayOrders()
})
</script>

<style scoped>
.home {
  padding-bottom: 60px;
  background: #f5f5f5;
  min-height: 100vh;
}

/* 统计卡片 */
.stats-section {
  padding: 15px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 14px;
  padding: 14px 10px;
  text-align: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  position: relative;
  overflow: hidden;
}

.stat-icon {
  font-size: 24px;
  margin-bottom: 6px;
}

.stat-value {
  font-size: 22px;
  font-weight: 700;
  color: #333;
}

.stat-value .currency {
  font-size: 14px;
  font-weight: 500;
}

.stat-label {
  font-size: 11px;
  color: #999;
  margin-top: 4px;
}

.stat-trend {
  position: absolute;
  top: 8px;
  right: 8px;
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 2px;
}

.stat-trend.up {
  background: #E8FFE8;
  color: #52C41A;
}

.stat-card.income .stat-value { color: #FF6B35; }
.stat-card.pending .stat-value { color: #FF9500; }
.stat-card.completed .stat-value { color: #34C759; }

/* 抢单提醒 */
.grab-notice {
  margin: 12px;
  padding: 14px 16px;
  background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%);
  border-radius: 14px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #fff;
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.3);
  cursor: pointer;
  transition: transform 0.2s;
}

.grab-notice:active {
  transform: scale(0.98);
}

.notice-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.notice-icon-wrapper {
  position: relative;
}

.notice-icon {
  font-size: 24px;
}

.pulse-ring {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% {
    transform: translate(-50%, -50%) scale(1);
    opacity: 1;
  }
  100% {
    transform: translate(-50%, -50%) scale(1.5);
    opacity: 0;
  }
}

.notice-text {
  display: flex;
  flex-direction: column;
}

.main-text {
  font-size: 15px;
  font-weight: 600;
}

.sub-text {
  font-size: 12px;
  opacity: 0.9;
}

.notice-arrow {
  font-size: 16px;
}

/* 快捷入口 */
.quick-actions {
  background: #fff;
  margin: 0 12px;
  border-radius: 16px;
  padding: 14px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin-bottom: 14px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.task-count {
  font-size: 12px;
  color: #999;
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  position: relative;
}

.action-icon {
  width: 50px;
  height: 50px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  color: #667eea;
  transition: transform 0.2s;
}

.action-item:active .action-icon {
  transform: scale(0.9);
}

.action-icon.grab { background: linear-gradient(135deg, #FFF0E8 0%, #FFE5D9 100%); color: #FF6B35; }
.action-icon.order { background: linear-gradient(135deg, #E8F4FF 0%, #D6F4FF 100%); color: #1890FF; }
.action-icon.income { background: linear-gradient(135deg, #E8FFE8 0%, #D6FFD6 100%); color: #52C41A; }
.action-icon.attendance { background: linear-gradient(135deg, #F0F5FF 0%, #E6EDFF 100%); color: #722ED1; }

.action-text {
  font-size: 12px;
  color: #666;
}

.action-badge {
  position: absolute;
  top: 0;
  right: 12px;
  min-width: 16px;
  height: 16px;
  padding: 0 4px;
  background: #FF4D4F;
  color: #fff;
  font-size: 10px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 今日任务 */
.today-tasks {
  margin: 12px;
}

.task-card {
  background: #fff;
  border-radius: 14px;
  padding: 14px;
  margin-bottom: 10px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: transform 0.2s;
}

.task-card:active {
  transform: scale(0.98);
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.task-service {
  display: flex;
  align-items: center;
  gap: 8px;
}

.service-icon {
  font-size: 20px;
}

.task-type {
  font-size: 15px;
  font-weight: 600;
  color: #333;
}

.task-status {
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 12px;
  font-weight: 500;
}

.task-status.pending {
  background: linear-gradient(135deg, #FFF0E8 0%, #FFE5D9 100%);
  color: #FF6B35;
}

.task-status.processing {
  background: linear-gradient(135deg, #E8F4FF 0%, #D6F4FF 100%);
  color: #1890FF;
}

.task-status.completed {
  background: linear-gradient(135deg, #E8FFE8 0%, #D6FFD6 100%);
  color: #52C41A;
}

.task-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #666;
}

.info-icon {
  color: #999;
}

.task-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f5f5f5;
}

.task-price {
  font-size: 18px;
  font-weight: 700;
  color: #FF6B35;
}

/* 动画 */
.slide-enter-active,
.slide-leave-active {
  transition: all 0.3s ease;
}

.slide-enter-from,
.slide-leave-to {
  transform: translateY(-20px);
  opacity: 0;
}
</style>
