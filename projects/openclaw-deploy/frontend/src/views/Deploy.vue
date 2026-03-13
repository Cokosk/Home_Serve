<template>
  <div class="deploy-page">
    <div class="container">
      <h1>部署 OpenClaw</h1>
      
      <div class="deploy-form">
        <!-- Step 1: 选择套餐 -->
        <div class="form-section">
          <h2><span class="step-badge">1</span> 选择套餐</h2>
          <div class="plan-grid">
            <div class="plan-card" :class="{ active: selectedPlan === 'free' }" @click="selectedPlan = 'free'">
              <h3>免费版</h3>
              <div class="price">¥0<span>/月</span></div>
              <ul>
                <li>✓ 1核 CPU</li>
                <li>✓ 1GB 内存</li>
                <li>✓ 基础 Skills</li>
                <li>✓ 社区支持</li>
              </ul>
            </div>
            <div class="plan-card" :class="{ active: selectedPlan === 'basic' }" @click="selectedPlan = 'basic'">
              <div class="recommend">推荐</div>
              <h3>基础版</h3>
              <div class="price">¥49<span>/月</span></div>
              <ul>
                <li>✓ 2核 CPU</li>
                <li>✓ 2GB 内存</li>
                <li>✓ 所有 Skills</li>
                <li>✓ 邮件支持</li>
              </ul>
            </div>
            <div class="plan-card" :class="{ active: selectedPlan === 'pro' }" @click="selectedPlan = 'pro'">
              <h3>专业版</h3>
              <div class="price">¥99<span>/月</span></div>
              <ul>
                <li>✓ 4核 CPU</li>
                <li>✓ 4GB 内存</li>
                <li>✓ 所有 Skills</li>
                <li>✓ 优先支持</li>
              </ul>
            </div>
          </div>
        </div>

        <!-- Step 2: 配置信息 -->
        <div class="form-section">
          <h2><span class="step-badge">2</span> 配置信息</h2>
          <div class="form-group">
            <label>联系人邮箱 *</label>
            <input type="email" v-model="email" placeholder="your@email.com" required>
          </div>
          <div class="form-group">
            <label>备注信息</label>
            <textarea v-model="notes" placeholder="其他需求或说明..."></textarea>
          </div>
        </div>

        <!-- Step 3: 选择 Skills -->
        <div class="form-section">
          <h2><span class="step-badge">3</span> 选择 Skills（可选）</h2>
          <div class="skills-grid">
            <label class="skill-checkbox" v-for="skill in availableSkills" :key="skill.id">
              <input type="checkbox" :value="skill.id" v-model="selectedSkills">
              <span class="skill-name">{{ skill.name }}</span>
              <span class="skill-desc">{{ skill.desc }}</span>
            </label>
          </div>
        </div>

        <!-- Submit -->
        <div class="form-section">
          <button class="btn-deploy" @click="submitDeploy" :disabled="deploying">
            {{ deploying ? '部署中...' : '立即部署' }}
          </button>
          <p class="deploy-note">部署完成后，访问地址将发送至您的邮箱</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'Deploy',
  data() {
    return {
      selectedPlan: 'free',
      email: '',
      notes: '',
      selectedSkills: ['weather', 'clawhub'],
      deploying: false,
      availableSkills: [
        { id: 'weather', name: '天气查询', desc: '获取天气和预报' },
        { id: 'clawhub', name: 'ClawHub', desc: 'Skills 管理' },
        { id: 'tmux', name: 'Tmux 控制', desc: '远程 tmux 会话' },
        { id: 'morning-routine', name: '早晨习惯', desc: '习惯追踪' },
        { id: 'daily-review', name: '每日复盘', desc: '每日总结' },
        { id: 'healthcheck', name: '健康检查', desc: '系统安全检查' }
      ]
    }
  },
  methods: {
    async submitDeploy() {
      if (!this.email) {
        alert('请填写邮箱地址')
        return
      }
      
      this.deploying = true
      
      try {
        const response = await axios.post('/api/deploy', {
          plan: this.selectedPlan,
          email: this.email,
          notes: this.notes,
          skills: this.selectedSkills
        })
        
        if (response.data.success) {
          alert('部署申请已提交！部署完成后将发送邮件通知您。')
          this.$router.push('/')
        }
      } catch (error) {
        alert('提交失败：' + error.message)
      } finally {
        this.deploying = false
      }
    }
  }
}
</script>

<style scoped>
.deploy-page {
  background: white;
  min-height: calc(100vh - 150px);
  padding: 3rem 1rem;
}

.container {
  max-width: 900px;
  margin: 0 auto;
}

h1 {
  text-align: center;
  color: #333;
  margin-bottom: 2rem;
}

.deploy-form {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 2rem;
}

.form-section {
  margin-bottom: 2.5rem;
}

.form-section h2 {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 1.3rem;
  color: #333;
  margin-bottom: 1rem;
}

.step-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 50%;
  font-size: 0.9rem;
  font-weight: bold;
}

.plan-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
}

.plan-card {
  background: white;
  border: 2px solid #e0e0e0;
  border-radius: 12px;
  padding: 1.5rem;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
}

.plan-card:hover {
  border-color: #667eea;
}

.plan-card.active {
  border-color: #667eea;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
}

.plan-card .recommend {
  position: absolute;
  top: -10px;
  right: 10px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 0.2rem 0.8rem;
  border-radius: 10px;
  font-size: 0.75rem;
}

.plan-card h3 {
  color: #333;
  margin-bottom: 0.5rem;
}

.plan-card .price {
  font-size: 2rem;
  font-weight: bold;
  color: #667eea;
  margin-bottom: 1rem;
}

.plan-card .price span {
  font-size: 1rem;
  color: #999;
}

.plan-card ul {
  list-style: none;
  text-align: left;
}

.plan-card li {
  padding: 0.3rem 0;
  color: #666;
  font-size: 0.9rem;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: #333;
  font-weight: 500;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 0.8rem 1rem;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 1rem;
  transition: border-color 0.3s;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #667eea;
}

.form-group textarea {
  min-height: 100px;
  resize: vertical;
}

.skills-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 0.8rem;
}

.skill-checkbox {
  display: flex;
  flex-direction: column;
  padding: 1rem;
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.skill-checkbox:has(input:checked) {
  border-color: #667eea;
  background: rgba(102, 126, 234, 0.05);
}

.skill-checkbox input {
  margin-bottom: 0.5rem;
}

.skill-name {
  font-weight: 500;
  color: #333;
}

.skill-desc {
  font-size: 0.85rem;
  color: #999;
}

.btn-deploy {
  width: 100%;
  padding: 1rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1.1rem;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-deploy:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 20px rgba(102, 126, 234, 0.4);
}

.btn-deploy:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.deploy-note {
  text-align: center;
  color: #999;
  font-size: 0.9rem;
  margin-top: 1rem;
}
</style>