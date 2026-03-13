<template>
  <div class="grab-pool">
    <van-nav-bar title="抢单池" />

    <!-- 刷新按钮 -->
    <div class="refresh-btn">
      <van-button size="small" @click="loadOrders">
        <i data-lucide="refresh-cw" style="width:14px;height:14px;margin-right:4px;"></i> 刷新
      </van-button>
      <span class="count">可抢订单: {{ orders.length }}</span>
    </div>

    <!-- 订单列表 -->
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list v-model:loading="loading" :finished="true">
        <div class="order-card" v-for="order in orders" :key="order.id">
          <van-card>
            <template #title>
              <div class="order-title">{{ order.serviceName }}</div>
            </template>
            <template #desc>
              <div class="order-info">
                <p><i data-lucide="clock" style="width:14px;height:14px;margin-right:4px;vertical-align:middle;"></i> 预约时间: {{ order.appointmentTime }}</p>
                <p><i data-lucide="map-pin" style="width:14px;height:14px;margin-right:4px;vertical-align:middle;"></i> 地址: {{ order.address }}</p>
              </div>
            </template>
            <template #price>
              <span class="price">¥{{ order.price }}</span>
            </template>
            <template #footer>
              <van-button type="primary" size="small" @click="handleGrab(order.id)" :loading="order.grabbing">
                立即抢单
              </van-button>
            </template>
          </van-card>
        </div>
      </van-list>

      <van-empty v-if="!loading && orders.length === 0" description="暂无可抢订单" />
    </van-pull-refresh>

    <!-- 底部导航 - 使用 Vant Tabbar -->
    <van-tabbar v-model="activeTab" route active-color="#667eea">
      <van-tabbar-item to="/">
        <span>首页</span>
        <template #icon="props">
          <i data-lucide="home" :style="{ color: props.active ? '#667eea' : '#999' }"></i>
        </template>
      </van-tabbar-item>
      <van-tabbar-item to="/grab" :badge="orders.length || ''">
        <span>抢单</span>
        <template #icon="props">
          <i data-lucide="shopping-bag" :style="{ color: props.active ? '#667eea' : '#999' }"></i>
        </template>
      </van-tabbar-item>
      <van-tabbar-item to="/orders">
        <span>订单</span>
        <template #icon="props">
          <i data-lucide="list" :style="{ color: props.active ? '#667eea' : '#999' }"></i>
        </template>
      </van-tabbar-item>
      <van-tabbar-item to="/user">
        <span>我的</span>
        <template #icon="props">
          <i data-lucide="user" :style="{ color: props.active ? '#667eea' : '#999' }"></i>
        </template>
      </van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { orderApi } from '../api/order'
import { showToast } from 'vant'

const activeTab = ref(1)
const orders = ref([])
const loading = ref(false)
const refreshing = ref(false)

// 初始化图标
const initIcons = () => {
  nextTick(() => {
    if (window.lucide) {
      window.lucide.createIcons()
    }
  })
}

const loadOrders = async () => {
  loading.value = true
  try {
    const res = await orderApi.getGrabPool()
    if (res.code === 200) {
      orders.value = (res.data || []).map(o => ({ ...o, grabbing: false }))
    }
  } finally {
    loading.value = false
    initIcons()
  }
}

const onRefresh = async () => {
  await loadOrders()
  refreshing.value = false
}

const handleGrab = async (orderId) => {
  const order = orders.value.find(o => o.id === orderId)
  if (!order) return
  
  order.grabbing = true
  try {
    const res = await orderApi.grab(orderId)
    if (res.code === 200) {
      showToast('抢单成功！')
      orders.value = orders.value.filter(o => o.id !== orderId)
    } else {
      showToast(res.message || '抢单失败')
    }
  } catch (error) {
    showToast('抢单失败，请重试')
  } finally {
    order.grabbing = false
  }
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.grab-pool {
  padding-bottom: 70px;
}

.refresh-btn {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 15px;
  background: #fff;
}

.count {
  color: #666;
  font-size: 14px;
}

.order-card {
  margin: 10px;
  border-radius: 8px;
  overflow: hidden;
  background: #fff;
}

.order-title {
  font-size: 16px;
  font-weight: bold;
}

.order-info {
  font-size: 12px;
  color: #666;
}

.order-info p {
  margin: 5px 0;
  display: flex;
  align-items: center;
}

.price {
  font-size: 18px;
  color: #ff6600;
  font-weight: bold;
}
</style>
