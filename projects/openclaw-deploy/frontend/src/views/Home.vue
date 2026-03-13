<template>
  <div class="home">
    <!-- Hero Section -->
    <section class="hero">
      <div class="hero-content">
        <h1>一键部署你的 AI 助手</h1>
        <p class="subtitle">OpenClaw 自动化部署平台，无需技术背景，3分钟启动你的专属 AI 助手</p>
        <div class="cta-buttons">
          <router-link to="/deploy" class="btn btn-primary">立即部署</router-link>
          <router-link to="/docs" class="btn btn-secondary">查看文档</router-link>
        </div>
        <div class="stats">
          <div class="stat">
            <span class="stat-number">2 min</span>
            <span class="stat-label">平均部署时间</span>
          </div>
          <div class="stat">
            <span class="stat-number">99.9%</span>
            <span class="stat-label">服务可用性</span>
          </div>
          <div class="stat">
            <span class="stat-number">24/7</span>
            <span class="stat-label">技术支持</span>
          </div>
        </div>
      </div>
    </section>

    <!-- Features Section -->
    <section class="features">
      <h2>核心功能</h2>
      <div class="feature-grid">
        <div class="feature-card">
          <div class="feature-icon">⚡</div>
          <h3>一键部署</h3>
          <p>无需复杂配置，点击按钮即可完成部署</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">🔧</div>
          <h3>可视化配置</h3>
          <p>图形化界面管理 Skills 和环境变量</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">📊</div>
          <h3>实时监控</h3>
          <p>服务状态、资源使用一目了然</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">🔔</div>
          <h3>多渠道通知</h3>
          <p>支持微信、Telegram、邮件等通知方式</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">🔒</div>
          <h3>安全可靠</h3>
          <p>数据隔离、HTTPS 加密、自动备份</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">📈</div>
          <h3>弹性扩展</h3>
          <p>按需升级配置，支持自动扩缩容</p>
        </div>
      </div>
    </section>

    <!-- How it Works -->
    <section class="how-it-works">
      <h2>如何工作</h2>
      <div class="steps">
        <div class="step">
          <div class="step-number">1</div>
          <h3>选择套餐</h3>
          <p>根据需求选择合适的配置</p>
        </div>
        <div class="step-arrow">→</div>
        <div class="step">
          <div class="step-number">2</div>
          <h3>配置服务</h3>
          <p>选择需要的 Skills 和通知渠道</p>
        </div>
        <div class="step-arrow">→</div>
        <div class="step">
          <div class="step-number">3</div>
          <h3>一键部署</h3>
          <p>点击部署，等待 2-5 分钟</p>
        </div>
        <div class="step-arrow">→</div>
        <div class="step">
          <div class="step-number">4</div>
          <h3>开始使用</h3>
          <p>获取访问地址，开始对话</p>
        </div>
      </div>
    </section>

    <!-- CTA Section -->
    <section class="cta">
      <h2>准备好开始了吗？</h2>
      <p>免费试用，无需信用卡</p>
      <router-link to="/deploy" class="btn btn-primary btn-large">免费部署</router-link>
    </section>
  </div>
</template>

<script>
export default {
  name: 'Home'
}
</script>

<style scoped>
.home {
  flex: 1;
}

.hero {
  padding: 6rem 2rem;
  text-align: center;
  color: white;
}

.hero-content {
  max-width: 800px;
  margin: 0 auto;
}

.hero h1 {
  font-size: 3rem;
  margin-bottom: 1.5rem;
  text-shadow: 2px 2px 10px rgba(0, 0, 0, 0.3);
}

.subtitle {
  font-size: 1.3rem;
  margin-bottom: 2rem;
  opacity: 0.95;
  line-height: 1.6;
}

.cta-buttons {
  display: flex;
  gap: 1rem;
  justify-content: center;
  margin-bottom: 3rem;
}

.btn {
  padding: 1rem 2rem;
  border-radius: 8px;
  text-decoration: none;
  font-weight: bold;
  transition: all 0.3s;
  display: inline-block;
}

.btn-primary {
  background: white;
  color: #667eea;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 20px rgba(255, 255, 255, 0.3);
}

.btn-secondary {
  background: transparent;
  color: white;
  border: 2px solid white;
}

.btn-secondary:hover {
  background: rgba(255, 255, 255, 0.1);
}

.btn-large {
  padding: 1.2rem 3rem;
  font-size: 1.1rem;
}

.stats {
  display: flex;
  gap: 3rem;
  justify-content: center;
}

.stat {
  text-align: center;
}

.stat-number {
  display: block;
  font-size: 2rem;
  font-weight: bold;
}

.stat-label {
  font-size: 0.9rem;
  opacity: 0.9;
}

.features {
  background: white;
  padding: 4rem 2rem;
}

.features h2 {
  text-align: center;
  font-size: 2rem;
  margin-bottom: 3rem;
  color: #333;
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.feature-card {
  padding: 2rem;
  border-radius: 12px;
  background: #f8f9fa;
  text-align: center;
  transition: transform 0.3s;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.feature-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.feature-card h3 {
  font-size: 1.2rem;
  margin-bottom: 0.5rem;
  color: #333;
}

.feature-card p {
  color: #666;
  line-height: 1.6;
}

.how-it-works {
  background: #f8f9fa;
  padding: 4rem 2rem;
}

.how-it-works h2 {
  text-align: center;
  font-size: 2rem;
  margin-bottom: 3rem;
  color: #333;
}

.steps {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
  flex-wrap: wrap;
  max-width: 1200px;
  margin: 0 auto;
}

.step {
  text-align: center;
  padding: 1.5rem;
}

.step-number {
  width: 50px;
  height: 50px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  font-weight: bold;
  margin: 0 auto 1rem;
}

.step h3 {
  color: #333;
  margin-bottom: 0.5rem;
}

.step p {
  color: #666;
  font-size: 0.9rem;
}

.step-arrow {
  font-size: 2rem;
  color: #667eea;
}

.cta {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 4rem 2rem;
  text-align: center;
  color: white;
}

.cta h2 {
  font-size: 2rem;
  margin-bottom: 1rem;
}

.cta p {
  margin-bottom: 2rem;
  opacity: 0.9;
}
</style>