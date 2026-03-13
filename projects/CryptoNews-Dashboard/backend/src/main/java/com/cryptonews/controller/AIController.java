package com.cryptonews.controller;

import com.cryptonews.service.AIAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * AI 分析接口
 */
@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AIController {
    
    @Autowired
    private AIAnalysisService aiService;
    
    /**
     * 分析新闻情感
     */
    @PostMapping("/analyze/sentiment")
    public Map<String, Object> analyzeSentiment(@RequestBody Map<String, Object> request) {
        String text = (String) request.get("text");
        return aiService.analyzeSentiment(text);
    }
    
    /**
     * 生成投资建议
     */
    @PostMapping("/advice")
    public Map<String, Object> generateAdvice(@RequestBody Map<String, Object> request) {
        String symbol = (String) request.get("symbol");
        return aiService.generateInvestmentAdvice(symbol);
    }
    
    /**
     * 综合市场分析
     */
    @GetMapping("/analysis/market")
    public Map<String, Object> getMarketAnalysis() {
        return aiService.getMarketAnalysis();
    }
    
    /**
     * 获取今日摘要
     */
    @GetMapping("/digest/today")
    public Map<String, Object> getTodayDigest() {
        return aiService.getTodayDigest();
    }
}