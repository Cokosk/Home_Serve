# Home Serve - 家政服务平台

> 基于 Redis 高并发响应的家政服务平台（毕业设计项目）

## 项目概述

家政服务预约平台，核心功能包括用户预约、服务者抢单、订单管理。重点突出 Redis 在高并发场景下的应用：缓存热点数据、分布式锁控制抢单、接口限流。

**在线演示：** http://101.200.180.182

**技术栈：** Spring Boot 2.7 + Redis + MySQL 8.0 + Vue3 + Element Plus

## 贡献者

| 角色 | 贡献者 | 职责 |
|:----:|:------:|------|
| 核心开发 | **WYH** ([@Cokosk](https://github.com/Cokosk)) | 核心代码开发、架构设计、业务逻辑实现 |
| 协助开发 | 🦞 麻辣小龙虾 (AI Assistant) | 部署配置、测试用例编写、文档完善 |

### 协作说明

本项目由 **WYH** 主导开发，负责后端核心功能、前端页面等核心代码的实现。

AI 助手（麻辣小龙虾）协助完成：
- 🔧 环境配置与部署文档
- 🧪 单元测试与集成测试
- 📝 项目文档维护
- 🚀 阿里云部署指导

## 核心功能

| 模块 | 功能 | 亮点 |
|------|------|------|
| 用户模块 | 登录/注册 | Redis 缓存用户信息 |
| 服务模块 | 分类/列表/详情 | 热点数据缓存 |
| 订单模块 | 创建/抢单/管理 | 分布式锁 + 限流 |
| 管理后台 | 数据概览/订单管理 | Vue3 + Element Plus |

## Redis 应用（核心亮点）

```
┌─────────────────────────────────────────────────────────┐
│  缓存热点数据     │  分布式锁抢单    │  接口限流        │
│  service:xxx     │  order:grab:lock │  rate:limit:xxx  │
│  过期: 30min-24h │  SET NX EX       │  滑动窗口算法    │
└─────────────────────────────────────────────────────────┘
```

## 项目结构

```
home-serve/
├── backend/           # 后端 (Spring Boot)
│   ├── src/main/      # 核心代码
│   └── src/test/      # 测试用例
├── front-admin/       # 管理后台 (Vue3)
├── sql/               # 数据库脚本
├── nginx/             # Nginx 配置
├── docker-compose.yml # Docker 编排
└── README.md
```

## 快速开始

```bash
# 克隆项目
git clone https://github.com/Cokosk/Home_Serve.git
cd Home_Serve

# Docker 一键部署
chmod +x deploy.sh
./deploy.sh
```

## 开发进度

- [x] 后端核心功能
- [x] 管理后台前端
- [x] 单元测试用例
- [ ] 用户端前端
- [ ] 服务者端前端

## 相关链接

- 📦 **GitHub：** https://github.com/Cokosk/Home_Serve
- 🌐 **在线演示：** http://101.200.180.182
- 📧 **联系方式：** 见 GitHub Profile

## 许可证

MIT License

---

<p align="center">
  Made with ❤️ by WYH | Assisted by 🦞 麻辣小龙虾
</p>