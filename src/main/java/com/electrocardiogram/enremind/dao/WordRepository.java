package com.electrocardiogram.enremind.dao;

import com.electrocardiogram.enremind.model.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {

    // 随机获取指定数量的单词 (不包含短语)
    @Query(value = "SELECT * FROM t_word WHERE type IN ('WORD', 'COMPUTER') ORDER BY RAND() LIMIT ?1", nativeQuery = true)
    List<Word> findRandomWords(int limit);

    // 随机获取指定数量的短语
    @Query(value = "SELECT * FROM t_word WHERE type IN ('PHRASE', 'COMPUTER_PHRASE') ORDER BY RAND() LIMIT ?1", nativeQuery = true)
    List<Word> findRandomPhrases(int limit);

    // 获取需要复习的旧词 (优先取复习次数少的，或者很久没看的)
    @Query(value = "SELECT * FROM t_word ORDER BY review_count ASC, last_review_at ASC NULLS FIRST LIMIT ?1", nativeQuery = true)
    List<Word> findReviewWords(int limit);
}