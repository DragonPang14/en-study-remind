package com.electrocardiogram.esr.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "t_word", indexes = {
        // 新增高频筛选索引，解决词库杂乱问题，加速查询
        @Index(name = "idx_collins", columnList = "collins"),
        @Index(name = "idx_oxford", columnList = "oxford"),
        @Index(name = "idx_frq", columnList = "frq"),
        @Index(name = "idx_review", columnList = "review_count, last_review_at")
})
public class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ---------------------- ECDICT原生标准字段 ----------------------
    @Column(nullable = false, unique = true)
    private String word;        // 单词名称

    private String phonetic;    // 音标，以英语英标为主

    @Column(columnDefinition = "TEXT")
    private String definition;  // 单词释义（英文），每行一个释义

    @Column(columnDefinition = "TEXT")
    private String translation; // 单词释义（中文），每行一个释义（推送核心用这个）

    private String pos;         // 词语位置，用 "/" 分割不同词性

    private Integer collins;    // 柯林斯星级 0-5，星级越高越常用

    private Integer oxford;     // 是否是牛津三千核心词汇 1=是 0=否

    private String tag;         // 字符串标签：zk/中考，gk/高考，cet4/四级 等等标签，空格分割

    private Long bnc;           // 英国国家语料库词频顺序，数值越小越常用

    private Long frq;           // 当代语料库词频顺序，数值越小越常用

    @Column(columnDefinition = "TEXT")
    private String exchange;    // 时态复数等变换，使用 "/" 分割不同项目

    @Column(columnDefinition = "TEXT")
    private String detail;      // json扩展信息，字典形式保存例句

    private String audio;       // 读音音频url

    // ---------------------- 自定义复习功能专属字段 ----------------------
    private LocalDateTime lastReviewAt; // 最后一次复习时间
    private Integer reviewCount = 0;    // 复习次数
}