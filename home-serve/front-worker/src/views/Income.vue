<template>
  <div class="income-page">
    <van-nav-bar title="收入统计" left-arrow @click-left="goBack" />

    <!-- 收入概览 -->
    <div class="income-overview">
      <div class="overview-card">
        <div class="period-tabs">
          <span :class="{ active: period === 'today' }" @click="period = 'today'">今日</span>
          <span :class="{ active: period === 'month' }" @click="period = 'month'">本月</span>
          <span :class="{ active: period === 'year' }" @click="period = 'year'">本年</span>
        </div>
        <div class="income-amount">
          <span class="currency">¥</span>
          <span class="amount">{{ currentIncome }}</span>
        </div>
        <div class="income-detail">
          <div class="detail-item">
            <span class="detail-label">订单数</span>
            <span class="detail-value">{{ currentOrders }}单</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">平均单价</span>
            <span class="detail-value">¥{{ avgPrice }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 收入趋势 -->
    <div class="section-card">
      <div class="section-title">收入趋势</div>
      <div class="chart-placeholder">
        <div class="chart-bars">
          <div class="bar" v-for="(item, index) in chartData" :key="index" :style="{ height: item.percent + '%' }">
            <span class="bar-value">¥{{ item.value }}</span>
          </div>
        </div>
        <div class="chart-labels">
          <span v-for="(item, index) in chartData" :key="index">{{ item.label }}</span>
        </div>
      </div>
    </div>

    <!-- 收入明细 -->
    <div class="section-card">
      <div class="section-title">收入明细</div>
      <div class="income-list">
        <div class="income-item" v-for="item in incomeList" :key="item.id">
          <div class="item-left">
            <div class="item-icon">{{ getServiceIcon(item.serviceId) }}</div>
            <div class="item-info">
              <div class="item-name">{{ item.serviceName }}</div>
              <div class="item-time">{{ item.time }}</div>
            </div>
          </div>
          <div class="item-amount">+¥{{ item.amount }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const period = ref('month')

const incomeData = {
  today: { income: 328, orders: 5 },
  month: { income: 8650, orders: 128 },
  year: { income: 98500, orders: 1560 }
}

const currentIncome = computed(() => incomeData[period.value].income)
const currentOrders = computed(() => incomeData[period.value].orders)
const avgPrice = computed(() => Math.round(currentIncome.value / currentOrders.value))

const chartData = ref([
  { label: '周一', value: 280, percent: 60 },
  { label: '周二', value: 350, percent: 75 },
  { label: '周三', value: 420, percent: 90 },
  { label: '周四', value: 380, percent: 80 },
  { label: '周五', value: 450, percent: 95 },
  { label: '周六', value: 520, percent: 100 },
  { label: '周日', value: 400, percent: 85 }
])

const incomeList = ref([
  { id: 1, serviceName: '日常保洁', serviceId: 1, amount: 88, time: '今天 14:30' },
  { id: 2, serviceName: '深度清洁', serviceId: 1, amount: 188, time: '今天 10:00' },
  { id: 3, serviceName: '空调清洗', serviceId: 2, amount: 120, time: '昨天 16:00' }
])

const serviceIcons = { 1: '🧹', 2: '🔧', 3: '👶', 4: '🛠️' }
const getServiceIcon = (id) => serviceIcons[id] || '📋'

const goBack = () => router.back()
</script>

<style scoped>
.income-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 20px;
}

.income-overview {
  padding: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.overview-card {
  background: rgba(255,255,255,0.95);
  border-radius: 16px;
  padding: 20px;
  text-align: center;
}

.period-tabs {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-bottom: 16px;
}

.period-tabs span {
  font-size: 14px;
  color: #999;
  cursor: pointer;
  transition: all 0.2s;
}

.period-tabs span.active {
  color: #667eea;
  font-weight: 600;
}

.income-amount {
  margin-bottom: 16px;
}

.currency {
  font-size: 20px;
  color: #333;
}

.amount {
  font-size: 36px;
  font-weight: 700;
  color: #333;
}

.income-detail {
  display: flex;
  justify-content: center;
  gap: 40px;
}

.detail-item {
  display: flex;
  flex-direction: column;
}

.detail-label {
  font-size: 12px;
  color: #999;
}

.detail-value {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-top: 4px;
}

.section-card {
  background: #fff;
  margin: 12px;
  border-radius: 16px;
  padding: 16px;
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin-bottom: 14px;
}

.chart-placeholder {
  padding: 10px 0;
}

.chart-bars {
  display: flex;
  justify-content: space-around;
  align-items: flex-end;
  height: 120px;
}

.bar {
  width: 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 4px 4px 0 0;
  position: relative;
  min-height: 20px;
}

.bar-value {
  position: absolute;
  top: -20px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 10px;
  color: #666;
  white-space: nowrap;
}

.chart-labels {
  display: flex;
  justify-content: space-around;
  margin-top: 8px;
  font-size: 11px;
  color: #999;
}

.income-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.income-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f5f5f5;
}

.income-item:last-child {
  border-bottom: none;
}

.item-left {
  display: flex;
  gap: 12px;
}

.item-icon {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #FFE5E5 0%, #FFE8DC 100%);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.item-name {
  font-size: 14px;
  color: #333;
}

.item-time {
  font-size: 12px;
  color: #999;
  margin-top: 2px;
}

.item-amount {
  font-size: 16px;
  font-weight: 600;
  color: #52C41A;
}
</style>
