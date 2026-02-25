package com.electrocardiogram.esr.service;

import com.electrocardiogram.esr.dao.WordRepository;
import com.electrocardiogram.esr.model.Word;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DailyTaskService {

    @Resource
    private WordRepository wordRepository;

    @Resource
    private WeChatService weChatService;

    // 每日早上8:30推送，测试可改为 0 * * * * ? 每分钟执行一次
    @Scheduled(cron = "0 30 8 * * ?")
    public void pushDailyEnglish() {
        // 1. 数据准备：高频热词+复习旧词+短语，完全匹配你的需求
        List<Word> newWords = wordRepository.findRandomHotWords(15);    // 15个高频新词
        List<Word> reviewWords = wordRepository.findReviewWords(5);      // 5个旧词复习
        List<Word> phrases = wordRepository.findRandomPhrases(5);        // 5个短语/固定搭配

        // 2. 校验数据，避免空推送
        if (newWords.isEmpty() && reviewWords.isEmpty() && phrases.isEmpty()) {
            weChatService.sendMessage("词库为空，请检查ECDICT词库是否导入成功！");
            return;
        }

        // 3. 拼接推送内容，适配新字段，排版更清晰
        StringBuilder content = new StringBuilder();
        content.append("🌅 每日英语打卡 | 外企进阶\n\n");

        // 新词学习板块
        content.append("📖 【高频新词学习】15个\n");
        appendWordList(content, newWords);

        // 温故知新板块
        content.append("🔄 【温故知新】5个\n");
        appendWordList(content, reviewWords);
        // 更新复习记录
        reviewWords.forEach(word -> {
            word.setReviewCount(word.getReviewCount() + 1);
            word.setLastReviewAt(LocalDateTime.now());
        });
        wordRepository.saveAll(reviewWords);

        // 短语积累板块
        content.append("💬 【短语积累】5个\n");
        appendWordList(content, phrases);

        content.append("\n✨ 每天进步一点点，外企offer在眼前！");

        // 4. 推送到微信
        weChatService.sendMessage(content.toString());
    }

    // 单词列表拼接工具方法，适配ECDICT字段
    private void appendWordList(StringBuilder sb, List<Word> words) {
        for (int i = 0; i < words.size(); i++) {
            Word word = words.get(i);
            // 序号+单词+音标
            sb.append(i + 1).append(". ").append(word.getWord());
            if (StringUtils.hasLength(word.getPhonetic())) {
                sb.append("  ").append(word.getPhonetic());
            }
            // 柯林斯星级+牛津核心词标记
            if (word.getCollins() != null && word.getCollins() > 0) {
                sb.append("  柯林斯").append(word.getCollins()).append("星");
            }
            if (word.getOxford() != null && word.getOxford() == 1) {
                sb.append("  牛津核心词");
            }
            sb.append("\n");
            // 词性+中文释义（核心学习内容）
            if (StringUtils.hasLength(word.getPos())) {
                sb.append("   【词性】").append(word.getPos()).append("\n");
            }
            sb.append("   【释义】").append(word.getTranslation().replace("\n", "；")).append("\n");
            sb.append("\n");
        }
    }
}