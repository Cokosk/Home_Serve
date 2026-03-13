package com.cryptonews.service;

import com.cryptonews.entity.News;
import com.cryptonews.collector.NewsCollector;
import com.cryptonews.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 新闻服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;
    private final NewsCollector newsCollector;

    /**
     * 获取新闻列表
     */
    public Map<String, Object> getNewsList(Integer page, Integer size, String sentiment, String coin) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by("publishedAt").descending());
        
        Page<News> newsPage;
        if (sentiment != null && !sentiment.isEmpty()) {
            newsPage = newsRepository.findBySentimentLabel(sentiment, pageRequest);
        } else if (coin != null && !coin.isEmpty()) {
            newsPage = newsRepository.findByRelatedCoinsContaining(coin, pageRequest);
        } else {
            newsPage = newsRepository.findAll(pageRequest);
        }

        List<Map<String, Object>> list = new ArrayList<>();
        for (News news : newsPage.getContent()) {
            list.add(convertToMap(news));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", Map.of(
            "list", list,
            "total", newsPage.getTotalElements(),
            "page", page,
            "size", size,
            "totalPages", newsPage.getTotalPages()
        ));

        return result;
    }

    /**
     * 获取新闻详情
     */
    public Map<String, Object> getNewsDetail(Long id) {
        Optional<News> newsOpt = newsRepository.findById(id);
        
        if (newsOpt.isEmpty()) {
            return Map.of("code", 404, "message", "新闻不存在", "data", null);
        }

        return Map.of(
            "code", 200,
            "message", "success",
            "data", convertToMap(newsOpt.get())
        );
    }

    /**
     * 手动触发新闻采集
     */
    public Map<String, Object> collectNews() {
        int collected = newsCollector.collectAll();
        
        return Map.of(
            "code", 200,
            "message", "采集完成",
            "data", Map.of("collected", collected)
        );
    }

    /**
     * 获取情感趋势
     */
    public Map<String, Object> getSentimentTrend(Integer days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        List<Object[]> trend = newsRepository.getSentimentTrend(since);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : trend) {
            Map<String, Object> point = new HashMap<>();
            point.put("date", row[0]);
            point.put("avgSentiment", row[1]);
            result.add(point);
        }

        return Map.of(
            "code", 200,
            "message", "success",
            "data", result
        );
    }

    /**
     * 获取最新新闻（供其他模块使用）
     */
    public List<News> getLatestNews(int limit) {
        LocalDateTime since = LocalDateTime.now().minusHours(24);
        return newsRepository.findByPublishedAtAfterOrderByPublishedAtDesc(since)
                .stream()
                .limit(limit)
                .toList();
    }

    /**
     * 转换为前端格式
     */
    private Map<String, Object> convertToMap(News news) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", news.getId());
        map.put("title", news.getTitle());
        map.put("summary", news.getSummary());
        map.put("url", news.getUrl());
        map.put("source", news.getSource());
        map.put("author", news.getAuthor());
        map.put("publishedAt", news.getPublishedAt());
        map.put("relatedCoins", parseJsonArray(news.getRelatedCoins()));
        map.put("sentiment", news.getSentiment());
        map.put("sentimentLabel", news.getSentimentLabel());
        map.put("aiSummary", news.getAiSummary());
        map.put("investmentAdvice", news.getInvestmentAdvice());
        return map;
    }

    /**
     * 解析 JSON 数组
     */
    private List<String> parseJsonArray(String json) {
        if (json == null || json.isEmpty() || json.equals("[]")) {
            return Collections.emptyList();
        }
        
        // 简单解析
        return Arrays.asList(json.replace("[", "")
                .replace("]", "")
                .replace("\"", "")
                .split(","));
    }
}