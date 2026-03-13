package com.cryptonews.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

/**
 * LLM AI 客户端
 * 支持 OpenAI 和通义千问
 */
@Slf4j
@Component
public class LLMClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${ai.provider:openai}")
    private String provider;

    @Value("${ai.openai.api-key:}")
    private String openaiApiKey;

    @Value("${ai.openai.model:gpt-4o-mini}")
    private String openaiModel;

    @Value("${ai.openai.base-url:https://api.openai.com/v1}")
    private String openaiBaseUrl;

    @Value("${ai.qwen.api-key:}")
    private String qwenApiKey;

    @Value("${ai.qwen.model:qwen-turbo}")
    private String qwenModel;

    private static final String QWEN_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";

    public LLMClient() {
        this.webClient = WebClient.create();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 调用 LLM 完成文本
     */
    public String chat(String systemPrompt, String userPrompt) {
        if ("qwen".equals(provider)) {
            return chatWithQwen(systemPrompt, userPrompt);
        } else {
            return chatWithOpenAI(systemPrompt, userPrompt);
        }
    }

    /**
     * OpenAI 调用
     */
    private String chatWithOpenAI(String systemPrompt, String userPrompt) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("model", openaiModel);
            body.put("messages", Arrays.asList(
                    Map.of("role", "system", "content", systemPrompt),
                    Map.of("role", "user", "content", userPrompt)
            ));
            body.put("temperature", 0.7);
            body.put("max_tokens", 2000);

            String response = webClient.post()
                    .uri(openaiBaseUrl + "/chat/completions")
                    .header("Authorization", "Bearer " + openaiApiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return parseOpenAIResponse(response);
        } catch (Exception e) {
            log.error("OpenAI 调用失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 通义千问调用
     */
    private String chatWithQwen(String systemPrompt, String userPrompt) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("model", qwenModel);

            Map<String, Object> input = new HashMap<>();
            input.put("messages", Arrays.asList(
                    Map.of("role", "system", "content", systemPrompt),
                    Map.of("role", "user", "content", userPrompt)
            ));
            body.put("input", input);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("temperature", 0.7);
            parameters.put("max_tokens", 2000);
            body.put("parameters", parameters);

            String response = webClient.post()
                    .uri(QWEN_URL)
                    .header("Authorization", "Bearer " + qwenApiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return parseQwenResponse(response);
        } catch (Exception e) {
            log.error("通义千问调用失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 分析新闻情感
     */
    public SentimentResult analyzeSentiment(String text) {
        String systemPrompt = """
                你是一个专业的金融新闻分析师。请分析给定新闻的情感倾向，返回JSON格式：
                {
                  "sentiment": 数值(-1到1，-1最负面，0中性，1最正面),
                  "label": "POSITIVE" 或 "NEGATIVE" 或 "NEUTRAL",
                  "confidence": 置信度(0到1),
                  "reason": "简短理由(50字以内)"
                }
                只返回JSON，不要其他内容。
                """;

        String response = chat(systemPrompt, text);
        if (response == null) {
            return new SentimentResult(0.0, "NEUTRAL", 0.5, "分析失败");
        }

        try {
            // 尝试提取 JSON
            String json = extractJson(response);
            JsonNode node = objectMapper.readTree(json);

            return new SentimentResult(
                    node.path("sentiment").asDouble(0.0),
                    node.path("label").asText("NEUTRAL"),
                    node.path("confidence").asDouble(0.5),
                    node.path("reason").asText("")
            );
        } catch (Exception e) {
            log.warn("解析情感分析结果失败: {}", e.getMessage());
            return new SentimentResult(0.0, "NEUTRAL", 0.5, "解析失败");
        }
    }

    /**
     * 生成投资建议
     */
    public InvestmentAdvice generateAdvice(String symbol, String marketData, String newsSummary) {
        String systemPrompt = """
                你是一个专业的加密货币分析师。基于市场数据和新闻，生成投资建议。
                
                重要规则：
                1. 必须包含风险提示
                2. 不要给出具体买卖点位
                3. 建议应该综合技术面和基本面
                4. 保持客观中立
                
                返回JSON格式：
                {
                  "analysis": "综合分析(200字以内)",
                  "sentimentScore": 数值(0-100),
                  "sentimentLabel": "非常乐观"到"非常悲观",
                  "suggestion": "建议操作(持有/观望/减仓等)",
                  "riskWarning": "风险提示",
                  "keyFactors": ["关键因素1", "关键因素2", ...]
                }
                只返回JSON，不要其他内容。
                """;

        String userPrompt = String.format("""
                币种: %s
                
                市场数据:
                %s
                
                相关新闻摘要:
                %s
                """, symbol, marketData, newsSummary);

        String response = chat(systemPrompt, userPrompt);
        if (response == null) {
            return getDefaultAdvice(symbol);
        }

        try {
            String json = extractJson(response);
            JsonNode node = objectMapper.readTree(json);

            InvestmentAdvice advice = new InvestmentAdvice();
            advice.setSymbol(symbol);
            advice.setAnalysis(node.path("analysis").asText());
            advice.setSentimentScore(node.path("sentimentScore").asInt(50));
            advice.setSentimentLabel(node.path("sentimentLabel").asText());
            advice.setSuggestion(node.path("suggestion").asText());
            advice.setRiskWarning(node.path("riskWarning").asText("加密货币投资存在高风险，请谨慎决策。"));

            List<String> factors = new ArrayList<>();
            node.path("keyFactors").forEach(f -> factors.add(f.asText()));
            advice.setKeyFactors(factors);

            return advice;
        } catch (Exception e) {
            log.warn("解析投资建议失败: {}", e.getMessage());
            return getDefaultAdvice(symbol);
        }
    }

    /**
     * 生成每日摘要
     */
    public String generateDailyDigest(List<String> newsTitles, String marketOverview) {
        String systemPrompt = """
                你是一个加密货币市场分析师。基于今日新闻和市场数据，生成一份简洁的市场摘要。
                
                要求：
                1. 控制在300字以内
                2. 突出重要事件和趋势
                3. 包含市场情绪判断
                4. 语言专业但易懂
                """;

        String userPrompt = String.format("""
                今日新闻标题:
                %s
                
                市场概况:
                %s
                """, String.join("\n", newsTitles), marketOverview);

        return chat(systemPrompt, userPrompt);
    }

    // ========== 辅助方法 ==========

    private String parseOpenAIResponse(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            log.error("解析 OpenAI 响应失败: {}", e.getMessage());
            return null;
        }
    }

    private String parseQwenResponse(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            return root.path("output").path("text").asText();
        } catch (Exception e) {
            log.error("解析通义千问响应失败: {}", e.getMessage());
            return null;
        }
    }

    private String extractJson(String text) {
        int start = text.indexOf("{");
        int end = text.lastIndexOf("}") + 1;
        if (start >= 0 && end > start) {
            return text.substring(start, end);
        }
        return text;
    }

    private InvestmentAdvice getDefaultAdvice(String symbol) {
        InvestmentAdvice advice = new InvestmentAdvice();
        advice.setSymbol(symbol);
        advice.setAnalysis("暂无足够数据进行分析");
        advice.setSentimentScore(50);
        advice.setSentimentLabel("中性");
        advice.setSuggestion("观望");
        advice.setRiskWarning("加密货币投资存在高风险，本建议仅供参考，不构成投资建议。");
        advice.setKeyFactors(List.of("数据不足"));
        return advice;
    }

    // ========== 数据类 ==========

    public record SentimentResult(
            double sentiment,
            String label,
            double confidence,
            String reason
    ) {}

    public static class InvestmentAdvice {
        private String symbol;
        private String analysis;
        private int sentimentScore;
        private String sentimentLabel;
        private String suggestion;
        private String riskWarning;
        private List<String> keyFactors;

        // Getters and Setters
        public String getSymbol() { return symbol; }
        public void setSymbol(String symbol) { this.symbol = symbol; }
        public String getAnalysis() { return analysis; }
        public void setAnalysis(String analysis) { this.analysis = analysis; }
        public int getSentimentScore() { return sentimentScore; }
        public void setSentimentScore(int sentimentScore) { this.sentimentScore = sentimentScore; }
        public String getSentimentLabel() { return sentimentLabel; }
        public void setSentimentLabel(String sentimentLabel) { this.sentimentLabel = sentimentLabel; }
        public String getSuggestion() { return suggestion; }
        public void setSuggestion(String suggestion) { this.suggestion = suggestion; }
        public String getRiskWarning() { return riskWarning; }
        public void setRiskWarning(String riskWarning) { this.riskWarning = riskWarning; }
        public List<String> getKeyFactors() { return keyFactors; }
        public void setKeyFactors(List<String> keyFactors) { this.keyFactors = keyFactors; }
    }
}