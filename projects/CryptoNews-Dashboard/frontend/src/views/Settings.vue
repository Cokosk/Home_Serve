<template>
  <div class="settings">
    <el-card>
      <template #header>
        <span>⚙️ 系统设置</span>
      </template>
      
      <el-form label-width="120px" class="settings-form">
        <h4>数据源配置</h4>
        <el-form-item label="新闻源">
          <el-checkbox-group v-model="settings.newsSources">
            <el-checkbox label="CoinDesk" />
            <el-checkbox label="CoinTelegraph" />
            <el-checkbox label="CryptoNews" />
            <el-checkbox label="Reddit" />
          </el-checkbox-group>
        </el-form-item>
        
        <el-form-item label="刷新频率">
          <el-select v-model="settings.refreshInterval" style="width: 200px;">
            <el-option label="每 5 分钟" :value="5" />
            <el-option label="每 15 分钟" :value="15" />
            <el-option label="每 30 分钟" :value="30" />
            <el-option label="每 1 小时" :value="60" />
          </el-select>
        </el-form-item>

        <el-divider />

        <h4>AI 配置</h4>
        <el-form-item label="AI 提供商">
          <el-select v-model="settings.aiProvider" style="width: 200px;">
            <el-option label="OpenAI" value="openai" />
            <el-option label="通义千问" value="qwen" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="API Key">
          <el-input 
            v-model="settings.apiKey" 
            type="password" 
            placeholder="请输入 API Key"
            style="width: 300px;"
            show-password
          />
        </el-form-item>

        <el-divider />

        <h4>通知设置</h4>
        <el-form-item label="价格预警">
          <el-switch v-model="settings.priceAlert" />
        </el-form-item>
        
        <el-form-item label="新闻推送">
          <el-switch v-model="settings.newsPush" />
        </el-form-item>

        <el-divider />

        <el-form-item>
          <el-button type="primary" @click="saveSettings">保存设置</el-button>
          <el-button @click="resetSettings">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { ElMessage } from 'element-plus'

const settings = reactive({
  newsSources: ['CoinDesk', 'CoinTelegraph'],
  refreshInterval: 15,
  aiProvider: 'openai',
  apiKey: '',
  priceAlert: true,
  newsPush: false
})

const saveSettings = () => {
  // TODO: 保存到后端或 localStorage
  ElMessage.success('设置已保存')
}

const resetSettings = () => {
  settings.newsSources = ['CoinDesk', 'CoinTelegraph']
  settings.refreshInterval = 15
  settings.aiProvider = 'openai'
  settings.apiKey = ''
  settings.priceAlert = true
  settings.newsPush = false
  ElMessage.info('设置已重置')
}
</script>

<style scoped>
.settings-form {
  max-width: 600px;
}

.settings-form h4 {
  color: #00d9ff;
  margin-bottom: 16px;
}
</style>