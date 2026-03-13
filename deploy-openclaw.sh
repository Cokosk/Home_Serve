#!/bin/bash
# OpenClaw 快速部署脚本 - 只安装指定的 skills

set -e

echo "🦞 OpenClaw 快速部署脚本"
echo "========================"

# 检查 Node.js
if ! command -v node &> /dev/null; then
    echo "❌ 请先安装 Node.js 18+"
    exit 1
fi

# 安装 OpenClaw
echo "📦 安装 OpenClaw..."
npm install -g openclaw

# 安装 ClawHub CLI
echo "📦 安装 ClawHub CLI..."
npm install -g clawhub

# 创建 workspace
mkdir -p ~/.openclaw/workspace/skills
cd ~/.openclaw/workspace

# 初始化基础文件
echo "📝 初始化 workspace..."
cat > AGENTS.md << 'EOF'
# AGENTS.md - Your Workspace

This folder is home. Treat it that way.

## Every Session

Before doing anything else:

1. Read `SOUL.md` — this is who you are
2. Read `USER.md` — this is who you're helping
3. Read `memory/YYYY-MM-DD.md` (today + yesterday) for recent context
EOF

cat > SOUL.md << 'EOF'
# SOUL.md - Who You Are

Be genuinely helpful, not performatively helpful.
Have opinions. Be resourceful before asking.
EOF

cat > USER.md << 'EOF'
# USER.md - About Your Human

Fill this in as you go.
EOF

cat > HEARTBEAT.md << 'EOF'
# HEARTBEAT.md
# Keep this file empty to skip heartbeat API calls.
EOF

mkdir -p memory

# 安装指定的 skills
echo ""
echo "📚 安装 Skills..."
echo "----------------"

# Skills 列表（按需修改）
SKILLS=(
    "weather"
    "morning-routine"
    "daily-review-ritual"
    "tmux"
    "clawhub"
)

for skill in "${SKILLS[@]}"; do
    echo "  ⬇️  安装 $skill..."
    clawhub install "$skill" --no-input 2>/dev/null || echo "  ⚠️  $skill 安装失败或不存在"
done

echo ""
echo "✅ 部署完成！"
echo ""
echo "🚀 启动方式："
echo "   openclaw gateway start    # 启动服务"
echo "   openclaw agent            # 运行一次对话"
echo ""
echo "📚 管理 Skills："
echo "   clawhub search <关键词>   # 搜索 skill"
echo "   clawhub install <名称>    # 安装 skill"
echo "   clawhub list              # 列出已安装"
EOF