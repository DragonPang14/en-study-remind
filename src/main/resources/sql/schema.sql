-- 单词主表，完全适配ECDICT字段，兼容复习功能
CREATE TABLE IF NOT EXISTS t_word (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    word VARCHAR(255) NOT NULL UNIQUE,
    phonetic VARCHAR(255),
    definition TEXT,
    translation TEXT,
    pos VARCHAR(255),
    collins INT DEFAULT 0,
    oxford INT DEFAULT 0,
    tag VARCHAR(500),
    bnc BIGINT,
    frq BIGINT,
    exchange TEXT,
    detail TEXT,
    audio VARCHAR(500),
    last_review_at TIMESTAMP,
    review_count INT DEFAULT 0
);

-- 高频筛选索引，解决词库杂乱问题，加速每日推送查询
CREATE INDEX IF NOT EXISTS idx_collins ON t_word(collins);
CREATE INDEX IF NOT EXISTS idx_oxford ON t_word(oxford);
CREATE INDEX IF NOT EXISTS idx_frq ON t_word(frq);
CREATE INDEX IF NOT EXISTS idx_review ON t_word(review_count, last_review_at);