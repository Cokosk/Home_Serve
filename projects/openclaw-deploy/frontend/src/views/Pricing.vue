<template>
  <div class="pricing-page">
    <div class="container">
      <h1>定价方案</h1>
      <p class="subtitle">选择适合你的方案，随时可以升级</p>
      
      <div class="pricing-grid">
        <div class="pricing-card">
          <h3>免费版</h3>
          <div class="price">¥0<span>/月</span></div>
          <p class="price-desc">适合个人体验</p>
          <ul class="features">
            <li>✓ 1核 CPU / 1GB 内存</li>
            <li>✓ 基础 Skills</li>
            <li>✓ 社区支持</li>
            <li>✓ HTTP 访问</li>
            <li>✗ 自定义域名</li>
            <li>✗ 自动备份</li>
          </ul>
          <router-link to="/deploy" class="btn">免费开始</router-link>
        </div>

        <div class="pricing-card recommended">
          <div class="badge">推荐</div>
          <h3>基础版</h3>
          <div class="price">¥49<span>/月</span></div>
          <p class="price-desc">适合个人日常使用</p>
          <ul class="features">
            <li>✓ 2核 CPU / 2GB 内存</li>
            <li>✓ 所有 Skills</li>
            <li>✓ 邮件支持</li>
            <li>✓ HTTPS 加密</li>
            <li>✓ 每周备份</li>
            <li>✓ 自定义域名</li>
          </ul>
          <router-link to="/deploy" class="btn btn-primary">选择方案</router-link>
        </div>

        <div class="pricing-card">
          <h3>专业版</h3>
          <div class="price">¥99<span>/月</span></div>
          <p class="price-desc">适合重度用户</p>
          <ul class="features">
            <li>✓ 4核 CPU / 4GB 内存</li>
            <li>✓ 所有 Skills</li>
            <li>✓ 优先技术支持</li>
            <li>✓ HTTPS 加密</li>
            <li>✓ 每日备份</li>
            <li>✓ 自定义域名</li>
          </ul>
          <router-link to="/deploy" class="btn">选择方案</router-link>
        </div>

        <div class="pricing-card">
          <h3>企业版</h3>
          <div class="price">¥299<span>/月</span></div>
          <p class="price-desc">适合团队和企业</p>
          <ul class="features">
            <li>✓ 8核 CPU / 8GB 内存</li>
            <li>✓ 所有 Skills</li>
            <li>✓ 专属技术支持</li>
            <li>✓ 私有化部署</li>
            <li>✓ 实时备份</li>
            <li>✓ SLA 保障</li>
          </ul>
          <router-link to="/deploy" class="btn">联系我们</router-link>
        </div>
      </div>

      <div class="faq-section">
        <h2>常见问题</h2>
        <div class="faq-grid">
          <div class="faq-item">
            <h4>可以随时升级吗？</h4>
            <p>是的，支持平滑升级，数据自动迁移。</p>
          </div>
          <div class="faq-item">
            <h4>支持退款吗？</h4>
            <p>7天内不满意可全额退款。</p>
          </div>
          <div class="faq-item">
            <h4>如何支付？</h4>
            <p>支持支付宝、微信支付、对公转账。</p>
          </div>
          <div class="faq-item">
            <h4>有年付优惠吗？</h4>
            <p>年付享8折优惠。</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Pricing'
}
</script>

<style scoped>
.pricing-page {
  background: white;
  min-height: calc(100vh - 150px);
  padding: 3rem 1rem;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
}

h1 {
  text-align: center;
  color: #333;
  margin-bottom: 0.5rem;
}

.subtitle {
  text-align: center;
  color: #666;
  margin-bottom: 3rem;
}

.pricing-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
  margin-bottom: 4rem;
}

.pricing-card {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 2rem;
  text-align: center;
  position: relative;
  border: 2px solid transparent;
}

.pricing-card.recommended {
  border-color: #667eea;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
}

.pricing-card .badge {
  position: absolute;
  top: -12px;
  left: 50%;
  transform: translateX(-50%);
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 0.3rem 1rem;
  border-radius: 20px;
  font-size: 0.85rem;
}

.pricing-card h3 {
  color: #333;
  margin-bottom: 0.5rem;
}

.pricing-card .price {
  font-size: 2.5rem;
  font-weight: bold;
  color: #667eea;
}

.pricing-card .price span {
  font-size: 1rem;
  color: #999;
}

.price-desc {
  color: #666;
  font-size: 0.9rem;
  margin-bottom: 1.5rem;
}

.features {
  list-style: none;
  text-align: left;
  margin-bottom: 1.5rem;
}

.features li {
  padding: 0.5rem 0;
  color: #666;
  border-bottom: 1px solid #e0e0e0;
}

.btn {
  display: inline-block;
  padding: 0.8rem 2rem;
  border-radius: 8px;
  text-decoration: none;
  font-weight: bold;
  transition: all 0.3s;
  background: #e9ecef;
  color: #333;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.15);
}

.faq-section {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 2rem;
}

.faq-section h2 {
  text-align: center;
  color: #333;
  margin-bottom: 2rem;
}

.faq-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1rem;
}

.faq-item {
  background: white;
  padding: 1.5rem;
  border-radius: 8px;
}

.faq-item h4 {
  color: #333;
  margin-bottom: 0.5rem;
}

.faq-item p {
  color: #666;
  font-size: 0.95rem;
}
</style>