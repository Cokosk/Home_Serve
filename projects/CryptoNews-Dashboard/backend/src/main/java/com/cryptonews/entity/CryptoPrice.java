package com.cryptonews.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 加密货币价格实体
 */
@Data
@Entity
@Table(name = "crypto_price", indexes = {
    @Index(name = "idx_symbol_time", columnList = "symbol,recordedAt")
})
public class CryptoPrice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** 币种符号 (BTC, ETH, SOL 等) */
    @Column(length = 20, nullable = false)
    private String symbol;
    
    /** 币种名称 */
    @Column(length = 100)
    private String name;
    
    /** 当前价格 (USD) */
    @Column(precision = 20, scale = 8)
    private BigDecimal price;
    
    /** 24小时涨跌幅 (%) */
    @Column(name = "change_24h", precision = 10, scale = 4)
    private BigDecimal change24h;
    
    /** 24小时交易量 (USD) */
    @Column(name = "volume_24h", precision = 20, scale = 2)
    private BigDecimal volume24h;
    
    /** 市值 (USD) */
    @Column(precision = 20, scale = 2)
    private BigDecimal marketCap;
    
    /** 记录时间 */
    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;
    
    /** 数据来源 */
    @Column(length = 50)
    private String source; // COINGECKO, BINANCE
}