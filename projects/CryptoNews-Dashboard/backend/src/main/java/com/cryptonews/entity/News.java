package com.cryptonews.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 新闻实体
 */
@Data
@Entity
@Table(name = "news", indexes = {
    @Index(name = "idx_published_at", columnList = "publishedAt"),
    @Index(name = "idx_source", columnList = "source"),
    @Index(name = "idx_sentiment", columnList = "sentiment")
})
public class News {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** 标题 */
    @Column(nullable = false, length = 500)
    private String title;
    
    /** 摘要 */
    @Column(columnDefinition = "TEXT")
    private String summary;
    
    /** 原文链接 */
    @Column(length = 1000)
    private String url;
    
    /** 来源 */
    @Column(length = 100)
    private String source;
    
    /** 作者 */
    @Column(length = 100)
    private String author;
    
    /** 发布时间 */
    @Column(name = "published_at")
    private LocalDateTime publishedAt;
    
    /** 采集时间 */
    @Column(name = "collected_at")
    private LocalDateTime collectedAt;
    
    /** 相关币种 (JSON 数组) */
    @Column(columnDefinition = "TEXT")
    private String relatedCoins;
    
    /** 情感分数 (-1 到 1，负面到正面) */
    @Column(precision = 3, scale = 2)
    private Double sentiment;
    
    /** 情感标签 */
    @Column(length = 20)
    private String sentimentLabel; // POSITIVE, NEGATIVE, NEUTRAL
    
    /** AI 生成的摘要 */
    @Column(columnDefinition = "TEXT")
    private String aiSummary;
    
    /** 投资建议 */
    @Column(columnDefinition = "TEXT")
    private String investmentAdvice;
    
    /** 是否已处理 */
    @Column(name = "is_processed")
    private Boolean isProcessed = false;
}