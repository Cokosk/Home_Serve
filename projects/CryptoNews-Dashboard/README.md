# 加密货币新闻监控仪表盘

> Global News Monitor with Crypto Heatmap & AI Analysis

## 项目概述

加密货币新闻监控仪表盘是一个集新闻聚合、实时行情、热力图可视化、AI 分析于一体的综合性平台。

**核心功能：**
- 📊 加密货币涨跌热力图
- 📰 多源新闻聚合 & 情感分析
- 🤖 AI 投资建议生成
- 📈 市场情绪趋势追踪

**技术栈：** Spring Boot 3.x + Redis + MySQL + Vue3 + ECharts + LLM

## 项目结构

```
CryptoNews-Dashboard/
├── backend/                    # 后端服务
│   ├── src/main/java/
│   │   └── com/cryptonews/
│   │       ├── controller/    # API 控制器
│   │       ├── service/       # 业务逻辑
│   │       ├── entity/        # 实体类
│   │       ├── collector/     # 新闻采集器
│   │       ├── ai/            # AI 分析模块
│   │       └── config/        # 配置类
│   └── pom.xml
├── frontend/                   # 前端应用
│   ├── src/
│   │   ├── views/             # 页面组件
│   │   ├── api/               # API 封装
│   │   └── router/            # 路由配置
│   └── package.json
├── sql/                        # 数据库脚本
├── docs/                       # 文档
└── docker-compose.yml          # Docker 编排
```

## 快速开始

### 环境要求

- JDK 21+
- Node.js 18+
- MySQL 8.0+
- Redis 7.0+
- Docker & Docker Compose

### 本地开发

```bash
# 1. 克隆项目
git clone https://github.com/Cokosk/CryptoNews-Dashboard.git
cd CryptoNews-Dashboard

# 2. 初始化数据库
mysql -u root -p < sql/init.sql

# 3. 启动后端
cd backend
mvn spring-boot:run

# 4. 启动前端
cd ../frontend
npm install
npm run dev
```

### Docker 部署

```bash
docker compose up -d
```

## API 接口

### 加密货币模块

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/crypto/heatmap` | GET | 获取热力图数据 |
| `/api/crypto/{symbol}` | GET | 获取币种详情 |
| `/api/crypto/{symbol}/history` | GET | 获取历史价格 |
| `/api/crypto/market/overview` | GET | 市场概览 |

### 新闻模块

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/news/list` | GET | 获取新闻列表 |
| `/api/news/{id}` | GET | 获取新闻详情 |
| `/api/news/collect` | POST | 触发新闻采集 |

### AI 分析模块

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/ai/analyze/sentiment` | POST | 情感分析 |
| `/api/ai/advice` | POST | 生成投资建议 |
| `/api/ai/analysis/market` | GET | 市场综合分析 |
| `/api/ai/digest/today` | GET | 今日摘要 |

## 配置说明

### 环境变量

| 变量 | 说明 | 默认值 |
|------|------|--------|
| `DB_PASSWORD` | MySQL 密码 | root |
| `REDIS_PASSWORD` | Redis 密码 | 空 |
| `COINGECKO_API_KEY` | CoinGecko API Key | 空 |
| `OPENAI_API_KEY` | OpenAI API Key | 空 |
| `AI_PROVIDER` | AI 提供商 | openai |

## 开发进度

- [x] 项目骨架搭建
- [ ] 新闻采集模块
- [ ] 加密货币数据接入
- [ ] 热力图可视化
- [ ] AI 情感分析
- [ ] 投资建议生成
- [ ] 用户系统
- [ ] 推送通知

## 贡献者

| 角色 | 贡献者 |
|:----:|:------:|
| 核心开发 | **WYH** ([@Cokosk](https://github.com/Cokosk)) |
| 协助开发 | 🦞 麻辣小龙虾 (AI Assistant) |

## 许可证

MIT License

---

**⚠️ 风险提示：** 本平台提供的所有信息和建议仅供参考，不构成投资建议。加密货币投资存在高风险，请谨慎决策。