package com.cryptonews.controller;

import com.cryptonews.service.CryptoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * 加密货币接口
 */
@RestController
@RequestMapping("/api/crypto")
@CrossOrigin(origins = "*")
public class CryptoController {
    
    @Autowired
    private CryptoService cryptoService;
    
    /**
     * 获取热力图数据
     */
    @GetMapping("/heatmap")
    public Map<String, Object> getHeatmapData(
            @RequestParam(defaultValue = "50") Integer limit) {
        return cryptoService.getHeatmapData(limit);
    }
    
    /**
     * 获取单个币种详情
     */
    @GetMapping("/{symbol}")
    public Map<String, Object> getCryptoDetail(@PathVariable String symbol) {
        return cryptoService.getCryptoDetail(symbol);
    }
    
    /**
     * 获取历史价格
     */
    @GetMapping("/{symbol}/history")
    public Map<String, Object> getPriceHistory(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "7") Integer days) {
        return cryptoService.getPriceHistory(symbol, days);
    }
    
    /**
     * 手动刷新价格数据
     */
    @PostMapping("/refresh")
    public Map<String, Object> refreshPrices() {
        return cryptoService.refreshPrices();
    }
    
    /**
     * 获取市场概览
     */
    @GetMapping("/market/overview")
    public Map<String, Object> getMarketOverview() {
        return cryptoService.getMarketOverview();
    }
}