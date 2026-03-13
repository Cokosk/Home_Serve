package com.cryptonews.controller;

import com.cryptonews.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * 新闻接口
 */
@RestController
@RequestMapping("/api/news")
@CrossOrigin(origins = "*")
public class NewsController {
    
    @Autowired
    private NewsService newsService;
    
    /**
     * 获取新闻列表
     */
    @GetMapping("/list")
    public Map<String, Object> getNewsList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String sentiment,
            @RequestParam(required = false) String coin) {
        return newsService.getNewsList(page, size, sentiment, coin);
    }
    
    /**
     * 获取新闻详情
     */
    @GetMapping("/{id}")
    public Map<String, Object> getNewsDetail(@PathVariable Long id) {
        return newsService.getNewsDetail(id);
    }
    
    /**
     * 手动触发新闻采集
     */
    @PostMapping("/collect")
    public Map<String, Object> collectNews() {
        return newsService.collectNews();
    }
    
    /**
     * 获取情感趋势
     */
    @GetMapping("/sentiment/trend")
    public Map<String, Object> getSentimentTrend(@RequestParam(defaultValue = "7") Integer days) {
        return newsService.getSentimentTrend(days);
    }
}