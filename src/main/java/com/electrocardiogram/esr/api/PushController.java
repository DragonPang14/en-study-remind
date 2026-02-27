package com.electrocardiogram.esr.api;

import com.electrocardiogram.esr.service.DailyTaskService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/push")
public class PushController {
    @Resource
    private DailyTaskService dailyTaskService;

    /**
     * 手动触发一次【每日英语推送】（高频词+复习旧词+短语）
     * 访问地址：http://localhost:8080/api/push/push-daily
     */
    @GetMapping("/push-daily")
    public String manualPushDaily() {
        try {
            dailyTaskService.pushDailyEnglish();
            return "✅ 每日英语推送成功！请查看你的微信（企业微信插件）。";
        } catch (Exception e) {
            e.printStackTrace();
            return "❌ 推送失败：" + e.getMessage();
        }
    }

}
