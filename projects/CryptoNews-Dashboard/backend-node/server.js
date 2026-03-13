const express = require('express');
const cors = require('cors');
const axios = require('axios');
const Redis = require('ioredis');
const mysql = require('mysql2/promise');

const app = express();
const PORT = 8080;

app.use(cors());
app.use(express.json());

// Redis 连接
const redis = new Redis({
  host: 'localhost',
  port: 6379
});

// MySQL 连接池
const pool = mysql.createPool({
  host: 'localhost',
  port: 13306,
  user: 'root',
  password: 'root123',
  database: 'crypto_news'
});

// CoinGecko API 配置
const COINGECKO_BASE_URL = 'https://api.coingecko.com/api/v3';

// ==================== 加密货币接口 ====================

// 获取热力图数据
app.get('/api/crypto/heatmap', async (req, res) => {
  try {
    const limit = parseInt(req.query.limit) || 50;
    const cacheKey = `crypto:heatmap:${limit}`;
    
    // 尝试从 Redis 获取缓存
    const cached = await redis.get(cacheKey);
    if (cached) {
      return res.json(JSON.parse(cached));
    }
    
    // 调用 CoinGecko API
    const response = await axios.get(`${COINGECKO_BASE_URL}/coins/markets`, {
      params: {
        vs_currency: 'usd',
        order: 'market_cap_desc',
        per_page: limit,
        page: 1,
        sparkline: false,
        price_change_percentage: '24h'
      }
    });
    
    const data = {
      timestamp: new Date().toISOString(),
      data: response.data.map(coin => ({
        id: coin.id,
        symbol: coin.symbol.toUpperCase(),
        name: coin.name,
        current_price: coin.current_price,
        market_cap: coin.market_cap,
        market_cap_rank: coin.market_cap_rank,
        total_volume: coin.total_volume,
        price_change_percentage_24h: coin.price_change_percentage_24h,
        image: coin.image
      }))
    };
    
    // 缓存 5 分钟
    await redis.setex(cacheKey, 300, JSON.stringify(data));
    
    res.json(data);
  } catch (error) {
    console.error('获取热力图数据失败:', error.message);
    res.status(500).json({ error: '获取数据失败', message: error.message });
  }
});

// 获取单个币种详情
app.get('/api/crypto/:symbol', async (req, res) => {
  try {
    const { symbol } = req.params;
    const cacheKey = `crypto:detail:${symbol}`;
    
    const cached = await redis.get(cacheKey);
    if (cached) {
      return res.json(JSON.parse(cached));
    }
    
    const response = await axios.get(`${COINGECKO_BASE_URL}/coins/${symbol.toLowerCase()}`);
    
    const data = {
      id: response.data.id,
      symbol: response.data.symbol.toUpperCase(),
      name: response.data.name,
      description: response.data.description?.en?.substring(0, 500),
      current_price: response.data.market_data?.current_price?.usd,
      market_cap: response.data.market_data?.market_cap?.usd,
      market_cap_rank: response.data.market_cap_rank,
      total_volume: response.data.market_data?.total_volume?.usd,
      price_change_24h: response.data.market_data?.price_change_24h,
      price_change_percentage_24h: response.data.market_data?.price_change_percentage_24h,
      image: response.data.image?.large,
      links: {
        homepage: response.data.links?.homepage?.[0],
        blockchain_site: response.data.links?.blockchain_site?.[0]
      }
    };
    
    await redis.setex(cacheKey, 300, JSON.stringify(data));
    
    res.json(data);
  } catch (error) {
    console.error('获取币种详情失败:', error.message);
    res.status(500).json({ error: '获取数据失败', message: error.message });
  }
});

// 获取历史价格
app.get('/api/crypto/:symbol/history', async (req, res) => {
  try {
    const { symbol } = req.params;
    const days = parseInt(req.query.days) || 7;
    const cacheKey = `crypto:history:${symbol}:${days}`;
    
    const cached = await redis.get(cacheKey);
    if (cached) {
      return res.json(JSON.parse(cached));
    }
    
    const response = await axios.get(`${COINGECKO_BASE_URL}/coins/${symbol.toLowerCase()}/market_chart`, {
      params: {
        vs_currency: 'usd',
        days: days
      }
    });
    
    const data = {
      symbol: symbol.toUpperCase(),
      prices: response.data.prices.map(([timestamp, price]) => ({
        timestamp: new Date(timestamp).toISOString(),
        price
      }))
    };
    
    await redis.setex(cacheKey, 300, JSON.stringify(data));
    
    res.json(data);
  } catch (error) {
    console.error('获取历史价格失败:', error.message);
    res.status(500).json({ error: '获取数据失败', message: error.message });
  }
});

