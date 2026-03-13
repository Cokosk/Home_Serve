<template>
  <div class="skeleton-loader">
    <!-- 服务卡片骨架屏 -->
    <div v-if="type === 'service-card'" class="skeleton-service-card">
      <div class="skeleton-image"></div>
      <div class="skeleton-content">
        <div class="skeleton-title"></div>
        <div class="skeleton-text"></div>
        <div class="skeleton-meta">
          <div class="skeleton-price"></div>
          <div class="skeleton-sales"></div>
        </div>
      </div>
    </div>

    <!-- 分类骨架屏 -->
    <div v-else-if="type === 'category'" class="skeleton-category">
      <div class="skeleton-icon"></div>
      <div class="skeleton-name"></div>
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
        <div class="skeleton-row short"></div>
      </div>
    </div>

    <!-- 轮播图骨架屏 -->
    <div v-else-if="type === 'banner'" class="skeleton-banner"></div>

    <!-- 统计卡片骨架屏 -->
    <div v-else-if="type === 'stat-card'" class="skeleton-stat-card">
      <div class="skeleton-icon-circle"></div>
      <div class="skeleton-value"></div>
      <div class="skeleton-label"></div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  type: {
    type: String,
    default: 'service-card'
  }
})
</script>

<style scoped>
@keyframes shimmer {
  0% {
    background-position: -200px 0;
  }
  100% {
    background-position: calc(200px + 100%) 0;
  }
}

.skeleton-loader {
  width: 100%;
}

/* 服务卡片骨架屏 */
.skeleton-service-card {
  display: flex;
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 10px;
}

.skeleton-image {
  width: 80px;
  height: 80px;
  flex-shrink: 0;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200px 100%;
  animation: shimmer 1.5s infinite;
}

.skeleton-content {
  flex: 1;
  padding: 12px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.skeleton-title {
  width: 60%;
  height: 16px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200px 100%;
  animation: shimmer 1.5s infinite;
  border-radius: 4px;
}

.skeleton-text {
  width: 90%;
  height: 12px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200px 100%;
  animation: shimmer 1.5s infinite;
  border-radius: 4px;
  margin: 4px 0;
}

.skeleton-meta {
  display: flex;
  justify-content: space-between;
}

.skeleton-price {
  width: 50px;
  height: 18px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200px 100%;
  animation: shimmer 1.5s infinite;
  border-radius: 4px;
}

.skeleton-sales {
  width: 40px;
  height: 12px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200px 100%;
  animation: shimmer 1.5s infinite;
  border-radius: 4px;
}

/* 分类骨架屏 */
.skeleton-category {
  width: 68px;
  text-align: center;
  padding: 10px 4px;
}

.skeleton-icon {
  width: 44px;
  height: 44px;
  margin: 0 auto 6px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200px 100%;
  animation: shimmer 1.5s infinite;
  border-radius: 12px;
}

.skeleton-name {
  width: 32px;
  height: 12px;
  margin: 0 auto;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200px 100%;
  animation: shimmer 1.5s infinite;
  border-radius: 4px;
}

/* 订单卡片骨架屏 */
.skeleton-order-card {
  background: #fff;
  border-radius: 12px;
  padding: 12px;
  margin-bottom: 10px;
}

.skeleton-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f5f5f5;
}

.skeleton-status {
  width: 50px;
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

.skeleton-row.short {
  width: 60%;
}

/* 轮播图骨架屏 */
.skeleton-banner {
  width: 100%;
  height: 120px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200px 100%;
  animation: shimmer 1.5s infinite;
  border-radius: 12px;
}

/* 统计卡片骨架屏 */
.skeleton-stat-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  padding: 15px 10px;
  text-align: center;
}

.skeleton-icon-circle {
  width: 36px;
  height: 36px;
  margin: 0 auto 8px;
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
  margin: 6px auto 0;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200px 100%;
  animation: shimmer 1.5s infinite;
  border-radius: 4px;
}
</style>
