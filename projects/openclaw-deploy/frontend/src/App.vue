<template>
  <div id="app">
    <nav class="navbar">
      <div class="nav-brand">
        <span class="logo">🦞</span>
        <span class="title">OpenClaw Deploy</span>
      </div>
      <div class="nav-links">
        <router-link to="/">首页</router-link>
        <router-link to="/deploy">部署</router-link>
        <router-link to="/docs">文档</router-link>
        <router-link to="/pricing">定价</router-link>
      </div>
    </nav>
    <router-view />
    <footer class="footer">
      <p>© 2025 OpenClaw Deploy. Powered by 🦞 麻辣小龙虾</p>
    </footer>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  min-height: 100vh;
}

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.navbar {
  background: rgba(255, 255, 255, 0.95);
  padding: 1rem 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.nav-brand {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.logo {
  font-size: 2rem;
}

.title {
  font-size: 1.5rem;
  font-weight: bold;
  color: #333;
}

.nav-links {
  display: flex;
  gap: 2rem;
}

.nav-links a {
  text-decoration: none;
  color: #666;
  font-weight: 500;
  transition: color 0.3s;
}

.nav-links a:hover,
.nav-links a.router-link-active {
  color: #667eea;
}

.footer {
  background: rgba(0, 0, 0, 0.8);
  color: white;
  text-align: center;
  padding: 1.5rem;
  margin-top: auto;
}

.footer p {
  opacity: 0.8;
}
</style>