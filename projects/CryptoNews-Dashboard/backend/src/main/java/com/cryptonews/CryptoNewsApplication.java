package com.cryptonews;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 加密货币新闻监控仪表盘 - 主启动类
 * 
 * @author WYH
 * @version 1.0.0
 */
@SpringBootApplication
@EnableScheduling
public class CryptoNewsApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(CryptoNewsApplication.class, args);
    }
}