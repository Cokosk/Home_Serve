package com.cryptonews.service;

import com.cryptonews.collector.CoinGeckoClient;
import com.cryptonews.entity.CryptoPrice;
import com.cryptonews.repository.CryptoPriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 加密货币服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CryptoService {

    private final CoinGeckoClient coinGeckoClient;
    private final CryptoPriceRepository cryptoPriceRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String CACHE_KEY_HEATMAP = "crypto:heatmap";
    private static final String CACHE_KEY_OVERVIEW = "crypto:overview";
    private static final String CACHE_KEY_DETAIL = "crypto:detail:";
    private static final long CACHE_TTL = 5; // 5分钟缓存

    /**
     * 获取热力图数据
     */
    public Map<String, Object> getHeatmapData(Integer limit) {
        // 先查缓存
        @SuppressWarnings("unchecked")
        Map<String, Object> cached = (Map<String, Object>) redisTemplate.opsForValue().get(CACHE_KEY_HEATMAP);
        if (cached != null) {
            log.debug("从缓存获取热力图数据");
            return cached;
        }

        // 从 CoinGecko 获取
        List<Map<String, Object>> markets = coinGeckoClient.getMarkets(limit);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", Map.of(
            "cryptos", markets,
            "lastUpdate", new Date(),
            "total", markets.size()
        ));

        // 写入缓存
        redisTemplate.opsForValue().set(CACHE_KEY_HEATMAP, result, CACHE_TTL, TimeUnit.MINUTES);

        // 异步保存到数据库
        savePricesToDatabase(markets);

        return result;
    }

    /**
     * 获取单个币种详情
     */
    public Map<String, Object> getCryptoDetail(String symbol) {
        String cacheKey = CACHE_KEY_DETAIL + symbol.toLowerCase();
        
        @SuppressWarnings("unchecked")
        Map<String, Object> cached = (Map<String, Object>) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }

        // CoinGecko 使用 id 而非 symbol，需要映射
        String coinId = mapSymbolToId(symbol);
        Map<String, Object> detail = coinGeckoClient.getCoinDetail(coinId);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", detail);

        redisTemplate.opsForValue().set(cacheKey, result, CACHE_TTL, TimeUnit.MINUTES);

        return result;
    }

    /**
     * 获取历史价格
     */
    public Map<String, Object> getPriceHistory(String symbol, Integer days) {
        String coinId = mapSymbolToId(symbol);
        List<Map<String, Object>> history = coinGeckoClient.getPriceHistory(coinId, days);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", history);

        return result;
    }

    /**
     * 刷新价格数据
     */
    public Map<String, Object> refreshPrices() {
        // 清除缓存
        redisTemplate.delete(CACHE_KEY_HEATMAP);
        redisTemplate.delete(CACHE_KEY_OVERVIEW);
        
        // 获取新数据
        List<Map<String, Object>> markets = coinGeckoClient.getMarkets(100);
        savePricesToDatabase(markets);

        return Map.of(
            "code", 200,
            "message", "刷新成功",
            "data", Map.of("updated", markets.size())
        );
    }

    /**
     * 获取市场概览
     */
    public Map<String, Object> getMarketOverview() {
        @SuppressWarnings("unchecked")
        Map<String, Object> cached = (Map<String, Object>) redisTemplate.opsForValue().get(CACHE_KEY_OVERVIEW);
        if (cached != null) {
            return cached;
        }

        Map<String, Object> globalData = coinGeckoClient.getGlobalData();

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", globalData);

        redisTemplate.opsForValue().set(CACHE_KEY_OVERVIEW, result, CACHE_TTL, TimeUnit.MINUTES);

        return result;
    }

    /**
     * 定时刷新价格数据（每5分钟）
     */
    @Scheduled(fixedRate = 300000)
    public void scheduledRefresh() {
        log.info("定时刷新加密货币数据...");
        try {
            refreshPrices();
        } catch (Exception e) {
            log.error("定时刷新失败: {}", e.getMessage());
        }
    }

    /**
     * 保存价格数据到数据库
     */
    private void savePricesToDatabase(List<Map<String, Object>> markets) {
        for (Map<String, Object> coin : markets) {
            try {
                CryptoPrice price = new CryptoPrice();
                price.setSymbol((String) coin.get("symbol"));
                price.setName((String) coin.get("name"));
                price.setPrice(BigDecimal.valueOf((Double) coin.get("price")));
                price.setChange24h(BigDecimal.valueOf((Double) coin.get("change24h")));
                price.setVolume24h(BigDecimal.valueOf((Long) coin.get("volume24h")));
                price.setMarketCap(BigDecimal.valueOf((Long) coin.get("marketCap")));
                price.setRecordedAt(LocalDateTime.now());
                price.setSource("COINGECKO");

                cryptoPriceRepository.save(price);
            } catch (Exception e) {
                log.warn("保存价格失败 [{}]: {}", coin.get("symbol"), e.getMessage());
            }
        }
    }

    /**
     * Symbol 到 CoinGecko ID 的映射
     */
    private String mapSymbolToId(String symbol) {
        Map<String, String> mapping = Map.of(
            "BTC", "bitcoin",
            "ETH", "ethereum",
            "BNB", "binancecoin",
            "SOL", "solana",
            "XRP", "ripple",
            "ADA", "cardano",
            "DOGE", "dogecoin",
            "AVAX", "avalanche-2",
            "DOT", "polkadot",
            "LINK", "chainlink",
            "MATIC", "matic-network",
            "LTC", "litecoin",
            "UNI", "uniswap",
            "ATOM", "cosmos",
            "ETC", "ethereum-classic",
            "XLM", "stellar",
            "NEAR", "near",
            "APT", "aptos",
            "ARB", "arbitrum",
            "OP", "optimism"
        );
        return mapping.getOrDefault(symbol.toUpperCase(), symbol.toLowerCase());
    }
}