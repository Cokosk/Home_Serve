-- CryptoNews Dashboard 数据库初始化脚本

CREATE DATABASE IF NOT EXISTS crypto_news DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE crypto_news;

-- 加密货币价格表
CREATE TABLE IF NOT EXISTS crypto_price (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    symbol VARCHAR(20) NOT NULL COMMENT '币种符号，如 BTC, ETH',
    name VARCHAR(100) NOT NULL COMMENT '币种名称',
    current_price DECIMAL(20, 8) COMMENT '当前价格(USD)',
    market_cap BIGINT COMMENT '市值',
    market_cap_rank INT COMMENT '市值排名',
    total_volume BIGINT COMMENT '24h交易量',
    price_change_24h DECIMAL(20, 8) COMMENT '24h价格变化',
    price_change_percentage_24h DECIMAL(10, 4) COMMENT '24h价格变化百分比',
    last_updated DATETIME COMMENT '最后更新时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_symbol (symbol),
    INDEX idx_rank (market_cap_rank)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='加密货币价格表';

-- 新闻表
CREATE TABLE IF NOT EXISTS news (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(500) NOT NULL COMMENT '标题',
    content TEXT COMMENT '内容',
    summary TEXT COMMENT '摘要',
    source VARCHAR(100) COMMENT '来源',
    author VARCHAR(100) COMMENT '作者',
    url VARCHAR(1000) COMMENT '原文链接',
    published_at DATETIME COMMENT '发布时间',
    category VARCHAR(50) COMMENT '分类',
    sentiment VARCHAR(20) COMMENT '情感：positive/negative/neutral',
    sentiment_score DECIMAL(5, 4) COMMENT '情感分数',
    related_coins JSON COMMENT '相关币种',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_published (published_at),
    INDEX idx_source (source),
    INDEX idx_sentiment (sentiment)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='新闻表';

-- AI分析记录表
CREATE TABLE IF NOT EXISTS ai_analysis (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(50) NOT NULL COMMENT '分析类型：sentiment/advice/market',
    input_text TEXT COMMENT '输入文本',
    result TEXT COMMENT '分析结果',
    model VARCHAR(100) COMMENT '使用的模型',
    tokens_used INT COMMENT '消耗的token数',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_type (type),
    INDEX idx_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI分析记录表';

-- 市场概览缓存表
CREATE TABLE IF NOT EXISTS market_overview (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    total_market_cap BIGINT COMMENT '总市值',
    total_volume BIGINT COMMENT '总交易量',
    btc_dominance DECIMAL(10, 4) COMMENT 'BTC市值占比',
    eth_dominance DECIMAL(10, 4) COMMENT 'ETH市值占比',
    market_cap_change_percentage_24h DECIMAL(10, 4) COMMENT '24h市值变化百分比',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='市场概览缓存表';

-- 插入示例数据
INSERT INTO crypto_price (symbol, name, current_price, market_cap, market_cap_rank, price_change_percentage_24h) VALUES
('BTC', 'Bitcoin', 67000.00, 1300000000000, 1, 2.5),
('ETH', 'Ethereum', 3500.00, 420000000000, 2, 3.2),
('BNB', 'BNB', 600.00, 90000000000, 3, 1.8);

-- 插入示例新闻
INSERT INTO news (title, content, source, category, sentiment, published_at) VALUES
('比特币突破67000美元，市场情绪高涨', '比特币今日突破67000美元关口，创下近期新高。分析师认为这可能与机构资金持续流入有关...', 'CoinDesk', 'market', 'positive', NOW()),
('以太坊2.0升级进展顺利', '以太坊基金会宣布2.0升级测试网运行稳定，预计将在下个季度完成最终升级...', 'CryptoNews', 'technology', 'positive', NOW() - INTERVAL 1 DAY),
('SEC加强加密货币监管', '美国证券交易委员会宣布将加强对加密货币交易所的监管力度，市场短期出现波动...', 'Reuters', 'regulation', 'neutral', NOW() - INTERVAL 2 DAY);