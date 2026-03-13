#!/bin/bash
# OpenClaw Deploy 自动部署脚本

set -e

PROJECT_DIR="/opt/openclaw-deploy"
SERVER_IP="101.200.180.182"

echo "🦞 OpenClaw Deploy 自动部署"
echo "============================"

# 检查 Docker
if ! command -v docker &> /dev/null; then
    echo "❌ Docker 未安装"
    exit 1
fi

if ! command -v docker compose &> /dev/null; then
    echo "❌ Docker Compose 未安装"
    exit 1
fi

echo "✅ Docker 环境检查通过"

# 创建项目目录
echo "📁 创建项目目录..."
mkdir -p $PROJECT_DIR
cd $PROJECT_DIR

# 停止旧容器
echo "🛑 停止旧容器..."
docker compose down 2>/dev/null || true

# 构建并启动
echo "🔨 构建并启动服务..."
docker compose up -d --build

# 等待服务启动
echo "⏳ 等待服务启动..."
sleep 15

# 检查服务状态
echo "🔍 检查服务状态..."
docker compose ps

echo ""
echo "✅ 部署完成！"
echo ""
echo "📍 访问地址："
echo "   前端: http://$SERVER_IP:10080"
echo "   后端: http://$SERVER_IP:18080"
echo ""
echo "📊 查看日志："
echo "   docker compose logs -f"
echo ""