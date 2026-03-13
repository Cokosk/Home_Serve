<template>
  <div class="dashboard">
    <!-- 数据概览卡片 -->
    <div class="stats-cards">
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat-card orders">
            <div class="stat-icon">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.todayOrders }}</div>
              <div class="stat-label">今日订单</div>
            </div>
            <div class="stat-trend up" v-if="stats.orderTrend > 0">
              <el-icon><ArrowUp /></el-icon>
              {{ stats.orderTrend }}%
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card pending">
            <div class="stat-icon">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.pendingOrders }}</div>
              <div class="stat-label">待抢单</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card workers">
            <div class="stat-icon">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.workersOnline }}</div>
              <div class="stat-label">服务者在线</div>
            </div>
            <div class="stat-status online">在线</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card income">
            <div class="stat-icon">
              <el-icon><Money /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">¥{{ formatNumber(stats.todayIncome) }}</div>
              <div class="stat-label">今日收入</div>
            </div>
            <div class="stat-trend up" v-if="stats.incomeTrend > 0">
              <el-icon><ArrowUp /></el-icon>
              {{ stats.incomeTrend }}%
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-section">
      <el-col :span="16">
        <div class="chart-card">
          <div class="card-header">
            <span class="card-title">订单趋势</span>
            <div class="card-actions">
              <el-radio-group v-model="chartPeriod" size="small">
                <el-radio-button label="week">近7天</el-radio-button>
                <el-radio-button label="month">近30天</el-radio-button>
              </el-radio-group>
            </div>
          </div>
          <div class="chart-container" ref="orderChartRef"></div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="chart-card">
          <div class="card-header">
            <span class="card-title">服务类型分布</span>
          </div>
          <div class="chart-container" ref="serviceChartRef"></div>
        </div>
      </el-col>
    </el-row>

    <!-- 热门服务和最新订单 -->
    <el-row :gutter="20" class="data-section">
      <el-col :span="12">
        <div class="data-card">
          <div class="card-header">
            <span class="card-title">🔥 热门服务</span>
            <el-button type="primary" link>查看全部</el-button>
          </div>
          <el-table :data="hotServices" style="width: 100%">
            <el-table-column prop="name" label="服务名称">
              <template #default="{ row }">
                <div class="service-cell">
                  <span class="service-icon">{{ getServiceIcon(row.categoryId) }}</span>
                  <span>{{ row.name }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="price" label="价格" width="100">
              <template #default="{ row }">
                <span class="price">¥{{ row.price }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="sales" label="销量" width="80">
              <template #default="{ row }">
                <el-tag size="small" type="success">{{ row.sales || 100 }}+</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="data-card">
          <div class="card-header">
            <span class="card-title">📋 最新订单</span>
            <el-button type="primary" link>查看全部</el-button>
          </div>
          <el-table :data="recentOrders" style="width: 100%">
            <el-table-column prop="orderNo" label="订单号" width="150" />
            <el-table-column prop="serviceName" label="服务" />
            <el-table-column prop="price" label="金额" width="90">
              <template #default="{ row }">
                <span class="price">¥{{ row.price }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="90">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)" size="small">
                  {{ getStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { Document, Clock, User, Money, ArrowUp } from '@element-plus/icons-vue'
import { serviceApi, orderApi } from '../api'
import * as echarts from 'echarts'

const chartPeriod = ref('week')
const orderChartRef = ref(null)
const serviceChartRef = ref(null)
let orderChart = null
let serviceChart = null

const stats = ref({
  todayOrders: 156,
  pendingOrders: 23,
  workersOnline: 45,
  todayIncome: 28560,
  orderTrend: 12.5,
  incomeTrend: 8.3
})

const hotServices = ref([])
const recentOrders = ref([])

// 服务图标映射
const serviceIcons = {
  1: '🧹',
  2: '🔧',
  3: '👶',
  4: '🛠️'
}

const getServiceIcon = (categoryId) => serviceIcons[categoryId] || '📋'

const formatNumber = (num) => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  }
  return num
}

const getStatusType = (status) => {
  const types = { 0: 'warning', 1: 'success', 2: 'primary', 3: 'info', 4: 'danger' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { 0: '待抢单', 1: '已接单', 2: '服务中', 3: '已完成', 4: '已取消' }
  return texts[status] || '未知'
}

// 初始化订单趋势图
const initOrderChart = () => {
  if (!orderChartRef.value) return
  
  orderChart = echarts.init(orderChartRef.value)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    legend: {
      data: ['订单数', '收入'],
      top: 0
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
      axisLine: {
        lineStyle: { color: '#e0e0e0' }
      },
      axisLabel: {
        color: '#666'
      }
    },
    yAxis: [
      {
        type: 'value',
        name: '订单数',
        axisLine: { show: false },
        axisTick: { show: false },
        axisLabel: { color: '#666' },
        splitLine: {
          lineStyle: { color: '#f0f0f0' }
        }
      },
      {
        type: 'value',
        name: '收入(元)',
        axisLine: { show: false },
        axisTick: { show: false },
        axisLabel: { color: '#666' },
        splitLine: { show: false }
      }
    ],
    series: [
      {
        name: '订单数',
        type: 'bar',
        barWidth: '40%',
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#667eea' },
            { offset: 1, color: '#764ba2' }
          ]),
          borderRadius: [4, 4, 0, 0]
        },
        data: [120, 132, 101, 134, 90, 230, 156]
      },
      {
        name: '收入',
        type: 'line',
        yAxisIndex: 1,
        smooth: true,
        lineStyle: {
          color: '#FF6B35',
          width: 3
        },
        itemStyle: {
          color: '#FF6B35'
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(255, 107, 53, 0.3)' },
            { offset: 1, color: 'rgba(255, 107, 53, 0.05)' }
          ])
        },
        data: [18200, 23200, 18200, 21200, 15600, 35200, 28560]
      }
    ]
  }
  
  orderChart.setOption(option)
}

