package com.electrocardiogram.esr.dao;

import com.electrocardiogram.esr.model.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {

    // ==================== 核心高频词查询（解决杂乱问题，优先用这个） ====================

    /**
     * 随机获取高频热词：牛津3000核心词 或 柯林斯3星及以上词汇，彻底过滤生僻词
     *
     * @param limit 抽取数量
     * @return 高频单词列表
     */
    @Query(value = "SELECT * FROM t_word " +
            "WHERE (oxford = 1 OR collins >= 3) " +
            "AND pos IS NOT NULL AND translation IS NOT NULL " +
            "ORDER BY RAND() LIMIT ?1", nativeQuery = true)
    List<Word> findRandomHotWords(int limit);

    /**
     * 随机获取计算机/IT相关词汇（通过tag筛选，适配你Java开发身份）
     *
     * @param limit 抽取数量
     * @return 计算机专业词汇列表
     */
    @Query(value = "SELECT * FROM t_word " +
            "WHERE tag LIKE '%computer%' OR tag LIKE '%it%' OR definition LIKE '%programming%' " +
            "AND translation IS NOT NULL " +
            "ORDER BY RAND() LIMIT ?1", nativeQuery = true)
    List<Word> findRandomComputerWords(int limit);

    // ==================== 短语/固定搭配查询 ====================

    /**
     * 随机获取短语/固定搭配（通过exchange字段筛选，排除单个单词）
     *
     * @param limit 抽取数量
     * @return 短语列表
     */
    @Query(value = "SELECT * FROM t_word " +
            "WHERE word LIKE '% %' AND translation IS NOT NULL " +
            "ORDER BY RAND() LIMIT ?1", nativeQuery = true)
    List<Word> findRandomPhrases(int limit);

    // ==================== 复习旧词查询 ====================

    /**
     * 获取需要复习的旧词，优先取复习次数少、很久没复习的高频词
     *
     * @param limit 抽取数量
     * @return 待复习单词列表
     */
    @Query(value = "SELECT * FROM t_word " +
            "WHERE (oxford = 1 OR collins >= 3) " +
            "ORDER BY review_count ASC, last_review_at ASC NULLS FIRST LIMIT ?1", nativeQuery = true)
    List<Word> findReviewWords(int limit);
}