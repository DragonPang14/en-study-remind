package com.electrocardiogram.esr.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class WeChatService {

    @Value("${wechat.corp-id}")
    private String corpId;

    @Value("${wechat.corp-secret}")
    private String corpSecret;

    @Value("${wechat.agent-id}")
    private Integer agentId;

    private final WebClient webClient;
    private String cachedToken;
    private long tokenExpireTime;

    public WeChatService() {
        this.webClient = WebClient.create();
    }

    // 获取 AccessToken (带缓存)
    private String getAccessToken() {
        if (System.currentTimeMillis() < tokenExpireTime && cachedToken != null) {
            return cachedToken;
        }

        String url = String.format("https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=%s&corpsecret=%s", corpId, corpSecret);

        try {
            String response = webClient.get().uri(url).retrieve().bodyToMono(String.class).block(Duration.ofSeconds(5));
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(response);

            if (node.has("access_token")) {
                this.cachedToken = node.get("access_token").asText();
                this.tokenExpireTime = System.currentTimeMillis() + (7000 * 1000); // 提前200秒过期
                return cachedToken;
            }
        } catch (Exception e) {
            log.error("获取 AccessToken 失败: {}", e.getMessage());
        }
        return null;
    }

    // 发送文本消息
    public void sendMessage(String content) {
        String token = getAccessToken();
        if (token == null) return;

        String url = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=" + token;

        Map<String, Object> body = new HashMap<>();
        body.put("touser", "@all");
        body.put("msgtype", "text");
        body.put("agentid", agentId);

        Map<String, String> text = new HashMap<>();
        text.put("content", content);
        body.put("text", text);

        try {
            webClient.post().uri(url).bodyValue(body).retrieve().bodyToMono(String.class).block(Duration.ofSeconds(5));
        } catch (Exception e) {
            log.error("发送消息失败: {}", e.getMessage());
        }
    }
}