// 初始化服务分布图
const initServiceChart = () => {
  if (!serviceChartRef.value) return
  
  serviceChart = echarts.init(serviceChartRef.value)
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: '5%',
      top: 'center',
      itemGap: 15
    },
    series: [
      {
        type: 'pie',
        radius: ['45%', '70%'],
        center: ['35%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 8,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 14,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: [
          { value: 1048, name: '保洁清洗', itemStyle: { color: '#667eea' } },
          { value: 735, name: '家电维修', itemStyle: { color: '#FF6B35' } },
          { value: 580, name: '月嫂保姆', itemStyle: { color: '#52C41A' } },
          { value: 484, name: '搬家服务', itemStyle: { color: '#FA8C16' } }
        ]
      }
    ]
  }
  
  serviceChart.setOption(option)
}

// 加载数据
const loadData = async () => {
  try {
    const hotRes = await serviceApi.hot()
    if (hotRes.code === 200) {
      hotServices.value = hotRes.data?.slice(0, 5) || []
    }
    
    const poolRes = await orderApi.grabList()
    if (poolRes.code === 200) {
      stats.value.pendingOrders = poolRes.data?.length || 0
    }
    
    // 模拟最新订单
    recentOrders.value = [
      { orderNo: 'ORD202403130001', serviceName: '日常保洁', price: 128, status: 0 },
      { orderNo: 'ORD202403130002', serviceName: '空调清洗', price: 168, status: 1 },
      { orderNo: 'ORD202403130003', serviceName: '深度保洁', price: 298, status: 2 },
      { orderNo: 'ORD202403130004', serviceName: '油烟机清洗', price: 188, status: 3 },
      { orderNo: 'ORD202403130005', serviceName: '窗帘清洗', price: 268, status: 3 }
    ]
  } catch (e) {
    console.error('加载数据失败', e)
  }
}

// 监听窗口大小变化
const handleResize = () => {
  orderChart?.resize()
  serviceChart?.resize()
}

watch(chartPeriod, () => {
  // 重新加载图表数据
  initOrderChart()
})

onMounted(async () => {
  await loadData()
  setTimeout(() => {
    initOrderChart()
    initServiceChart()
  }, 100)
  window.addEventListener('resize', handleResize)
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}

/* 统计卡片 */
.stats-cards {
  margin-bottom: 20px;
}

.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
}

.stat-card.orders::before { background: linear-gradient(180deg, #667eea, #764ba2); }
.stat-card.pending::before { background: linear-gradient(180deg, #FA8C16, #FAAD14); }
.stat-card.workers::before { background: linear-gradient(180deg, #52C41A, #73D13D); }
.stat-card.income::before { background: linear-gradient(180deg, #FF6B35, #FF8E53); }

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #fff;
}

.stat-card.orders .stat-icon { background: linear-gradient(135deg, #667eea, #764ba2); }
.stat-card.pending .stat-icon { background: linear-gradient(135deg, #FA8C16, #FAAD14); }
.stat-card.workers .stat-icon { background: linear-gradient(135deg, #52C41A, #73D13D); }
.stat-card.income .stat-icon { background: linear-gradient(135deg, #FF6B35, #FF8E53); }

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #999;
  margin-top: 4px;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  padding: 4px 8px;
  border-radius: 6px;
}

.stat-trend.up {
  background: #F6FFED;
  color: #52C41A;
}

.stat-status {
  font-size: 12px;
  padding: 4px 8px;
  border-radius: 6px;
}

.stat-status.online {
  background: #E8FFE8;
  color: #52C41A;
}

/* 图表区域 */
.chart-section {
  margin-bottom: 20px;
}

.chart-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.chart-container {
  height: 300px;
}

/* 数据卡片 */
.data-section .data-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.service-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.service-icon {
  font-size: 18px;
}

.price {
  color: #FF6B35;
  font-weight: 600;
}
</style>
