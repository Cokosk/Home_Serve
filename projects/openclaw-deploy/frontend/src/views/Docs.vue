<template>
  <div class="docs-page">
    <div class="container">
      <h1>使用文档</h1>
      
      <div class="doc-section">
        <h2>🚀 快速开始</h2>
        <ol>
          <li>点击「部署」页面</li>
          <li>选择适合的套餐</li>
          <li>填写邮箱和配置</li>
          <li>点击「立即部署」</li>
          <li>等待 2-5 分钟，收到邮件通知</li>
          <li>访问分配的地址，开始使用</li>
        </ol>
      </div>

      <div class="doc-section">
        <h2>🔧 配置说明</h2>
        <h3>环境变量</h3>
        <table>
          <thead>
            <tr>
              <th>变量名</th>
              <th>说明</th>
              <th>示例</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td><code>OPENAI_API_KEY</code></td>
              <td>OpenAI API 密钥</td>
              <td>sk-xxx</td>
            </tr>
            <tr>
              <td><code>BRAVE_API_KEY</code></td>
              <td>Brave Search API 密钥</td>
              <td>xxx</td>
            </tr>
            <tr>
              <td><code>TELEGRAM_BOT_TOKEN</code></td>
              <td>Telegram 机器人 Token</td>
              <td>123:ABC</td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="doc-section">
        <h2>📚 Skills 说明</h2>
        <div class="skill-list">
          <div class="skill-item">
            <h4>weather</h4>
            <p>天气查询，支持当前天气和预报</p>
          </div>
          <div class="skill-item">
            <h4>clawhub</h4>
            <p>Skills 管理，搜索安装更多技能</p>
          </div>
          <div class="skill-item">
            <h4>tmux</h4>
            <p>远程控制 tmux 会话</p>
          </div>
          <div class="skill-item">
            <h4>morning-routine</h4>
            <p>早晨习惯追踪和打卡</p>
          </div>
        </div>
      </div>

      <div class="doc-section">
        <h2>❓ 常见问题</h2>
        <div class="faq">
          <div class="faq-item">
            <h4>部署需要多长时间？</h4>
            <p>通常 2-5 分钟即可完成部署。</p>
          </div>
          <div class="faq-item">
            <h4>如何升级套餐？</h4>
            <p>联系客服，支持平滑升级，数据不丢失。</p>
          </div>
          <div class="faq-item">
            <h4>支持哪些支付方式？</h4>
            <p>支持支付宝、微信支付。</p>
          </div>
        </div>
      </div>

      <div class="doc-section">
        <h2>📞 联系支持</h2>
        <p>邮箱：support@openclaw-deploy.com</p>
        <p>微信：OpenClawSupport</p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Docs'
}
</script>

<style scoped>
.docs-page {
  background: white;
  min-height: calc(100vh - 150px);
  padding: 3rem 1rem;
}

.container {
  max-width: 900px;
  margin: 0 auto;
}

h1 {
  color: #333;
  margin-bottom: 2rem;
}

.doc-section {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 2rem;
  margin-bottom: 2rem;
}

.doc-section h2 {
  color: #333;
  margin-bottom: 1rem;
}

.doc-section h3 {
  color: #555;
  margin: 1.5rem 0 0.5rem;
}

.doc-section ol {
  padding-left: 1.5rem;
}

.doc-section li {
  margin-bottom: 0.5rem;
  color: #555;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 1rem;
}

th, td {
  padding: 0.8rem;
  text-align: left;
  border-bottom: 1px solid #e0e0e0;
}

th {
  background: #e9ecef;
  font-weight: 600;
}

code {
  background: #e9ecef;
  padding: 0.2rem 0.5rem;
  border-radius: 4px;
  font-family: monospace;
}

.skill-list, .faq {
  display: grid;
  gap: 1rem;
}

.skill-item, .faq-item {
  background: white;
  padding: 1rem;
  border-radius: 8px;
}

.skill-item h4, .faq-item h4 {
  color: #667eea;
  margin-bottom: 0.5rem;
}

.skill-item p, .faq-item p {
  color: #666;
  font-size: 0.95rem;
}
</style>