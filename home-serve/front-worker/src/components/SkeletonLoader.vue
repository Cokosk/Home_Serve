<template>
  <div class="skeleton-loader">
    <!-- 任务卡片骨架屏 -->
    <div v-if="type === 'task-card'" class="skeleton-task-card">
      <div class="skeleton-header">
        <div class="skeleton-title"></div>
        <div class="skeleton-status"></div>
      </div>
      <div class="skeleton-body">
        <div class="skeleton-row"></div>
        <div class="skeleton-row"></div>
      </div>
      <div class="skeleton-footer">
        <div class="skeleton-price"></div>
        <div class="skeleton-btn"></div>
      </div>
    </div>

    <!-- 统计卡片骨架屏 -->
    <div v-else-if="type === 'stat-card'" class="skeleton-stat-card">
      <div class="skeleton-icon-circle"></div>
      <div class="skeleton-value"></div>
      <div class="skeleton-label"></div>
    </div>

    <!-- 订单卡片骨架屏 -->
    <div v-else-if="type === 'order-card'" class="skeleton-order-card">
      <div class="skeleton-header">
        <div class="skeleton-title"></div>
        <div class="skeleton-status"></div>
      </div>
      <div class="skeleton-body">
        <div class="skeleton-row"></div>
        <div class="skeleton-row"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  type: {
    type: String,
    default: 'task-card'
  }
})
</script>

<style scoped>
@keyframes shimmer {
  0% { background-position: -200px 0; }
  100% { background-position: calc(200px + 100%) 0; }
}

.skeleton-task-card,
.skeleton-stat-card,
.skeleton-order-card {
  background: #fff;
  border-radius: 14px;
  padding: 14px;
  margin-bottom: 10px;
}

.skeleton-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
}

.skeleton-title {
  width: 100px;
  height: 16px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200px 100%;
  animation: shimmer 1.5s infinite;
  border-radius: 4px;
}

.skeleton-status {
  width: 60px;
  height: 20px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200px 100%;
  animation: shimmer 1.5s infinite;
  border-radius: 10px;
}

.skeleton-body {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.skeleton-row {
  width: 100%;
  height: 14px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200px 100%;
  animation: shimmer 1.5s infinite;
  border-radius: 4px;
}

.skeleton-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f5f5f5;
}

.skeleton-price {
  width: 60px;
  height: 18px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200px 100%;
  animation: shimmer 1.5s infinite;
  border-radius: 4px;
}

.skeleton-btn {
  width: 80px;
  height: 28px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200px 100%;
  animation: shimmer 1.5s infinite;
  border-radius: 4px;
}

.skeleton-stat-card {
  text-align: center;
  padding: 20px;
}

.skeleton-icon-circle {
  width: 40px;
  height: 40px;
  margin: 0 auto 10px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200px 100%;
  animation: shimmer 1.5s infinite;
  border-radius: 50%;
}

.skeleton-value {
  width: 50px;
  height: 24px;
  margin: 0 auto;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200px 100%;
  animation: shimmer 1.5s infinite;
  border-radius: 4px;
}

.skeleton-label {
  width: 40px;
  height: 12px;
  margin: 8px auto 0;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200px 100%;
  animation: shimmer 1.5s infinite;
  border-radius: 4px;
}
</style>
