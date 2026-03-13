# 数据库初始化
CREATE DATABASE IF NOT EXISTS openclaw_deploy DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE openclaw_deploy;

CREATE TABLE IF NOT EXISTS deploy_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    plan VARCHAR(50) NOT NULL,
    notes TEXT,
    skills TEXT,
    status VARCHAR(50) DEFAULT 'pending',
    access_url VARCHAR(255),
    container_id VARCHAR(255),
    created_at DATETIME,
    completed_at DATETIME,
    INDEX idx_email (email),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;