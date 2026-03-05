# Home Serve - 家政服务平台

基于Redis高并发响应的家政服务平台（毕业设计项目）

## 项目简介

开发一个家政服务预约平台，核心业务包括用户预约、服务者抢单、订单管理、支付模拟、双向评价。重点突出Redis在高并发场景下的应用：缓存热点数据、分布式锁控制抢单、接口限流、异步队列处理，确保系统能支撑高并发（如抢单时100+ QPS）且数据一致。

## 技术栈

### 后端
| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 2.7.18 | 后端框架 |
| MyBatis-Plus | 3.5.5 | ORM框架 |
| Redis (Lettuce) | - | 缓存 |
| Redisson | 3.25.2 | 分布式锁 |
| MySQL | 8.0 | 数据库 |
| Maven | 3.9+ | 构建工具 |
| JDK | 22 | Java版本 |

### 前端（待开发）
- 用户端：微信小程序（原生）
- 服务者端：微信小程序
- 管理后台：Vue 3 + Element Plus

## 核心功能

### 用户端
- ✅ 首页服务分类、热门服务列表（Redis缓存）
- ✅ 服务详情页（Redis缓存）
- ✅ 预约下单
- ✅ 订单列表/详情
- ✅ 评价订单

### 服务者端
- ✅ 可抢订单列表（从Redis抢单池获取）
- ✅ 抢单（Redis分布式锁 + 限流）
- ✅ 我的日程
- ✅ 收入查看

### 管理后台
- 用户管理
- 服务者审核
- 订单管理
- 服务项目管理

## 项目结构

```
home-serve/
├── backend/                      # 后端项目
│   ├── src/main/
│   │   ├── java/com/cokosk/homeserve/
│   │   │   ├── config/          # 配置类
│   │   │   │   ├── RedisConfig.java           # Redis配置
│   │   │   │   └── MyBatisPlusConfig.java     # MyBatis配置
│   │   │   ├── controller/    # 控制器
│   │   │   │   ├── UserController.java        # 用户接口
│   │   │   │   ├── ServiceController.java     # 服务接口
│   │   │   │   ├── OrderController.java       # 抢单接口
│   │   │   │   ├── OrderQueryController.java  # 订单查询
│   │   │   │   └── HealthController.java      # 健康检查
│   │   │   ├── service/       # 业务逻辑
│   │   │   │   ├── UserService.java           # 用户服务
│   │   │   │   ├── ServiceCategoryService.java # 分类服务
│   │   │   │   ├── ServiceItemService.java    # 服务项目
│   │   │   │   └── OrderService.java          # 订单服务(抢单核心)
│   │   │   ├── mapper/        # 数据访问
│   │   │   ├── entity/        # 实体类
│   │   │   ├── lock/          # 分布式锁
│   │   │   │   ├── DistributedLock.java       # 分布式锁工具
│   │   │   │   └── RateLimiter.java           # 接口限流
│   │   │   ├── utils/         # 工具类
│   │   │   │   └── AsyncQueueConsumer.java    # 异步队列消费
│   │   │   └── HomeServeApplication.java      # 启动类
│   │   └── resources/
│   │       └── application.yml               # 配置文件
│   └── pom.xml
├── docs/                        # 文档
├── sql/                         # 数据库脚本
│   └── init.sql                 # 初始化脚本
├── nginx/                       # Nginx配置
│   └── nginx.conf
├── docker-compose.yml           # Docker编排
├── Dockerfile                   # 后端镜像
├── deploy.sh                    # 部署脚本
├── .gitignore
└── README.md
```

## Redis应用设计（核心亮点）

### 1. 缓存热点数据
| 缓存Key | 过期时间 | 说明 |
|---------|----------|------|
| `service:category:list` | 30分钟 | 分类列表 |
| `service:hot` | 30分钟 | 热门服务 |
| `service:category:{id}` | 30分钟 | 分类下服务 |
| `service:detail:{id}` | 1小时 | 服务详情 |
| `user:info:{id}` | 24小时 | 用户信息 |

### 2. 分布式锁（抢单核心）
```
key: order:grab:lock:{orderId}
value: UUID唯一标识
实现: SET NX EX + Lua脚本原子释放
```

