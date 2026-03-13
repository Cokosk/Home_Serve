<template>
  <div class="messages-page">
    <van-nav-bar title="消息中心">
      <template #right>
        <span class="read-all" @click="readAll">全部已读</span>
      </template>
    </van-nav-bar>

    <!-- 消息分类 -->
    <van-tabs v-model:active="activeTab" sticky>
      <van-tab title="全部" name="all" />
      <van-tab title="系统通知" name="system" />
      <van-tab title="订单消息" name="order" />
      <van-tab title="活动优惠" name="activity" />
    </van-tabs>

    <!-- 消息列表 -->
    <div class="message-list">
      <div 
        class="message-item" 
        v-for="msg in filteredMessages" 
        :key="msg.id"
        :class="{ unread: !msg.isRead }"
        @click="readMessage(msg)"
      >
        <div class="msg-icon" :class="msg.type">
          <van-icon :name="getMsgIcon(msg.type)" />
        </div>
        <div class="msg-content">
          <div class="msg-header">
            <span class="msg-title">{{ msg.title }}</span>
            <span class="msg-time">{{ msg.time }}</span>
          </div>
          <div class="msg-text">{{ msg.content }}</div>
        </div>
        <div class="msg-dot" v-if="!msg.isRead"></div>
      </div>
      
      <van-empty v-if="filteredMessages.length === 0" description="暂无消息" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { showToast } from 'vant'

const activeTab = ref('all')

const messages = ref([
  { id: 1, type: 'system', title: '系统通知', content: '您的账号已通过实名认证', time: '10:30', isRead: false },
  { id: 2, type: 'order', title: '订单消息', content: '您的订单已接单，服务人员正在前往', time: '昨天', isRead: false },
  { id: 3, type: 'activity', title: '活动优惠', content: '春季大促！保洁服务限时8折', time: '03-10', isRead: true },
  { id: 4, type: 'order', title: '订单消息', content: '订单已完成，快去评价吧~', time: '03-08', isRead: true }
])

const filteredMessages = computed(() => {
  if (activeTab.value === 'all') return messages.value
  return messages.value.filter(m => m.type === activeTab.value)
})

const getMsgIcon = (type) => {
  const icons = { system: 'info-o', order: 'orders-o', activity: 'gift-o' }
  return icons[type] || 'comment-o'
}

const readMessage = (msg) => {
  msg.isRead = true
}

const readAll = () => {
  messages.value.forEach(m => m.isRead = true)
  showToast('已全部标记为已读')
}
</script>

<style scoped>
.messages-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.read-all {
  font-size: 13px;
  color: #FF6B35;
}

.message-list {
  padding: 12px;
}

.message-item {
  display: flex;
  gap: 12px;
  background: #fff;
  padding: 14px;
  border-radius: 12px;
  margin-bottom: 10px;
  position: relative;
}

.message-item.unread {
  background: #FFF9F5;
}

.msg-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
}

.msg-icon.system { background: #E6F7FF; color: #1890FF; }
.msg-icon.order { background: #FFF7E6; color: #FA8C16; }
.msg-icon.activity { background: #FFF1F0; color: #FF4D4F; }

.msg-content {
  flex: 1;
}

.msg-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.msg-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.msg-time {
  font-size: 11px;
  color: #999;
}

.msg-text {
  font-size: 13px;
  color: #666;
  line-height: 1.5;
}

.msg-dot {
  position: absolute;
  top: 14px;
  right: 14px;
  width: 8px;
  height: 8px;
  background: #FF4D4F;
  border-radius: 50%;
}
</style>
