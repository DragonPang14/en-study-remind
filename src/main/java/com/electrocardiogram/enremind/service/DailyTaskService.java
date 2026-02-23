package com.electrocardiogram.enremind.service;

import com.electrocardiogram.enremind.dao.WordRepository;
import com.electrocardiogram.enremind.model.Word;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DailyTaskService {

    @Resource
    private WordRepository wordRepository;

    @Resource
    private WeChatService weChatService;

    // 每日早上 8:30 执行 (秒 分 时 日 月 周)
    // 测试时可以改为 "0 * * * * ?" 表示每分钟执行一次
    @Scheduled(cron = "0 30 8 * * ?")
    public void pushDailyEnglish() {
        // 1. 数据准备
        List<Word> newWords = wordRepository.findRandomWords(15); // 15个新词
        List<Word> reviewWords = wordRepository.findReviewWords(5); // 5个旧词复习
        List<Word> phrases = wordRepository.findRandomPhrases(5); // 5个短语

        // 2. 组装消息
        StringBuilder sb = new StringBuilder();
        sb.append("🌅 每日英语打卡 \n\n");

        sb.append("📖 【新词学习】 (15个)\n");
        appendWordsList(sb, newWords);

        sb.append("🔄 【温故知新】 (5个)\n");
        appendWordsList(sb, reviewWords);
        // 更新复习时间
        reviewWords.forEach(w -> {
            w.setReviewCount(w.getReviewCount() + 1);
            w.setLastReviewAt(LocalDateTime.now());
        });
        wordRepository.saveAll(reviewWords);

        sb.append("💬 【短语积累】 (5个)\n");
        appendWordsList(sb, phrases);

        sb.append("\n加油！又是进步的一天！💪");

        // 3. 推送
        weChatService.sendMessage(sb.toString());
    }

    private void appendWordsList(StringBuilder sb, List<Word> words) {
        for (int i = 0; i < words.size(); i++) {
            Word w = words.get(i);
            sb.append(i + 1).append(". ").append(w.getWord());
            if (w.getPhonetic() != null && !w.getPhonetic().isEmpty()) {
                sb.append(" ").append(w.getPhonetic());
            }
            sb.append("\n   ").append(w.getDefinition());
            if (w.getExample() != null && !w.getExample().isEmpty()) {
                sb.append("\n   例: ").append(w.getExample());
            }
            sb.append("\n");
        }
        sb.append("\n");
    }
}