### 3. 接口限流
```
key: rate:limit:{ip}:{api}
算法: 滑动窗口
限制: 每IP每分钟10次
```

### 4. 异步队列
| 队列名 | 用途 |
|--------|------|
| `queue:order:grabbed` | 抢单成功通知 |
| `queue:payment:success` | 支付成功处理 |

### 5. 抢单池
使用Sorted Set存储待抢订单，按创建时间排序

## API接口文档

### 用户模块
| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/user/login` | POST | 用户登录 |
| `/api/user/register` | POST | 用户注册 |
| `/api/user/info` | GET | 获取用户信息 |

### 服务模块
| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/service/category` | GET | 获取分类列表（缓存） |
| `/api/service/hot` | GET | 热门服务（缓存） |
| `/api/service/list` | GET | 服务列表（缓存） |
| `/api/service/detail` | GET | 服务详情（缓存） |

### 订单模块
| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/order/create` | POST | 创建订单 |
| `/api/order/grab` | POST | 抢单 ⭐ |
| `/api/order/grab-pool` | GET | 抢单池 |
| `/api/order/list` | GET | 订单列表 |
| `/api/order/detail` | GET | 订单详情 |
| `/api/order/cancel` | POST | 取消订单 |
| `/api/order/start` | POST | 开始服务 |
| `/api/order/finish` | POST | 完成服务 |
| `/api/order/worker-list` | GET | 服务者订单 |

### 健康检查
| 接口 | 方法 | 说明 |
|------|------|------|
| `/health` | GET | 健康检查 |

## 抢单核心流程

```
1. 限流检查
   ↓
2. 获取分布式锁 (SET NX EX)
   ↓
3. 验证订单状态 (Redis缓存 → DB)
   ↓
4. 更新数据库订单状态
   ↓
5. 同步更新Redis缓存
   ↓
6. 从抢单池移除
   ↓
7. 异步通知 (队列)
   ↓
8. 释放分布式锁
```

## 快速启动

### 1. 环境要求
- JDK 22
- Maven 3.9+
- MySQL 8.0+
- Redis 5.0+

### 2. 初始化数据库
```bash
mysql -u root -p < sql/init.sql
```

### 3. 配置修改
修改 `backend/src/main/resources/application.yml` 中的数据库和Redis配置

### 4. 本地运行
```bash
cd backend
mvn spring-boot:run
```

### 5. Docker部署
```bash
# 方式一：一键部署
chmod +x deploy.sh
./deploy.sh

# 方式二：Docker Compose
docker-compose up -d
```

### 6. 访问服务
- 后端API: http://localhost:8080
- Nginx: http://localhost

## Docker部署架构

```
┌─────────────────────────────────────────┐
│              Nginx (80/443)             │
└────────────────┬────────────────────────┘
                 │
    ┌────────────┼────────────┐
    ▼            ▼            ▼
┌───────┐   ┌───────┐   ┌───────┐
│  Vue  │   │ 小程序  │   │ 小程序  │
│  后台  │   │ 用户端  │   │服务者端 │
└───┬───┘   └───┬───┘   └───┬───┘
    │           │           │
    └───────────┼───────────┘
                ▼
      ┌─────────────────┐
      │  Spring Boot    │
      │    (Java 22)    │
      └────────┬────────┘
               │
    ┌──────────┼──────────┐
    ▼          ▼          ▼
 ┌─────┐   ┌─────┐   ┌─────┐
 │MySQL│   │Redis│   │  ... │
 │ 8.0 │   │ 7.x │   └─────┘
 └─────┘   └─────┘
```

## 性能目标

| 指标 | 目标 |
|------|------|
| 抢单接口响应时间 | < 500ms (100并发) |
| 缓存命中率 | > 90% |
| 数据库QPS降低 | > 50% |

## 开发阶段

- [x] 第一阶段：基础框架搭建
- [x] 第二阶段：Redis缓存、分布式锁、限流
- [ ] 第三阶段：前端界面完善
- [ ] 第四阶段：压力测试、优化

## 后续规划

1. 微信小程序前端开发
2. 管理后台Vue开发
3. 真实支付集成
4. 消息推送集成
5. 压力测试与优化

## 许可证

MIT