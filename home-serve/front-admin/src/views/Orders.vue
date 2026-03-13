<template>
  <div class="orders-page">
    <div class="page-card">
      <div class="page-title">订单管理</div>
      
      <!-- 搜索筛选 -->
      <div class="search-section">
        <el-form :inline="true" class="search-form">
          <el-form-item label="订单号">
            <el-input v-model="searchForm.orderNo" placeholder="请输入订单号" clearable>
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item label="服务类型">
            <el-select v-model="searchForm.serviceType" placeholder="全部" clearable>
              <el-option label="保洁清洗" value="1" />
              <el-option label="家电维修" value="2" />
              <el-option label="月嫂保姆" value="3" />
              <el-option label="搬家服务" value="4" />
            </el-select>
          </el-form-item>
          <el-form-item label="订单状态">
            <el-select v-model="searchForm.status" placeholder="全部" clearable>
              <el-option label="待抢单" :value="0" />
              <el-option label="已接单" :value="1" />
              <el-option label="服务中" :value="2" />
              <el-option label="已完成" :value="3" />
              <el-option label="已取消" :value="4" />
            </el-select>
          </el-form-item>
          <el-form-item label="时间范围">
            <el-date-picker
              v-model="searchForm.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadOrders">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="resetSearch">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 数据统计 -->
      <div class="stats-row">
        <div class="stat-item">
          <span class="stat-label">全部订单</span>
          <span class="stat-value">{{ stats.total }}</span>
        </div>
        <div class="stat-item pending">
          <span class="stat-label">待抢单</span>
          <span class="stat-value">{{ stats.pending }}</span>
        </div>
        <div class="stat-item processing">
          <span class="stat-label">进行中</span>
          <span class="stat-value">{{ stats.processing }}</span>
        </div>
        <div class="stat-item completed">
          <span class="stat-label">已完成</span>
          <span class="stat-value">{{ stats.completed }}</span>
        </div>
        <div class="stat-item cancelled">
          <span class="stat-label">已取消</span>
          <span class="stat-value">{{ stats.cancelled }}</span>
        </div>
      </div>

      <!-- 订单列表 -->
      <el-table :data="orders" style="width: 100%" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="orderNo" label="订单号" width="180">
          <template #default="{ row }">
            <span class="order-no">{{ row.orderNo }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="serviceName" label="服务名称">
          <template #default="{ row }">
            <div class="service-cell">
              <span class="service-icon">{{ getServiceIcon(row.serviceId) }}</span>
              <span>{{ row.serviceName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="金额" width="100">
          <template #default="{ row }">
            <span class="price">¥{{ row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="appointmentTime" label="预约时间" width="160" />
        <el-table-column prop="phone" label="联系电话" width="120" />
        <el-table-column prop="address" label="地址" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" effect="plain">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewDetail(row)">
              <el-icon><View /></el-icon>
              查看
            </el-button>
            <el-button type="primary" link @click="assignWorker(row)" v-if="row.status === 0">
              <el-icon><UserFilled /></el-icon>
              指派
            </el-button>
            <el-button type="danger" link @click="cancelOrder(row)" v-if="row.status === 0">
              <el-icon><Close /></el-icon>
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadOrders"
          @current-change="loadOrders"
        />
      </div>
    </div>

    <!-- 订单详情弹窗 -->
    <el-dialog v-model="detailVisible" title="订单详情" width="650px" destroy-on-close>
      <el-descriptions :column="2" border v-if="currentOrder">
        <el-descriptions-item label="订单号">
          <span class="order-no">{{ currentOrder.orderNo }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="getStatusType(currentOrder.status)">
            {{ getStatusText(currentOrder.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="服务名称">{{ currentOrder.serviceName }}</el-descriptions-item>
        <el-descriptions-item label="订单金额">
          <span class="price">¥{{ currentOrder.price }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="预约时间">{{ currentOrder.appointmentTime }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentOrder.phone }}</el-descriptions-item>
        <el-descriptions-item label="服务地址" :span="2">{{ currentOrder.address }}</el-descriptions-item>
        <el-descriptions-item label="服务人员" v-if="currentOrder.workerName">
          {{ currentOrder.workerName }}
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">
          {{ currentOrder.remark || '无' }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentOrder.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ currentOrder.updateTime }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button type="primary" @click="printOrder">打印订单</el-button>
      </template>
    </el-dialog>

    <!-- 指派服务者弹窗 -->
    <el-dialog v-model="assignVisible" title="指派服务者" width="500px">
      <el-form label-width="80px">
        <el-form-item label="选择服务者">
          <el-select v-model="selectedWorker" placeholder="请选择" style="width: 100%">
            <el-option 
              v-for="worker in workerList" 
              :key="worker.id" 
              :label="worker.name" 
              :value="worker.id"
            >
              <div class="worker-option">
                <span>{{ worker.name }}</span>
                <span class="worker-rating">评分: {{ worker.rating }}</span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAssign">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, View, UserFilled, Close } from '@element-plus/icons-vue'
import { orderApi } from '../api'

const loading = ref(false)
const orders = ref([])
const detailVisible = ref(false)
const assignVisible = ref(false)
const currentOrder = ref(null)
const selectedWorker = ref('')

const searchForm = reactive({
  orderNo: '',
  serviceType: '',
  status: '',
  dateRange: []
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const stats = reactive({
  total: 1256,
  pending: 23,
  processing: 45,
  completed: 1156,
  cancelled: 32
})

const workerList = ref([
  { id: 1, name: '张师傅', rating: 4.9 },
  { id: 2, name: '李师傅', rating: 4.8 },
  { id: 3, name: '王师傅', rating: 4.7 }
])

// 服务图标
const serviceIcons = {
  1: '🧹',
  2: '🔧',
  3: '👶',
  4: '🛠️'
}

const getServiceIcon = (serviceId) => serviceIcons[serviceId] || '📋'

const getStatusType = (status) => {
  const types = { 0: 'warning', 1: 'success', 2: 'primary', 3: 'info', 4: 'danger' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { 0: '待抢单', 1: '已接单', 2: '服务中', 3: '已完成', 4: '已取消' }
  return texts[status] || '未知'
}

const loadOrders = async () => {
  loading.value = true
  try {
    const res = await orderApi.list({
      userId: 1,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ...searchForm
    })
    if (res.code === 200) {
      orders.value = res.data || []
      pagination.total = res.total || 0
    }
  } catch (e) {
    ElMessage.error('加载订单失败')
  } finally {
    loading.value = false
  }
}

const resetSearch = () => {
  searchForm.orderNo = ''
  searchForm.serviceType = ''
  searchForm.status = ''
  searchForm.dateRange = []
  pagination.pageNum = 1
  loadOrders()
}

const viewDetail = (row) => {
  currentOrder.value = row
  detailVisible.value = true
}

const assignWorker = (row) => {
  currentOrder.value = row
  assignVisible.value = true
}

const confirmAssign = () => {
  if (!selectedWorker.value) {
    ElMessage.warning('请选择服务者')
    return
  }
  ElMessage.success('指派成功')
  assignVisible.value = false
  loadOrders()
}

const cancelOrder = async (row) => {
  try {
    await ElMessageBox.confirm('确定要取消此订单吗？', '提示', { type: 'warning' })
    const res = await orderApi.cancel(row.id)
    if (res.code === 200) {
      ElMessage.success('取消成功')
      loadOrders()
    } else {
      ElMessage.error(res.message || '取消失败')
    }
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('取消失败')
    }
  }
}

const printOrder = () => {
  ElMessage.success('正在打印...')
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.orders-page {
  padding: 20px;
}

.page-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
}

.search-section {
  margin-bottom: 20px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
}

.stats-row {
  display: flex;
  gap: 24px;
  margin-bottom: 20px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-label {
  font-size: 13px;
  color: #999;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #333;
}

.stat-item.pending .stat-value { color: #FA8C16; }
.stat-item.processing .stat-value { color: #1890FF; }
.stat-item.completed .stat-value { color: #52C41A; }
.stat-item.cancelled .stat-value { color: #FF4D4F; }

.order-no {
  font-family: monospace;
  color: #667eea;
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

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.worker-option {
  display: flex;
  justify-content: space-between;
  width: 100%;
}

.worker-rating {
  color: #999;
  font-size: 12px;
}
</style>
