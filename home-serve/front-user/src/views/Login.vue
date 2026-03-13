<template>
  <div class="login-page">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>

    <!-- Logo区域 -->
    <div class="logo-section">
      <div class="logo-icon">🦞</div>
      <h1 class="logo-text">勤家家政</h1>
      <p class="logo-slogan">让生活更美好，让服务更简单</p>
    </div>

    <!-- 登录表单 -->
    <div class="login-card">
      <!-- 切换登录方式 -->
      <van-tabs v-model:active="loginType" shrink>
        <van-tab title="密码登录" name="password" />
        <van-tab title="验证码登录" name="sms" />
      </van-tabs>

      <!-- 密码登录 -->
      <van-form v-if="loginType === 'password'" @submit="handlePasswordLogin">
        <van-field
          v-model="passwordForm.phone"
          type="tel"
          name="phone"
          label="手机号"
          placeholder="请输入手机号"
          left-icon="phone-o"
          :rules="[{ required: true, message: '请输入手机号' }, { pattern: /^1\d{10}$/, message: '手机号格式不正确' }]"
        />
        <van-field
          v-model="passwordForm.password"
          type="password"
          name="password"
          label="密码"
          placeholder="请输入密码"
          left-icon="lock"
          :rules="[{ required: true, message: '请输入密码' }]"
        />
        <div class="form-extra">
          <router-link to="/forgot-password" class="link">忘记密码？</router-link>
        </div>
        <div class="submit-btn">
          <van-button round block type="primary" native-type="submit" :loading="loading">
            登录
          </van-button>
        </div>
      </van-form>

      <!-- 验证码登录 -->
      <van-form v-else @submit="handleSmsLogin">
        <van-field
          v-model="smsForm.phone"
          type="tel"
          name="phone"
          label="手机号"
          placeholder="请输入手机号"
          left-icon="phone-o"
          :rules="[{ required: true, message: '请输入手机号' }, { pattern: /^1\d{10}$/, message: '手机号格式不正确' }]"
        />
        <van-field
          v-model="smsForm.code"
          type="digit"
          name="code"
          label="验证码"
          placeholder="请输入验证码"
          left-icon="shield-o"
          maxlength="6"
          :rules="[{ required: true, message: '请输入验证码' }]"
        >
          <template #button>
            <van-button 
              size="small" 
              type="primary" 
              plain 
              round 
              :disabled="countdown > 0"
              @click="sendSmsCode"
            >
              {{ countdown > 0 ? `${countdown}s后重发` : '获取验证码' }}
            </van-button>
          </template>
        </van-field>
        <div class="submit-btn">
          <van-button round block type="primary" native-type="submit" :loading="loading">
            登录
          </van-button>
        </div>
      </van-form>

      <!-- 其他登录方式 -->
      <div class="other-login">
        <div class="divider">
          <span>其他登录方式</span>
        </div>
        <div class="social-login">
          <div class="social-item" @click="handleWechatLogin">
            <div class="social-icon wechat">
              <van-icon name="chat" size="24" />
            </div>
            <span>微信登录</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 注册链接 -->
    <div class="register-link">
      还没有账号？<router-link to="/register" class="link">立即注册</router-link>
    </div>

    <!-- 用户协议 -->
    <div class="agreement">
      登录即表示同意
      <router-link to="/agreement?type=user" class="link">《用户协议》</router-link>
      和
      <router-link to="/agreement?type=privacy" class="link">《隐私政策》</router-link>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../stores/user'
import { showToast } from 'vant'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loginType = ref('password')
const loading = ref(false)
const countdown = ref(0)

const passwordForm = ref({
  phone: '',
  password: ''
})

const smsForm = ref({
  phone: '',
  code: ''
})

// 密码登录
const handlePasswordLogin = async () => {
  loading.value = true
  try {
    const res = await userStore.login(passwordForm.value)
    showToast('登录成功')
    router.push(route.query.redirect || '/')
  } catch (error) {
    showToast(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}

// 验证码登录
const handleSmsLogin = async () => {
  loading.value = true
  try {
    // 模拟验证码登录
    await userStore.smsLogin(smsForm.value)
    showToast('登录成功')
    router.push(route.query.redirect || '/')
  } catch (error) {
    showToast(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}

// 发送验证码
const sendSmsCode = async () => {
  if (!smsForm.value.phone || !/^1\d{10}$/.test(smsForm.value.phone)) {
    showToast('请输入正确的手机号')
    return
  }
  try {
    // 调用发送验证码接口
    showToast('验证码已发送')
    countdown.value = 60
    const timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        clearInterval(timer)
      }
    }, 1000)
  } catch (error) {
    showToast('发送失败，请重试')
  }
}

// 微信登录
const handleWechatLogin = () => {
  showToast('微信登录功能开发中')
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  padding: 40px 20px;
  background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%);
  position: relative;
  overflow: hidden;
}

/* 背景装饰 */
.bg-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
}

.circle-1 {
  width: 200px;
  height: 200px;
  top: -50px;
  right: -50px;
}

.circle-2 {
  width: 150px;
  height: 150px;
  bottom: 200px;
  left: -50px;
}

.circle-3 {
  width: 100px;
  height: 100px;
  bottom: 100px;
  right: 30px;
}

/* Logo */
.logo-section {
  text-align: center;
  margin-bottom: 30px;
  position: relative;
  z-index: 2;
}

.logo-icon {
  font-size: 60px;
  margin-bottom: 10px;
  animation: bounce 2s ease-in-out infinite;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.logo-text {
  font-size: 28px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 8px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.logo-slogan {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.9);
}

/* 登录卡片 */
.login-card {
  background: #fff;
  border-radius: 20px;
  padding: 20px 16px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
  position: relative;
  z-index: 2;
}

.login-card :deep(.van-tabs__nav) {
  background: transparent;
}

.login-card :deep(.van-tab--active) {
  color: #FF6B35;
  font-weight: 600;
}

.login-card :deep(.van-tabs__line) {
  background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%);
  height: 3px;
  border-radius: 2px;
}

.login-card :deep(.van-cell) {
  padding: 14px 16px;
}

.login-card :deep(.van-field__label) {
  width: 60px;
  color: #333;
}

.form-extra {
  display: flex;
  justify-content: flex-end;
  padding: 8px 16px;
}

.link {
  color: #FF6B35;
  font-size: 13px;
}

.submit-btn {
  padding: 20px 16px;
}

.submit-btn :deep(.van-button--primary) {
  background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%);
  border: none;
  height: 48px;
  font-size: 16px;
  font-weight: 500;
}

/* 其他登录方式 */
.other-login {
  margin-top: 20px;
}

.divider {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.divider::before,
.divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background: #eee;
}

.divider span {
  padding: 0 16px;
  font-size: 12px;
  color: #999;
}

.social-login {
  display: flex;
  justify-content: center;
  gap: 40px;
}

.social-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.social-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.social-icon.wechat {
  background: #07C160;
  color: #fff;
}

.social-item span {
  font-size: 12px;
  color: #666;
}

/* 注册链接 */
.register-link {
  text-align: center;
  margin-top: 20px;
  color: #fff;
  font-size: 14px;
}

.register-link .link {
  color: #fff;
  font-weight: 500;
  text-decoration: underline;
}

/* 用户协议 */
.agreement {
  text-align: center;
  margin-top: 30px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.8);
  position: relative;
  z-index: 2;
}

.agreement .link {
  color: #fff;
}
</style>
