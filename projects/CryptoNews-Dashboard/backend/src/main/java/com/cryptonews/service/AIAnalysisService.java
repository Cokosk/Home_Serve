package com.cryptonews.service;

import com.cryptonews.ai.LLMClient;
import com.cryptonews.collector.CoinGeckoClient;
import com.cryptonews.entity.News;
import com.cryptonews.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * AI 分析服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AIAnalysisService {

    private final LLMClient llmClient;
    private final CoinGeckoClient coinGeckoClient;
    private final NewsRepository newsRepository;
    private final NewsService newsService;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String CACHE_KEY_MARKET_ANALYSIS = "ai:market:analysis";
    private static final String CACHE_KEY_TODAY_DIGEST = "ai:digest:today";

    /**
     * 分析新闻情感
     */
    public Map<String, Object> analyzeSentiment(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Map.of(
                "code", 400,
                "message", "文本不能为空",
                "data", null
            );
        }

        LLMClient.SentimentResult result = llmClient.analyzeSentiment(text);

        return Map.of(
            "code", 200,
            "message", "success",
            "data", Map.of(
                "sentiment", result.sentiment(),
                "label", result.label(),
                "confidence", result.confidence(),
                "reason", result.reason()
            )
        );
    }

    /**
     * 生成投资建议
     */
    public Map<String, Object> generateInvestmentAdvice(String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) {
            return Map.of(
                "code", 400,
                "message", "币种不能为空",
                "data", null
            );
        }

        try {
            // 获取市场数据
            String marketData = getMarketDataForSymbol(symbol);
            
            // 获取相关新闻
            String newsSummary = getNewsSummaryForSymbol(symbol);

            // 调用 AI 生成建议
            LLMClient.InvestmentAdvice advice = llmClient.generateAdvice(symbol, marketData, newsSummary);

            return Map.of(
                "code", 200,
                "message", "success",
                "data", Map.of(
                    "symbol", advice.getSymbol(),
                    "analysis", advice.getAnalysis(),
                    "sentimentScore", advice.getSentimentScore(),
                    "sentimentLabel", advice.getSentimentLabel(),
                    "suggestion", advice.getSuggestion(),
                    "riskWarning", advice.getRiskWarning(),
                    "keyFactors", advice.getKeyFactors(),
                    "disclaimer", "本建议仅供参考，不构成投资建议。加密货币投资存在高风险，请谨慎决策。"
                )
            );
        } catch (Exception e) {
            log.error("生成投资建议失败 [{}]: {}", symbol, e.getMessage());
            return Map.of(
                "code", 500,
                "message", "生成失败: " + e.getMessage(),
                "data", null
            );
        }
    }

    /**
     * 综合市场分析
     */
    public Map<String, Object> getMarketAnalysis() {
        // 检查缓存
        @SuppressWarnings("unchecked")
        Map<String, Object> cached = (Map<String, Object>) redisTemplate.opsForValue().get(CACHE_KEY_MARKET_ANALYSIS);
        if (cached != null) {
            return cached;
        }

        try {
            // 获取全球市场数据
            Map<String, Object> globalData = coinGeckoClient.getGlobalData();
            
            // 获取最新新闻
            List<News> latestNews = newsService.getLatestNews(10);
            
            // 计算市场情绪
            double avgSentiment = calculateAverageSentiment(latestNews);
            String sentimentLabel = getSentimentLabel(avgSentiment);

            // 获取热门币种
            List<Map<String, Object>> hotCryptos = coinGeckoClient.getMarkets(5);

            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", Map.of(
                "overallSentiment", sentimentLabel,
                "sentimentScore", (int) ((avgSentiment + 1) * 50), // 转换为 0-100
                "globalMarketCap", globalData.get("totalMarketCap"),
                "btcDominance", globalData.get("btcDominance"),
                "hotCoins", hotCryptos.stream().map(c -> Map.of(
                    "symbol", c.get("symbol"),
                    "name", c.get("name"),
                    "change24h", c.get("change24h")
                )).collect(Collectors.toList()),
                "keyNews", latestNews.stream().limit(5).map(n -> Map.of(
                    "title", n.getTitle(),
                    "sentiment", n.getSentimentLabel()
                )).collect(Collectors.toList()),
                "lastUpdate", new Date()
            ));

            // 缓存 10 分钟
            redisTemplate.opsForValue().set(CACHE_KEY_MARKET_ANALYSIS, result, 10, TimeUnit.MINUTES);

            return result;
        } catch (Exception e) {
            log.error("获取市场分析失败: {}", e.getMessage());
            return Map.of("code", 500, "message", "分析失败", "data", null);
        }
    }

    /**
     * 获取今日摘要
     */
    public Map<String, Object> getTodayDigest() {
        @SuppressWarnings("unchecked")
        Map<String, Object> cached = (Map<String, Object>) redisTemplate.opsForValue().get(CACHE_KEY_TODAY_DIGEST);
        if (cached != null) {
            return cached;
        }

        try {
            // 获取今日新闻
            List<News> todayNews = newsRepository.findByPublishedAtAfterOrderByPublishedAtDesc(
                    LocalDateTime.now().minusHours(24)
            );

            List<String> titles = todayNews.stream()
                    .limit(20)
                    .map(News::getTitle)
                    .collect(Collectors.toList());

            // 获取市场概况
            Map<String, Object> globalData = coinGeckoClient.getGlobalData();
            String marketOverview = String.format(
                    "总市值: $%s, BTC主导率: %.1f%%, 24h交易量: $%s",
                    formatNumber((Long) globalData.get("totalMarketCap")),
                    globalData.get("btcDominance"),
                    formatNumber((Long) globalData.get("totalVolume"))
            );

            // 生成摘要
            String digest = llmClient.generateDailyDigest(titles, marketOverview);

            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", Map.of(
                "date", new Date(),
                "summary", digest != null ? digest : "暂无摘要",
                "totalNews", todayNews.size(),
                "marketOverview", marketOverview,
                "topNews", todayNews.stream().limit(5).map(n -> Map.of(
                    "title", n.getTitle(),
                    "source", n.getSource()
                )).collect(Collectors.toList())
            ));

            // 缓存 1 小时
            redisTemplate.opsForValue().set(CACHE_KEY_TODAY_DIGEST, result, 1, TimeUnit.HOURS);

            return result;
        } catch (Exception e) {
            log.error("获取今日摘要失败: {}", e.getMessage());
            return Map.of("code", 500, "message", "生成失败", "data", null);
        }
    }

    /**
     * 批量处理未分析的新闻
     */
    @Scheduled(fixedRate = 600000) // 每10分钟
    public void processUnanalyzedNews() {
        log.info("开始处理未分析的新闻...");
        
        List<News> unprocessed = newsRepository.findByPublishedAtAfterOrderByPublishedAtDesc(
                LocalDateTime.now().minusHours(24)
        ).stream()
                .filter(n -> !Boolean.TRUE.equals(n.getIsProcessed()))
                .limit(50)
                .toList();

        for (News news : unprocessed) {
            try {
                // 分析情感
                String text = news.getTitle() + " " + news.getSummary();
                LLMClient.SentimentResult sentiment = llmClient.analyzeSentiment(text);

                news.setSentiment(sentiment.sentiment());
                news.setSentimentLabel(sentiment.label());
                news.setIsProcessed(true);

                newsRepository.save(news);
            } catch (Exception e) {
                log.warn("处理新闻失败 [{}]: {}", news.getId(), e.getMessage());
            }
        }

        log.info("处理完成，共处理 {} 条新闻", unprocessed.size());
    }

    // ========== 辅助方法 ==========

    private String getMarketDataForSymbol(String symbol) {
        String coinId = mapSymbolToId(symbol);
        Map<String, Object> detail = coinGeckoClient.getCoinDetail(coinId);

        if (detail.isEmpty()) {
            return "暂无市场数据";
        }

        return String.format("""
                当前价格: $%.2f
                24小时涨跌: %.2f%%
                7天涨跌: %.2f%%
                市值: $%s
                24小时交易量: $%s
                历史最高价: $%.2f
                距离最高价: %.2f%%
                """,
                detail.get("currentPrice"),
                detail.get("change24h"),
                detail.get("change7d"),
                formatNumber((Long) detail.get("marketCap")),
                formatNumber((Long) detail.get("volume24h")),
                detail.get("ath"),
                detail.get("athChangePercentage")
        );
    }

    private String getNewsSummaryForSymbol(String symbol) {
        List<News> relatedNews = newsRepository.findByRelatedCoinsContainingOrderByPublishedAtDesc(
                symbol, 
                org.springframework.data.domain.PageRequest.of(0, 10)
        ).getContent();

        if (relatedNews.isEmpty()) {
            return "暂无相关新闻";
        }

        return relatedNews.stream()
                .map(n -> "- " + n.getTitle())
                .collect(Collectors.joining("\n"));
    }

    private double calculateAverageSentiment(List<News> news) {
        return news.stream()
                .filter(n -> n.getSentiment() != null)
                .mapToDouble(News::getSentiment)
                .average()
                .orElse(0.0);
    }

    private String getSentimentLabel(double sentiment) {
        if (sentiment >= 0.3) return "非常乐观";
        if (sentiment >= 0.1) return "乐观";
        if (sentiment >= -0.1) return "中性";
        if (sentiment >= -0.3) return "悲观";
        return "非常悲观";
    }

    private String formatNumber(long num) {
        if (num >= 1_000_000_000_000L) {
            return String.format("%.2fT", num / 1_000_000_000_000.0);
        } else if (num >= 1_000_000_000L) {
            return String.format("%.2fB", num / 1_000_000_000.0);
        } else if (num >= 1_000_000L) {
            return String.format("%.2fM", num / 1_000_000.0);
        } else {
            return String.format("%,d", num);
        }
    }

    private String mapSymbolToId(String symbol) {
        Map<String, String> mapping = Map.of(
            "BTC", "bitcoin", "ETH", "ethereum", "BNB", "binancecoin",
            "SOL", "solana", "XRP", "ripple", "ADA", "cardano",
            "DOGE", "dogecoin", "AVAX", "avalanche-2", "DOT", "polkadot",
            "LINK", "chainlink", "MATIC", "matic-network", "LTC", "litecoin"
        );
        return mapping.getOrDefault(symbol.toUpperCase(), symbol.toLowerCase());
    }
}