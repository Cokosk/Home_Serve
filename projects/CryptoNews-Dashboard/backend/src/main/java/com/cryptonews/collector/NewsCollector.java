package com.cryptonews.collector;

import com.cryptonews.entity.News;
import com.cryptonews.repository.NewsRepository;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * RSS 新闻采集器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NewsCollector {

    private final NewsRepository newsRepository;

    @Value("${news.refresh-interval:300000}")
    private long refreshInterval;

    // 新闻源配置
    private static final List<RssSource> RSS_SOURCES = List.of(
        new RssSource("CoinDesk", "https://www.coindesk.com/arc/outboundfeeds/rss/"),
        new RssSource("CoinTelegraph", "https://cointelegraph.com/rss"),
        new RssSource("CryptoNews", "https://cryptonews.com/news/feed/"),
        new RssSource("Decrypt", "https://decrypt.co/feed"),
        new RssSource("The Block", "https://www.theblock.co/rss.xml"),
        new RssSource("Bitcoinist", "https://bitcoinist.com/feed/"),
        new RssSource("NewsBTC", "https://www.newsbtc.com/feed/")
    );

    // 加密货币关键词（用于识别相关新闻）
    private static final Set<String> CRYPTO_KEYWORDS = Set.of(
        "bitcoin", "btc", "ethereum", "eth", "crypto", "cryptocurrency",
        "blockchain", "defi", "nft", "web3", "altcoin", "stablecoin",
        "solana", "cardano", "ripple", "xrp", "dogecoin", "doge", "bnb",
        "polygon", "matic", "polkadot", "dot", "avalanche", "avax",
        "chainlink", "link", "uniswap", "uni", "sec", "etf", "halving",
        "mining", "wallet", "exchange", "binance", "coinbase", "kraken"
    );

    // 币种符号映射
    private static final Map<String, String> COIN_SYMBOLS = Map.ofEntries(
        Map.entry("bitcoin", "BTC"),
        Map.entry("btc", "BTC"),
        Map.entry("ethereum", "ETH"),
        Map.entry("eth", "ETH"),
        Map.entry("solana", "SOL"),
        Map.entry("cardano", "ADA"),
        Map.entry("ripple", "XRP"),
        Map.entry("xrp", "XRP"),
        Map.entry("dogecoin", "DOGE"),
        Map.entry("doge", "DOGE"),
        Map.entry("binance", "BNB"),
        Map.entry("polygon", "MATIC"),
        Map.entry("polkadot", "DOT"),
        Map.entry("avalanche", "AVAX"),
        Map.entry("chainlink", "LINK"),
        Map.entry("uniswap", "UNI"),
        Map.entry("litecoin", "LTC")
    );

    /**
     * 采集所有新闻源
     */
    public int collectAll() {
        int totalCollected = 0;
        
        for (RssSource source : RSS_SOURCES) {
            try {
                int collected = collectFromSource(source);
                totalCollected += collected;
                log.info("从 {} 采集了 {} 条新闻", source.name, collected);
            } catch (Exception e) {
                log.error("采集 {} 失败: {}", source.name, e.getMessage());
            }
        }

        log.info("本次共采集 {} 条新闻", totalCollected);
        return totalCollected;
    }

    /**
     * 从单个源采集新闻
     */
    private int collectFromSource(RssSource source) {
        try {
            SyndFeed feed = new SyndFeedInput().build(new XmlReader(new URL(source.url)));
            int count = 0;

            for (SyndEntry entry : feed.getEntries()) {
                String url = entry.getLink();
                
                // 去重检查
                if (newsRepository.existsByUrl(url)) {
                    continue;
                }

                News news = parseEntry(entry, source.name);
                if (news != null && isCryptoRelated(news)) {
                    // 识别相关币种
                    Set<String> relatedCoins = extractRelatedCoins(news.getTitle() + " " + news.getSummary());
                    news.setRelatedCoins(toJsonArray(relatedCoins));
                    
                    newsRepository.save(news);
                    count++;
                }
            }

            return count;
        } catch (Exception e) {
            log.error("解析 RSS 源失败 [{}]: {}", source.name, e.getMessage());
            return 0;
        }
    }

    /**
     * 解析 RSS 条目
     */
    private News parseEntry(SyndEntry entry, String sourceName) {
        try {
            News news = new News();
            news.setTitle(entry.getTitle());
            news.setUrl(entry.getLink());
            news.setSource(sourceName);
            news.setSummary(entry.getDescription() != null ? entry.getDescription().getValue() : "");
            news.setAuthor(entry.getAuthor());
            news.setCollectedAt(LocalDateTime.now());
            news.setIsProcessed(false);

            if (entry.getPublishedDate() != null) {
                news.setPublishedAt(entry.getPublishedDate().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime());
            } else {
                news.setPublishedAt(LocalDateTime.now());
            }

            // 清理 HTML 标签
            news.setSummary(stripHtml(news.getSummary()));
            
            return news;
        } catch (Exception e) {
            log.warn("解析新闻条目失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 判断是否与加密货币相关
     */
    private boolean isCryptoRelated(News news) {
        String text = (news.getTitle() + " " + news.getSummary()).toLowerCase();
        return CRYPTO_KEYWORDS.stream().anyMatch(text::contains);
    }

    /**
     * 提取相关币种
     */
    private Set<String> extractRelatedCoins(String text) {
        Set<String> coins = new HashSet<>();
        String lowerText = text.toLowerCase();

        for (Map.Entry<String, String> entry : COIN_SYMBOLS.entrySet()) {
            if (lowerText.contains(entry.getKey())) {
                coins.add(entry.getValue());
            }
        }

        return coins;
    }

    /**
     * 转换为 JSON 数组字符串
     */
    private String toJsonArray(Set<String> set) {
        if (set.isEmpty()) return "[]";
        
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (String s : set) {
            if (!first) sb.append(",");
            sb.append("\"").append(s).append("\"");
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * 移除 HTML 标签
     */
    private String stripHtml(String html) {
        if (html == null) return "";
        return html.replaceAll("<[^>]*>", "")
                .replaceAll("&nbsp;", " ")
                .replaceAll("&amp;", "&")
                .replaceAll("&lt;", "<")
                .replaceAll("&gt;", ">")
                .replaceAll("&quot;", "\"")
                .trim();
    }

    /**
     * 定时采集任务（每5分钟）
     */
    @Scheduled(fixedRate = 300000)
    public void scheduledCollect() {
        log.info("开始定时采集新闻...");
        collectAll();
    }

    /**
     * RSS 源配置
     */
    private record RssSource(String name, String url) {}
}