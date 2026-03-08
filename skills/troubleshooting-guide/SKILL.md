# Troubleshooting Guide Skill

开发过程中常见问题的解决方案。

---

## 问题 1：OpenClaw 网关 Token 不匹配

### 症状
```
error: token_mismatch
```

### 原因
网关配置了 `auth.token` 但 CLI 缺少 `remote.token` 配置。

### 解决方案
在 `~/.openclaw/openclaw.json` 中添加：

```json
{
  "gateway": {
    "auth": {
      "token": "your_token"
    },
    "remote": {
      "token": "your_token"
    }
  }
}
```

然后重启网关：
```bash
openclaw gateway restart
```

---

## 问题 2：Docker 镜像拉取失败（国内网络）

### 症状
```
Error: pull access denied
timeout
```

### 解决方案
配置国内镜像源：

```bash
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

---

## 问题 3：Redisson 与 Spring Boot 2.7 不兼容

### 症状
```
NoClassDefFoundError: org/springframework/data/redis/connection/...
```

### 原因
Redisson 3.25.2 默认使用 spring-data-32，但 Spring Boot 2.7 需要 spring-data-27。

### 解决方案
修改 `pom.xml`：

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

---

## 问题 4：前端页面 404

### 症状
访问网页返回 404，Nginx 日志显示找不到 index.html。

### 原因
Nginx 配置缺少前端静态文件路由。

### 解决方案

1. 更新 `nginx/nginx.conf`：

```nginx
server {
    listen 80;
    server_name localhost;
    
    root /usr/share/nginx/html;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api/ {
        proxy_pass http://backend;
        proxy_set_header Host $host;
    }
}
```

2. 复制前端文件到容器：

```bash
# 构建前端
cd front-admin && npm run build

# 复制到 Nginx 容器
docker cp dist/. homeserve-nginx:/usr/share/nginx/html/
docker restart homeserve-nginx
```

---

## 问题 5：数据库中文乱码

### 症状
API 返回中文显示为 `ä¿æ´æœåŠ¡` 等乱码。

### 原因
MySQL 客户端使用 latin1 字符集，数据以错误编码存储。

### 解决方案

1. 重建数据库：

```sql
DROP DATABASE home_serve;
CREATE DATABASE home_serve 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;
```

2. 使用正确字符集导入数据：

```bash
mysql --default-character-set=utf8mb4 -u root -p home_serve < sql/init.sql
```

3. 确保 docker-compose.yml 配置正确：

```yaml
mysql:
  command:
    - --character-set-server=utf8mb4
    - --collation-server=utf8mb4_unicode_ci
```

---

## 问题 6：GitHub 推送认证失败

### 症状
```
fatal: could not read Username
```

### 解决方案
使用 Token 认证：

```bash
# 设置远程 URL（包含 token）
git remote set-url origin https://<token>@github.com/user/repo.git

# 推送
git push origin main
```

**注意：** Token 不要提交到仓库，使用 .gitignore 排除敏感文件。

---

## 问题 7：敏感信息泄露

### 症状
GitHub Push Protection 拒绝推送：
```
remote: error: GH013: Repository rule violations found
remote: - Push cannot contain secrets
```

### 解决方案

1. 将敏感信息移到 `.gitignore` 排除的文件中
2. 使用 git reset 回退提交：

```bash
git reset --soft HEAD~1
```

3. 创建本地专属 Skill 存储敏感信息

---

## 问题 8：服务器代码不同步

### 症状
服务器和本地/GitHub 代码不一致。

### 解决方案

```bash
# 强制同步到远程
git fetch origin
git reset --hard origin/main

# 或手动同步关键文件
scp -i ~/.ssh/key local-file root@server:/path/
```

---

## 预防措施

### 1. 敏感信息管理
- 使用 `.gitignore` 排除敏感文件
- 使用本地 Skill 存储密码、Token
- 不要将敏感信息提交到 GitHub

### 2. 字符集配置
- 数据库使用 `utf8mb4`
- 连接 URL 添加 `characterEncoding=utf8`
- MySQL 容器配置 `--character-set-server=utf8mb4`

### 3. Docker 网络
- 使用国内镜像源
- 配置 `daemon.json`

### 4. 依赖兼容性
- 检查 Spring Boot 版本对应的依赖
- 使用 exclusion 排除冲突依赖