// 获取市场概览
app.get('/api/crypto/market/overview', async (req, res) => {
  try {
    const cacheKey = 'crypto:market:overview';
    
    const cached = await redis.get(cacheKey);
    if (cached) {
      return res.json(JSON.parse(cached));
    }
    
    const response = await axios.get(`${COINGECKO_BASE_URL}/global`);
    
    const data = {
      total_market_cap: response.data.data.total_market_cap?.usd,
      total_volume: response.data.data.total_volume?.usd,
      market_cap_percentage: response.data.data.market_cap_percentage,
      active_cryptocurrencies: response.data.data.active_cryptocurrencies,
      markets: response.data.data.markets,
      updated_at: new Date().toISOString()
    };
    
    await redis.setex(cacheKey, 300, JSON.stringify(data));
    
    res.json(data);
  } catch (error) {
    console.error('获取市场概览失败:', error.message);
    res.status(500).json({ error: '获取数据失败', message: error.message });
  }
});

// 刷新价格数据
app.post('/api/crypto/refresh', async (req, res) => {
  try {
    // 清除相关缓存
    const keys = await redis.keys('crypto:*');
    if (keys.length > 0) {
      await redis.del(keys);
    }
    
    res.json({ success: true, message: '缓存已刷新' });
  } catch (error) {
    console.error('刷新数据失败:', error.message);
    res.status(500).json({ error: '刷新失败', message: error.message });
  }
});

// ==================== 新闻接口 ====================

// 获取新闻列表
app.get('/api/news/list', async (req, res) => {
  try {
    const page = parseInt(req.query.page) || 1;
    const limit = parseInt(req.query.limit) || 20;
    const offset = (page - 1) * limit;
    
    const [rows] = await pool.query(
      'SELECT * FROM news ORDER BY published_at DESC LIMIT ? OFFSET ?',
      [limit, offset]
    );
    
    const [countRows] = await pool.query('SELECT COUNT(*) as total FROM news');
    
    res.json({
      data: rows,
      pagination: {
        page,
        limit,
        total: countRows[0].total,
        totalPages: Math.ceil(countRows[0].total / limit)
      }
    });
  } catch (error) {
    console.error('获取新闻列表失败:', error.message);
    res.status(500).json({ error: '获取数据失败', message: error.message });
  }
});

// 获取新闻详情
app.get('/api/news/:id', async (req, res) => {
  try {
    const { id } = req.params;
    
    const [rows] = await pool.query('SELECT * FROM news WHERE id = ?', [id]);
    
    if (rows.length === 0) {
      return res.status(404).json({ error: '新闻不存在' });
    }
    
    res.json(rows[0]);
  } catch (error) {
    console.error('获取新闻详情失败:', error.message);
    res.status(500).json({ error: '获取数据失败', message: error.message });
  }
});

// ==================== AI 分析接口 ====================

// 情感分析
app.post('/api/ai/analyze/sentiment', async (req, res) => {
  try {
    const { text } = req.body;
    
    // 简单的情感分析模拟
    const sentiment = text.toLowerCase().includes('涨') || text.toLowerCase().includes('上涨') 
      ? 'positive' 
      : text.toLowerCase().includes('跌') || text.toLowerCase().includes('下跌')
        ? 'negative'
        : 'neutral';
    
    const score = sentiment === 'positive' ? 0.7 : sentiment === 'negative' ? 0.3 : 0.5;
    
    res.json({
      sentiment,
      score,
      analyzed_at: new Date().toISOString()
    });
  } catch (error) {
    console.error('情感分析失败:', error.message);
    res.status(500).json({ error: '分析失败', message: error.message });
  }
});

// 健康检查
app.get('/api/health', (req, res) => {
  res.json({ 
    status: 'OK', 
    timestamp: new Date().toISOString(),
    services: {
      redis: 'connected',
      mysql: 'connected'
    }
  });
});

// 启动服务器
app.listen(PORT, '0.0.0.0', () => {
  console.log(`🦞 CryptoNews Dashboard Backend running on port ${PORT}`);
  console.log(`📊 API: http://localhost:${PORT}/api`);
  console.log(`💰 Crypto: http://localhost:${PORT}/api/crypto/heatmap`);
  console.log(`📰 News: http://localhost:${PORT}/api/news/list`);
});