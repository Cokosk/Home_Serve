package com.cryptonews.collector;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.*;

/**
 * CoinGecko API 客户端
 * 文档: https://www.coingecko.com/en/api/documentation
 */
@Slf4j
@Component
public class CoinGeckoClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${crypto.coingecko.api-key:}")
    private String apiKey;

    public CoinGeckoClient(@Value("${crypto.coingecko.base-url:https://api.coingecko.com/api/v3}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 获取热门币种市场数据（用于热力图）
     * @param limit 返回数量
     * @return 币种列表
     */
    public List<Map<String, Object>> getMarkets(int limit) {
        try {
            String response = webClient.get()
                    .uri(uriBuilder -> {
                        uriBuilder.path("/coins/markets")
                                .queryParam("vs_currency", "usd")
                                .queryParam("order", "market_cap_desc")
                                .queryParam("per_page", limit)
                                .queryParam("page", 1)
                                .queryParam("sparkline", "false")
                                .queryParam("price_change_percentage", "24h,7d");
                        if (apiKey != null && !apiKey.isEmpty()) {
                            uriBuilder.queryParam("x_cg_demo_api_key", apiKey);
                        }
                        return uriBuilder.build();
                    })
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return parseMarketsResponse(response);
        } catch (Exception e) {
            log.error("获取市场数据失败: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * 获取单个币种详情
     */
    public Map<String, Object> getCoinDetail(String coinId) {
        try {
            String response = webClient.get()
                    .uri(uriBuilder -> {
                        uriBuilder.path("/coins/" + coinId)
                                .queryParam("localization", "false")
                                .queryParam("tickers", "false")
                                .queryParam("market_data", "true")
                                .queryParam("community_data", "false")
                                .queryParam("developer_data", "false");
                        if (apiKey != null && !apiKey.isEmpty()) {
                            uriBuilder.queryParam("x_cg_demo_api_key", apiKey);
                        }
                        return uriBuilder.build();
                    })
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return parseCoinDetailResponse(response);
        } catch (Exception e) {
            log.error("获取币种详情失败 [{}]: {}", coinId, e.getMessage());
            return Collections.emptyMap();
        }
    }

    /**
     * 获取历史价格数据
     */
    public List<Map<String, Object>> getPriceHistory(String coinId, int days) {
        try {
            String response = webClient.get()
                    .uri(uriBuilder -> {
                        uriBuilder.path("/coins/" + coinId + "/market_chart")
                                .queryParam("vs_currency", "usd")
                                .queryParam("days", days);
                        if (apiKey != null && !apiKey.isEmpty()) {
                            uriBuilder.queryParam("x_cg_demo_api_key", apiKey);
                        }
                        return uriBuilder.build();
                    })
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return parsePriceHistoryResponse(response);
        } catch (Exception e) {
            log.error("获取历史价格失败 [{}]: {}", coinId, e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * 获取全球市场数据
     */
    public Map<String, Object> getGlobalData() {
        try {
            String response = webClient.get()
                    .uri(uriBuilder -> {
                        uriBuilder.path("/global");
                        if (apiKey != null && !apiKey.isEmpty()) {
                            uriBuilder.queryParam("x_cg_demo_api_key", apiKey);
                        }
                        return uriBuilder.build();
                    })
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return parseGlobalResponse(response);
        } catch (Exception e) {
            log.error("获取全球数据失败: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }

    // ========== 响应解析方法 ==========

    private List<Map<String, Object>> parseMarketsResponse(String response) {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(response);
            for (JsonNode coin : root) {
                Map<String, Object> coinData = new HashMap<>();
                coinData.put("id", coin.path("id").asText());
                coinData.put("symbol", coin.path("symbol").asText().toUpperCase());
                coinData.put("name", coin.path("name").asText());
                coinData.put("image", coin.path("image").asText());
                coinData.put("price", coin.path("current_price").asDouble(0));
                coinData.put("marketCap", coin.path("market_cap").asLong(0));
                coinData.put("marketCapRank", coin.path("market_cap_rank").asInt(0));
                coinData.put("volume24h", coin.path("total_volume").asLong(0));
                coinData.put("change24h", coin.path("price_change_percentage_24h").asDouble(0));
                coinData.put("change7d", coin.path("price_change_percentage_7d_in_currency").asDouble(0));
                coinData.put("circulatingSupply", coin.path("circulating_supply").asDouble(0));
                coinData.put("totalSupply", coin.path("total_supply").asDouble(0));
                coinData.put("ath", coin.path("ath").asDouble(0));
                coinData.put("athChangePercentage", coin.path("ath_change_percentage").asDouble(0));
                result.add(coinData);
            }
        } catch (Exception e) {
            log.error("解析市场数据失败: {}", e.getMessage());
        }
        return result;
    }

    private Map<String, Object> parseCoinDetailResponse(String response) {
        Map<String, Object> result = new HashMap<>();
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode marketData = root.path("market_data");

            result.put("id", root.path("id").asText());
            result.put("symbol", root.path("symbol").asText().toUpperCase());
            result.put("name", root.path("name").asText());
            result.put("description", root.path("description").path("en").asText());
            result.put("image", root.path("image").path("large").asText());

            // 价格数据
            result.put("currentPrice", marketData.path("current_price").path("usd").asDouble(0));
            result.put("ath", marketData.path("ath").path("usd").asDouble(0));
            result.put("athDate", marketData.path("ath_date").path("usd").asText());
            result.put("atl", marketData.path("atl").path("usd").asDouble(0));
            result.put("atlDate", marketData.path("atl_date").path("usd").asText());
            result.put("marketCap", marketData.path("market_cap").path("usd").asLong(0));
            result.put("volume24h", marketData.path("total_volume").path("usd").asLong(0));
            result.put("change24h", marketData.path("price_change_percentage_24h").asDouble(0));
            result.put("change7d", marketData.path("price_change_percentage_7d").asDouble(0));
            result.put("change30d", marketData.path("price_change_percentage_30d").asDouble(0));
            result.put("circulatingSupply", marketData.path("circulating_supply").asDouble(0));
            result.put("totalSupply", marketData.path("total_supply").asDouble(0));
            result.put("maxSupply", marketData.path("max_supply").asDouble(0));
        } catch (Exception e) {
            log.error("解析币种详情失败: {}", e.getMessage());
        }
        return result;
    }

    private List<Map<String, Object>> parsePriceHistoryResponse(String response) {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode prices = root.path("prices");
            for (JsonNode price : prices) {
                Map<String, Object> point = new HashMap<>();
                point.put("timestamp", price.get(0).asLong());
                point.put("price", price.get(1).asDouble());
                result.add(point);
            }
        } catch (Exception e) {
            log.error("解析历史价格失败: {}", e.getMessage());
        }
        return result;
    }

    private Map<String, Object> parseGlobalResponse(String response) {
        Map<String, Object> result = new HashMap<>();
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode data = root.path("data");

            result.put("totalMarketCap", data.path("total_market_cap").path("usd").asLong(0));
            result.put("totalVolume", data.path("total_volume").path("usd").asLong(0));
            result.put("btcDominance", data.path("market_cap_percentage").path("btc").asDouble(0));
            result.put("ethDominance", data.path("market_cap_percentage").path("eth").asDouble(0));
            result.put("activeCryptos", data.path("active_cryptocurrencies").asInt(0));
            result.put("activeExchanges", data.path("active_exchanges").asInt(0));
            result.put("updatedAt", data.path("updated_at").asLong(0));
        } catch (Exception e) {
            log.error("解析全球数据失败: {}", e.getMessage());
        }
        return result;
    }
}