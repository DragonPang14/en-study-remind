package com.electrocardiogram.esr.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class WeChatBotService {

    @Value("${weChat.groupKey}")
    private String groupKey;
    // 替换为你自己的机器人Webhook地址
    private static final String WEBHOOK_URL = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=";
    private final RestTemplate restTemplate = new RestTemplate();

    public void sendMessage(String content) {
        Map<String, Object> body = new HashMap<>();
        body.put("msgtype", "text");

        Map<String, String> text = new HashMap<>();
        text.put("content", content);
        body.put("text", text);

        try {
            String response = restTemplate.postForObject(WEBHOOK_URL + groupKey, body, String.class);
            log.info("机器人推送结果：{}", response);
        } catch (Exception e) {
            log.error("推送失败", e);
        }
    }
}