<template>
  <div class="settings-page">
    <van-nav-bar title="设置" left-arrow @click-left="goBack" />

    <div class="settings-content">
      <!-- 账号设置 -->
      <van-cell-group inset title="账号设置">
        <van-cell title="个人信息" is-link to="/profile" />
        <van-cell title="账号安全" is-link to="/security" />
        <van-cell title="收货地址" is-link to="/address" />
      </van-cell-group>

      <!-- 通用设置 -->
      <van-cell-group inset title="通用设置">
        <van-cell title="消息通知">
          <template #right-icon>
            <van-switch v-model="settings.notification" size="22" />
          </template>
        </van-cell>
        <van-cell title="深色模式">
          <template #right-icon>
            <van-switch v-model="settings.darkMode" size="22" />
          </template>
        </van-cell>
        <van-cell title="语言设置" is-link value="简体中文" />
      </van-cell-group>

      <!-- 关于 -->
      <van-cell-group inset title="关于">
        <van-cell title="版本信息" value="v1.0.0" />
        <van-cell title="用户协议" is-link to="/agreement?type=user" />
        <van-cell title="隐私政策" is-link to="/agreement?type=privacy" />
        <van-cell title="关于我们" is-link to="/about" />
      </van-cell-group>

      <!-- 退出登录 -->
      <div class="logout-section">
        <van-button type="danger" block round @click="handleLogout">
          退出登录
        </van-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { showConfirmDialog, showToast } from 'vant'

const router = useRouter()
const userStore = useUserStore()

const settings = ref({
  notification: true,
  darkMode: false
})

const goBack = () => router.back()

const handleLogout = async () => {
  try {
    await showConfirmDialog({
      title: '退出登录',
      message: '确定要退出登录吗？'
    })
    userStore.logout()
    showToast('已退出登录')
    router.push('/login')
  } catch (e) {
    // 取消
  }
}
</script>

<style scoped>
.settings-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.settings-content {
  padding: 12px;
}

.van-cell-group {
  margin-bottom: 12px;
}

.logout-section {
  margin-top: 20px;
  padding: 0 16px;
}
</style>
