const express = require('express');
const path = require('path');
const app = express();
const PORT = 10080;

// 静态文件
app.use(express.static(path.join(__dirname, 'frontend/dist')));

// SPA 路由
app.use((req, res) => {
  res.sendFile(path.join(__dirname, 'frontend/dist', 'index.html'));
});

app.listen(PORT, '0.0.0.0', () => {
  console.log(`🦞 Frontend running on port ${PORT}`);
});