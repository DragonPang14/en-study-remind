package com.electrocardiogram.enremind.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "t_word")
public class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String word;
    private String phonetic;

    @Column(length = 1024)
    private String definition;

    private String type; // WORD, PHRASE, COMPUTER

    @Column(length = 2048)
    private String example;

    private LocalDateTime lastReviewAt; // 最后一次复习时间
    private Integer reviewCount = 0; // 复习次数
}