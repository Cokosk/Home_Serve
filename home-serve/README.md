# Home Serve - 家政服务平台

基于 Redis 高并发响应的家政服务平台（毕业设计项目）

## 项目简介

开发一个家政服务预约平台，核心业务包括用户预约、服务者抢单、订单管理、支付模拟、双向评价。重点突出 Redis 在高并发场景下的应用：缓存热点数据、分布式锁控制抢单、接口限流、异步队列处理，确保系统能支撑高并发（如抢单时 100+ QPS）且数据一致。

## 贡献者

| 角色 | 贡献者 | 职责 |
|------|--------|------|
| 核心开发 | **WYH** (@Cokosk) | 核心代码开发、架构设计、业务逻辑实现 |
| 协助开发 | 🦞 麻辣小龙虾 (AI Assistant) | 部署配置、测试用例编写、文档完善 |

### 协作说明

本项目由 **WYH** 主导开发，负责后端核心功能、前端页面等核心代码的实现。

AI 助手（麻辣小龙虾）协助完成以下工作：
- 🔧 环境配置与部署文档编写
- 🧪 单元测试与集成测试用例编写
- 📝 项目文档完善与维护
- 🚀 阿里云部署指导

## 技术栈

### 后端
| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 2.7.18 | 后端框架 |
| MyBatis-Plus | 3.5.5 | ORM 框架 |
| Redis (Lettuce) | - | 缓存 |
| Redisson | 3.25.2 | 分布式锁 |
| MySQL | 8.0 | 数据库 |
| Maven | 3.9+ | 构建工具 |
| JDK | 22 | Java 版本 |

### 前端（管理后台）
| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.4+ | 前端框架 |
| Element Plus | 2.5+ | UI 组件库 |
| Vite | 5.0+ | 构建工具 |
| Axios | 1.6+ | HTTP 客户端 |
| Pinia | 2.1+ | 状态管理 |
| Vue Router | 4.2+ | 路由管理 |

## 项目结构

```
home-serve/
├── backend/                      # 后端项目
│   ├── src/main/
│   │   ├── java/com/cokosk/homeserve/
│   │   │   ├── config/          # 配置类
│   │   │   ├── controller/      # 控制器
│   │   │   ├── service/         # 业务逻辑
│   │   │   ├── mapper/          # 数据访问
│   │   │   ├── entity/          # 实体类
│   │   │   ├── lock/            # 分布式锁
│   │   │   └── utils/           # 工具类
│   │   └── resources/
│   │       └── application.yml  # 配置文件
│   ├── src/test/                # 测试用例
│   └── pom.xml
│
├── front-admin/                  # 前端管理后台
│   ├── src/
│   │   ├── api/                  # API 封装
│   │   ├── router/               # 路由配置
│   │   ├── views/                # 页面组件
│   │   │   ├── Home.vue          # 首页/数据概览
│   │   │   ├── Orders.vue        # 订单管理
│   │   │   ├── Services.vue      # 服务管理
│   │   │   ├── Users.vue         # 用户管理
│   │   │   └── Workers.vue       # 服务者管理
│   │   ├── App.vue               # 根组件
│   │   └── main.js               # 入口文件
│   ├── index.html
│   ├── vite.config.js
│   └── package.json
│
├── docs/                         # 文档
├── sql/                          # 数据库脚本
├── nginx/                        # Nginx 配置
├── docker-compose.yml            # Docker 编排
├── Dockerfile                    # 后端镜像
├── deploy.sh                     # 部署脚本
└── README.md
```

## 核心功能

### 后端 API
- ✅ 用户模块：登录/注册/信息缓存
- ✅ 服务模块：分类列表、服务列表、详情（Redis 缓存）
- ✅ 订单模块：创建订单、抢单（分布式锁 + 限流）
- ✅ 订单查询：用户/服务者订单列表
- ✅ 异步队列：抢单通知、支付处理

### 前端管理后台
- ✅ 首页：数据概览、热门服务、最新订单
- ✅ 订单管理：订单列表、搜索筛选、详情查看、取消订单
- ✅ 服务管理：分类管理、服务项目管理
- ✅ 用户管理：用户列表、状态管理
- ✅ 服务者管理：服务者列表、审核管理

## Redis 应用设计（核心亮点）

### 1. 缓存热点数据
| 缓存 Key | 过期时间 | 说明 |
|---------|----------|------|
| `service:category:list` | 30 分钟 | 分类列表 |
| `service:hot` | 30 分钟 | 热门服务 |
| `service:category:{id}` | 30 分钟 | 分类下服务 |
| `service:detail:{id}` | 1 小时 | 服务详情 |
| `user:info:{id}` | 24 小时 | 用户信息 |

### 2. 分布式锁（抢单核心）
```
key: order:grab:lock:{orderId}
value: UUID 唯一标识
实现: SET NX EX + Lua 脚本原子释放
```

### 3. 接口限流
```
key: rate:limit:{ip}:{api}
算法: 滑动窗口
限制: 每 IP 每分钟 10 次
```

