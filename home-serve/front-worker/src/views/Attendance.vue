<template>
  <div class="attendance-page">
    <van-nav-bar title="考勤打卡" left-arrow @click-left="goBack" />

    <!-- 打卡卡片 -->
    <div class="clock-card">
      <div class="current-time">
        <div class="time">{{ currentTime }}</div>
        <div class="date">{{ currentDate }}</div>
      </div>
      
      <div class="clock-btn" :class="{ checked: isCheckedIn }" @click="handleClock">
        <div class="clock-inner">
          <van-icon :name="isCheckedIn ? 'success' : 'location-o'" size="32" />
          <span>{{ isCheckedIn ? '已打卡' : '打卡' }}</span>
        </div>
      </div>
      
      <div class="clock-status" v-if="lastClockTime">
        上次打卡: {{ lastClockTime }}
      </div>
    </div>

    <!-- 本月统计 -->
    <div class="stats-card">
      <div class="stats-title">本月考勤</div>
      <div class="stats-grid">
        <div class="stat-item">
          <div class="stat-value">{{ monthStats.total }}</div>
          <div class="stat-label">应出勤</div>
        </div>
        <div class="stat-item">
          <div class="stat-value success">{{ monthStats.checked }}</div>
          <div class="stat-label">已出勤</div>
        </div>
        <div class="stat-item">
          <div class="stat-value warning">{{ monthStats.late }}</div>
          <div class="stat-label">迟到</div>
        </div>
        <div class="stat-item">
          <div class="stat-value danger">{{ monthStats.absent }}</div>
          <div class="stat-label">缺勤</div>
        </div>
      </div>
    </div>

    <!-- 打卡记录 -->
    <div class="records-card">
      <div class="records-title">打卡记录</div>
      <div class="records-list">
        <div class="record-item" v-for="record in records" :key="record.date">
          <div class="record-date">{{ record.date }}</div>
          <div class="record-times">
            <div class="time-item" :class="record.onTime ? 'success' : 'warning'">
              <span class="label">上班</span>
              <span class="time">{{ record.onDuty || '--:--' }}</span>
            </div>
            <div class="time-item" :class="record.offTime ? 'success' : ''">
              <span class="label">下班</span>
              <span class="time">{{ record.offDuty || '--:--' }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部导航 - 使用 Vant Tabbar -->
    <van-tabbar v-model="activeTab" route active-color="#667eea">
      <van-tabbar-item to="/">
        <span>首页</span>
        <template #icon="props">
          <i data-lucide="home" :style="{ color: props.active ? '#667eea' : '#999' }"></i>
        </template>
      </van-tabbar-item>
      <van-tabbar-item to="/grab" :badge="grabCount || ''">
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
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'

const router = useRouter()
const currentTime = ref('')
const currentDate = ref('')
const isCheckedIn = ref(false)
const lastClockTime = ref('')
const activeTab = ref(3)
const grabCount = ref(0)

const monthStats = ref({
  total: 22,
  checked: 18,
  late: 2,
  absent: 0
})

const records = ref([
  { date: '03-13 周四', onDuty: '08:55', onTime: true, offDuty: '18:10', offTime: true },
  { date: '03-12 周三', onDuty: '09:05', onTime: false, offDuty: '18:00', offTime: true },
  { date: '03-11 周二', onDuty: '08:50', onTime: true, offDuty: '18:05', offTime: true }
])

let timer = null

const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit', second: '2-digit' })
  currentDate.value = now.toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' })
}

const handleClock = () => {
  if (isCheckedIn.value) {
    showToast('今日已打卡')
    return
  }
  isCheckedIn.value = true
  lastClockTime.value = currentTime.value
  showToast('打卡成功')
}

const goBack = () => router.back()

// 初始化图标
const initIcons = () => {
  nextTick(() => {
    if (window.lucide) {
      window.lucide.createIcons()
    }
  })
}

onMounted(() => {
  updateTime()
  timer = setInterval(updateTime, 1000)
  initIcons()
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.attendance-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 70px;
}

.clock-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 30px;
  text-align: center;
  color: #fff;
}

.current-time {
  margin-bottom: 24px;
}

.time {
  font-size: 42px;
  font-weight: 700;
  letter-spacing: 2px;
}

.date {
  font-size: 14px;
  opacity: 0.9;
  margin-top: 8px;
}

.clock-btn {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: rgba(255,255,255,0.2);
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
}

.clock-btn:active {
  transform: scale(0.95);
}

.clock-btn.checked {
  background: rgba(82, 196, 26, 0.3);
}

.clock-inner {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: #fff;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #667eea;
}

.clock-btn.checked .clock-inner {
  color: #52C41A;
}

.clock-inner span {
  font-size: 14px;
  font-weight: 500;
  margin-top: 4px;
}

.clock-status {
  margin-top: 16px;
  font-size: 13px;
  opacity: 0.9;
}

.stats-card {
  background: #fff;
  margin: 12px;
  border-radius: 16px;
  padding: 16px;
}

.stats-title, .records-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin-bottom: 14px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #333;
}

.stat-value.success { color: #52C41A; }
.stat-value.warning { color: #FAAD14; }
.stat-value.danger { color: #FF4D4F; }

.stat-label {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.records-card {
  background: #fff;
  margin: 12px;
  border-radius: 16px;
  padding: 16px;
}

.record-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
}

.record-item:last-child {
  border-bottom: none;
}

.record-date {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.record-times {
  display: flex;
  gap: 20px;
}

.time-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.time-item .label {
  font-size: 11px;
  color: #999;
}

.time-item .time {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.time-item.success .time { color: #52C41A; }
.time-item.warning .time { color: #FAAD14; }
</style>
