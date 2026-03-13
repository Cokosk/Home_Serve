package com.cryptonews.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 调度任务配置
 */
@Configuration
@EnableScheduling
public class ScheduleConfig {
    // 定时任务已在各 Service 中通过 @Scheduled 注解实现
    // - CryptoService.scheduledRefresh(): 每5分钟刷新价格
    // - NewsCollector.scheduledCollect(): 每5分钟采集新闻
    // - AIAnalysisService.processUnanalyzedNews(): 每10分钟处理未分析新闻
}