### 4. 异步队列
| 队列名 | 用途 |
|--------|------|
| `queue:order:grabbed` | 抢单成功通知 |
| `queue:payment:success` | 支付成功处理 |

## 快速启动

### 方式一：分别启动（开发模式）

#### 1. 启动后端
```bash
# 初始化数据库
mysql -u root -p < sql/init.sql

# 修改配置（可选，默认已配置好）
vim backend/src/main/resources/application.yml

# 启动后端
cd backend
mvn spring-boot:run
```

#### 2. 启动前端
```bash
cd front-admin
npm install
npm run dev
```

访问 http://localhost:3000

### 方式二：Docker 部署（推荐）

```bash
# 克隆项目
git clone https://github.com/Cokosk/Home_Serve.git
cd Home_Serve

# 一键部署
chmod +x deploy.sh
./deploy.sh
```

访问 http://your-server-ip

## 前端页面预览

| 页面 | 说明 |
|------|------|
| 首页 | 数据统计卡片、热门服务表格、最新订单列表 |
| 订单管理 | 搜索筛选、订单表格、分页、详情弹窗 |
| 服务管理 | 分类 Tab、服务项目 Tab、状态管理 |
| 用户管理 | 用户列表、角色标签、状态开关 |
| 服务者管理 | 服务者列表、评分显示、审核操作 |

## API 接口文档

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
3. 验证订单状态 (Redis 缓存 → DB)
   ↓
4. 更新数据库订单状态
   ↓
5. 同步更新 Redis 缓存
   ↓
6. 从抢单池移除
   ↓
7. 异步通知 (队列)
   ↓
8. 释放分布式锁
```

## 开发进度

- [x] 后端核心功能开发
- [x] 管理后台前端开发
- [x] 配置修复（支持环境变量）
- [x] 阿里云部署文档
- [x] 单元测试用例编写
- [ ] 集成测试与性能测试
- [ ] 用户端前端开发
- [ ] 服务者端前端开发

## 部署说明

### 阿里云部署步骤

```bash
# 1. 上传项目到服务器
scp -r home-serve root@your-server-ip:/opt/

# 2. 登录服务器
ssh root@your-server-ip

# 3. 安装依赖（如未安装）
apt update && apt install -y docker.io docker-compose maven

# 4. 配置 Docker 镜像源（解决拉取失败问题）
cat > /etc/docker/daemon.json << 'EOF'
{
  "registry-mirrors": [
    "https://docker.mirrors.ustc.edu.cn",
    "https://registry.cn-hangzhou.aliyuncs.com"
  ]
}
EOF
systemctl restart docker

# 5. 启动服务
cd /opt/home-serve
docker-compose up -d

# 6. 验证服务
curl http://localhost:8080/health
```

### 防火墙配置

```bash
# 开放必要端口
ufw allow 80      # Nginx
ufw allow 8080    # 后端 API
ufw allow 3306    # MySQL（可选，仅内网访问建议关闭）
ufw allow 6379    # Redis（可选，仅内网访问建议关闭）
```

### 阿里云安全组

在阿里云控制台 - 安全组添加入站规则：
- TCP 80（HTTP）
- TCP 8080（API）
- TCP 3306（MySQL，建议仅内网）
- TCP 6379（Redis，建议仅内网）

### 日志查看

```bash
# 查看服务状态
docker-compose ps

# 查看后端日志
docker-compose logs backend

# 实时日志
docker-compose logs -f backend
```

## 性能目标

| 指标 | 目标 |
|------|------|
| 抢单接口响应时间 | < 500ms (100 并发) |
| 缓存命中率 | > 90% |
| 数据库 QPS 降低 | > 50% |

## 配置说明

### 环境变量配置

项目支持通过环境变量覆盖默认配置，便于 Docker 部署：

| 环境变量 | 默认值 | 说明 |
|---------|--------|------|
| `SERVER_PORT` | 8080 | 服务端口 |
| `SPRING_DATASOURCE_URL` | jdbc:mysql://localhost:3306/home_serve | 数据库连接 |
| `SPRING_DATASOURCE_USERNAME` | root | 数据库用户名 |
| `SPRING_DATASOURCE_PASSWORD` | root | 数据库密码 |
| `SPRING_REDIS_HOST` | localhost | Redis 主机 |
| `SPRING_REDIS_PORT` | 6379 | Redis 端口 |
| `SPRING_REDIS_PASSWORD` | (空) | Redis 密码 |

## 更新日志

### v1.0.2 (2026-03-08)
- ✅ 添加单元测试用例（UserService、OrderService、DistributedLock）
- ✅ 完善 README 贡献者说明

### v1.0.1 (2026-03-06)
- ✅ 修复 application.yml 支持环境变量配置
- ✅ 更新 docker-compose.yml 使用标准环境变量格式
- ✅ 添加阿里云部署步骤和常见问题排查
- ✅ 添加 Docker 镜像源配置说明

### v1.0.0 (2026-03-05)
- ✅ 完成后端核心功能开发
- ✅ 完成 Vue3 + Element Plus 管理后台
- ✅ Redis 缓存、分布式锁、限流功能实现
- ✅ 抢单核心流程实现

## 许可证

MIT