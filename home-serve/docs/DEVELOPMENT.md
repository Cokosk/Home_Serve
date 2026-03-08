# 开发文档

## 环境要求

| 软件 | 版本 | 说明 |
|------|------|------|
| JDK | 21+ | Java 运行环境 |
| Maven | 3.9+ | 构建工具 |
| Node.js | 18+ | 前端构建 |
| MySQL | 8.0+ | 数据库 |
| Redis | 7.0+ | 缓存 |
| Docker | 24+ | 容器化部署 |

---

## 本地开发

### 1. 克隆项目

```bash
git clone https://github.com/Cokosk/Qin_Jia-HomeService.git
cd Qin_Jia-HomeService/home-serve
```

### 2. 初始化数据库

```bash
mysql -u root -p < sql/init.sql
```

### 3. 启动后端

```bash
# 方式一：Maven
mvn spring-boot:run

# 方式二：打包运行
mvn clean package -DskipTests
java -jar target/home-serve-1.0.0.jar
```

### 4. 启动前端

```bash
cd front-admin
npm install
npm run dev
```

访问 http://localhost:3000

---

## Docker 部署

### 一键启动

```bash
docker compose up -d
```

### 服务说明

| 容器 | 端口 | 说明 |
|------|------|------|
| homeserve-mysql | 3306 | MySQL 数据库 |
| homeserve-redis | 6379 | Redis 缓存 |
| homeserve-backend | 8080 | 后端 API |
| homeserve-nginx | 80 | Nginx 反向代理 |

### 常用命令

```bash
# 查看服务状态
docker compose ps

# 查看日志
docker compose logs -f backend

# 重启服务
docker compose restart

# 停止服务
docker compose down
```

---

## 阿里云部署

### 1. 环境准备

```bash
# 安装 Docker
apt update && apt install -y docker.io docker-compose-v2

# 配置镜像源（国内）
mkdir -p /etc/docker
cat > /etc/docker/daemon.json << 'EOF'
{
  "registry-mirrors": [
    "https://docker.mirrors.ustc.edu.cn",
    "https://registry.cn-hangzhou.aliyuncs.com"
  ]
}
EOF
systemctl restart docker
```

### 2. 部署项目

```bash
# 克隆项目
git clone https://github.com/Cokosk/Qin_Jia-HomeService.git /opt/home-serve
cd /opt/home-serve/home-serve

# 构建并启动
mvn clean package -DskipTests
docker compose up -d --build
```

### 3. 防火墙配置

```bash
# 开放端口
ufw allow 80
ufw allow 8080
```

### 4. 阿里云安全组

在阿里云控制台添加入站规则：
- TCP 80 (HTTP)
- TCP 8080 (API)

---

## 项目结构

```
home-serve/
├── backend/                 # 后端源码
│   └── src/main/java/com/cokosk/homeserve/
│       ├── config/          # 配置类
│       ├── controller/      # 控制器
│       ├── service/         # 业务逻辑
│       ├── mapper/          # MyBatis Mapper
│       ├── entity/          # 实体类
│       └── lock/            # 分布式锁
├── front-admin/             # 管理后台前端
│   └── src/
│       ├── api/             # API 封装
│       ├── router/          # 路由
│       └── views/           # 页面组件
├── sql/                     # 数据库脚本
├── nginx/                   # Nginx 配置
├── docker-compose.yml       # Docker 编排
├── Dockerfile               # 后端镜像
└── pom.xml                  # Maven 配置
```

---

## API 接口

### 用户模块

```
POST /api/user/login      # 登录
POST /api/user/register   # 注册
GET  /api/user/info       # 用户信息
```

### 服务模块

```
GET /api/service/category  # 分类列表（缓存）
GET /api/service/hot       # 热门服务（缓存）
GET /api/service/list      # 服务列表
GET /api/service/detail    # 服务详情
```

### 订单模块

```
POST /api/order/create     # 创建订单
POST /api/order/grab       # 抢单
GET  /api/order/grab-pool  # 抢单池
GET  /api/order/list       # 订单列表
POST /api/order/cancel     # 取消订单
```

---

## Redis 设计

### 缓存 Key 设计

| Key | 类型 | 过期时间 | 说明 |
|-----|------|----------|------|
| `service:category:list` | String | 30min | 分类列表 |
| `service:hot` | String | 30min | 热门服务 |
| `service:detail:{id}` | String | 1h | 服务详情 |
| `user:info:{id}` | String | 24h | 用户信息 |
| `order:grab:pool` | ZSet | - | 抢单池 |

### 分布式锁

```
Key: order:grab:lock:{orderId}
实现: SET NX EX
超时: 30秒
```

### 限流

```
Key: rate:limit:{ip}:{api}
算法: 滑动窗口
限制: 10次/分钟
```

---

## 常见问题

### 1. Docker 镜像拉取失败

**原因：** 国内网络问题

**解决：** 配置国内镜像源

```bash
cat > /etc/docker/daemon.json << 'EOF'
{
  "registry-mirrors": [
    "https://docker.mirrors.ustc.edu.cn",
    "https://registry.cn-hangzhou.aliyuncs.com"
  ]
}
EOF
systemctl restart docker
```

### 2. 中文乱码

**原因：** MySQL 字符集配置问题

**解决：** 重建数据库

```sql
DROP DATABASE home_serve;
CREATE DATABASE home_serve CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

然后重新导入数据。

### 3. Redisson 版本不兼容

**原因：** Redisson 3.25.2 默认使用 spring-data-32

**解决：** 在 pom.xml 添加兼容依赖

```xml
<dependency>
    <groupId>org.redisson</groupId>
    <artifactId>redisson-spring-boot-starter</artifactId>
    <version>${redisson.version}</version>
    <exclusions>
        <exclusion>
            <groupId>org.redisson</groupId>
            <artifactId>redisson-spring-data-32</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.redisson</groupId>
    <artifactId>redisson-spring-data-27</artifactId>
    <version>${redisson.version}</version>
</dependency>
```

### 4. 前端 404

**原因：** Nginx 未配置前端静态文件

**解决：** 更新 nginx.conf

```nginx
server {
    listen 80;
    root /usr/share/nginx/html;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }
}
```

然后复制前端文件到容器：

```bash
docker cp front-admin/dist/. homeserve-nginx:/usr/share/nginx/html/
```

---

## 开发规范

### Git 提交规范

```
feat: 新功能
fix: 修复 bug
docs: 文档更新
style: 代码格式
refactor: 重构
test: 测试
chore: 构建/工具
```

### 分支管理

- `main` - 主分支，稳定版本
- `develop` - 开发分支
- `feature/*` - 功能分支

---

## 更新日志

### v1.0.0 (2026-03-09)

- 完成后端核心功能
- 完成管理后台前端
- Redis 缓存、分布式锁、限流
- 阿里云部署成功
- 单元测试用例