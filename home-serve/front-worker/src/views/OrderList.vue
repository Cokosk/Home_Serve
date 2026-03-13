<template>
  <div class="order-list">
    <van-nav-bar title="我的订单" />

    <van-tabs v-model:active="activeStatus" @change="onStatusChange" sticky shrink>
      <van-tab title="全部" name="" />
      <van-tab title="待处理" name="pending" />
      <van-tab title="进行中" name="processing" />
      <van-tab title="已完成" name="completed" />
    </van-tabs>

    <van-pull-refresh v-model="refreshing" @refresh="onRefresh" success-text="刷新成功">
      <van-list
        v-model:loading="loading"
        :finished="finished"
        finished-text=""
        @load="loadOrders"
      >
        <div class="order-item" v-for="order in orders" :key="order.id" @click="goToDetail(order.id)">
          <div class="order-card">
            <div class="order-header">
              <div class="order-service">
                <span class="service-icon">{{ getServiceIcon(order.serviceId) }}</span>
                <span class="service-name">{{ order.serviceName }}</span>
              </div>
              <span class="order-status" :class="'status-' + getStatusClass(order.status)">
                {{ getStatusText(order.status) }}
              </span>
            </div>
            <div class="order-body">
              <div class="info-row">
                <div class="info-icon"><van-icon name="clock-o" /></div>
                <span class="info-text">{{ order.appointmentTime }}</span>
              </div>
              <div class="info-row">
                <div class="info-icon"><van-icon name="location-o" /></div>
                <span class="info-text">{{ order.address }}</span>
              </div>
            </div>
            <div class="order-footer">
              <div class="order-price">
                <span class="price-label">订单金额</span>
                <span class="price-value">¥{{ order.price }}</span>
              </div>
              <div class="order-actions">
                <van-button 
                  v-if="order.status === 'pending'" 
                  size="small" 
                  type="primary"
                  @click.stop="confirmOrder(order)"
                >
                  确认接单
                </van-button>
                <van-button 
                  v-if="order.status === 'processing'" 
                  size="small" 
                  type="primary"
                  @click.stop="completeOrder(order)"
                >
                  完成服务
                </van-button>
                <van-button 
                  v-if="order.status === 'completed'" 
                  size="small" 
                  type="default"
                  plain
                  @click.stop="goToDetail(order.id)"
                >
                  查看详情
                </van-button>
              </div>
            </div>
          </div>
        </div>
        
        <van-empty v-if="orders.length === 0 && !loading" description="暂无订单" />
      </van-list>
    </van-pull-refresh>

    <van-tabbar v-model="activeTab" route active-color="#667eea">
      <van-tabbar-item icon="home-o" to="/">首页</van-tabbar-item>
      <van-tabbar-item icon="shopping-cart-o" to="/grab" :badge="grabCount || ''">抢单</van-tabbar-item>
      <van-tabbar-item icon="orders-o" to="/orders">订单</van-tabbar-item>
      <van-tabbar-item icon="user-o" to="/user">我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { orderApi } from '../api/order'
import { showToast } from 'vant'

const router = useRouter()

const activeTab = ref(2)
const activeStatus = ref('')
const orders = ref([])
const loading = ref(false)
const refreshing = ref(false)
const finished = ref(false)
const page = ref(1)
const grabCount = ref(0)

// 服务图标
const serviceIcons = {
  1: '🧹',
  2: '🔧',
  3: '👶',
  4: '🛠️'
}

const getServiceIcon = (serviceId) => serviceIcons[serviceId] || '📋'

const getStatusClass = (status) => {
  const classes = {
    pending: 'pending',
    processing: 'processing',
    completed: 'completed',
    cancelled: 'cancelled'
  }
  return classes[status] || 'pending'
}

const getStatusText = (status) => {
  const statusMap = {
    pending: '待处理',
    processing: '进行中',
    completed: '已完成',
    cancelled: '已取消'
  }
  return statusMap[status] || status
}

const loadOrders = async () => {
  loading.value = true
  try {
    const res = await orderApi.getList({
      page: page.value,
      size: 10,
      status: activeStatus.value
    })
    if (res.code === 200) {
      const data = res.data?.records || res.data || []
      orders.value = [...orders.value, ...data]
      page.value++
      finished.value = data.length < 10
    }
  } finally {
    loading.value = false
  }
}

const onRefresh = () => {
  page.value = 1
  orders.value = []
  finished.value = false
  loadOrders().finally(() => {
    refreshing.value = false
  })
}

const onStatusChange = () => {
  page.value = 1
  orders.value = []
  finished.value = false
  loadOrders()
}

const confirmOrder = (order) => {
  showToast('已确认接单')
}

const completeOrder = (order) => {
  showToast('服务已完成')
}

const goToDetail = (id) => {
  router.push(`/order/${id}`)
}
</script>

<style scoped>
.order-list {
  padding-bottom: 60px;
  background: #f5f5f5;
  min-height: 100vh;
}

.order-item {
  padding: 8px 12px;
}

.order-card {
  background: #fff;
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px;
  border-bottom: 1px solid #f5f5f5;
}

.order-service {
  display: flex;
  align-items: center;
  gap: 8px;
}

.service-icon {
  font-size: 20px;
}

.service-name {
  font-size: 15px;
  font-weight: 600;
  color: #333;
}

.order-status {
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 12px;
  font-weight: 500;
}

.status-pending {
  background: linear-gradient(135deg, #FFF0E8 0%, #FFE5D9 100%);
  color: #FF6B35;
}

.status-processing {
  background: linear-gradient(135deg, #E8F4FF 0%, #D6F4FF 100%);
  color: #1890FF;
}

.status-completed {
  background: linear-gradient(135deg, #E8FFE8 0%, #D6FFD6 100%);
  color: #52C41A;
}

.status-cancelled {
  background: linear-gradient(135deg, #FFF1F0 0%, #FFE7E6 100%);
  color: #FF4D4F;
}

.order-body {
  padding: 12px 14px;
}

.info-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.info-row:last-child {
  margin-bottom: 0;
}

.info-icon {
  width: 24px;
  height: 24px;
  background: #f5f5f5;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
  font-size: 14px;
}

.info-text {
  font-size: 13px;
  color: #666;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 14px;
  background: #fafafa;
}

.order-price {
  display: flex;
  flex-direction: column;
}

.price-label {
  font-size: 11px;
  color: #999;
}

.price-value {
  font-size: 18px;
  color: #FF6B35;
  font-weight: 700;
}

.order-actions {
  display: flex;
  gap: 8px;
}
</style>
