<template>
  <div class="empty-state">
    <div class="empty-icon">
      {{ getIcon() }}
    </div>
    <div class="empty-title">{{ title || '暂无数据' }}</div>
    <div class="empty-desc" v-if="description">{{ description }}</div>
    <div class="empty-action" v-if="actionText">
      <van-button type="primary" size="small" @click="$emit('action')">
        {{ actionText }}
      </van-button>
    </div>
  </div>
</template>

<script setup>
defineProps({
  type: {
    type: String,
    default: 'default' // default, order, service, search, network
  },
  title: {
    type: String,
    default: ''
  },
  description: {
    type: String,
    default: ''
  },
  actionText: {
    type: String,
    default: ''
  }
})

defineEmits(['action'])

const getIcon = () => {
  const icons = {
    default: '📭',
    order: '📋',
    service: '🧹',
    search: '🔍',
    network: '📡',
    error: '😅'
  }
  return icons[type] || icons.default
}
</script>

<style scoped>
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  text-align: center;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
  animation: bounce 2s ease-in-out infinite;
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.empty-title {
  font-size: 16px;
  color: #333;
  font-weight: 500;
  margin-bottom: 8px;
}

.empty-desc {
  font-size: 13px;
  color: #999;
  margin-bottom: 16px;
  max-width: 200px;
}

.empty-action {
  margin-top: 8px;
}
</style>
