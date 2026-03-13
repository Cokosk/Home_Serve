const express = require('express');
const cors = require('cors');

const app = express();
const PORT = 18080;

app.use(cors());
app.use(express.json());

// 模拟数据库（内存存储）
const orders = [];
let orderId = 1;

// 部署 API
app.post('/api/deploy', (req, res) => {
  const { email, plan, notes, skills } = req.body;
  
  if (!email) {
    return res.status(400).json({ success: false, message: '邮箱不能为空' });
  }
  
  const order = {
    id: orderId++,
    email,
    plan: plan || 'free',
    notes: notes || '',
    skills: JSON.stringify(skills || []),
    status: 'pending',
    accessUrl: `http://101.200.180.182:10080/instance/${orderId}`,
    containerId: `container-${Date.now()}`,
    createdAt: new Date().toISOString(),
    completedAt: null
  };
  
  orders.push(order);
  
  console.log('新订单:', order);
  
  res.json({ success: true, message: '部署申请已提交', data: order });
});

// 查询订单
app.get('/api/orders', (req, res) => {
  const { email } = req.query;
  const filtered = email ? orders.filter(o => o.email === email) : orders;
  res.json({ success: true, data: filtered });
});

// 健康检查
app.get('/api/health', (req, res) => {
  res.json({ success: true, data: 'OK' });
});

app.listen(PORT, '0.0.0.0', () => {
  console.log(`🦞 OpenClaw Deploy Backend running on port ${PORT}`);
});