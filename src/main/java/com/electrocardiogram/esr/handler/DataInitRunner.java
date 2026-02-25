package com.electrocardiogram.esr.handler;

import com.electrocardiogram.esr.dao.WordRepository;
import com.electrocardiogram.esr.model.Word;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class DataInitRunner implements CommandLineRunner {

    @Autowired
    private WordRepository wordRepository;

    // ECDICT CSV文件路径，下载后直接放入resources/data/目录即可
    private static final String ECDICT_CSV_PATH = "data/ecdict.csv";

    @Override
    public void run(String... args) throws Exception {
        // 防重复导入：数据库已有数据则直接跳过
        if (wordRepository.count() > 0) {
            log.info("数据库已存在单词数据，跳过ECDICT词库初始化导入");
            return;
        }

        // 检查CSV文件是否存在
        ClassPathResource resource = new ClassPathResource(ECDICT_CSV_PATH);
        if (!resource.exists()) {
            log.warn("未找到ECDICT词库文件，请将ecdict.csv放入 src/main/resources/data/ 目录下");
            return;
        }

        log.info("开始导入ECDICT词库，这可能需要1-2分钟，请耐心等待...");
        List<Word> wordList = new ArrayList<>();

        // 解析ECDICT原生CSV，完全适配官方表头
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                     .withFirstRecordAsHeader()
                     .withIgnoreHeaderCase()
                     .withTrim()
                     .withIgnoreSurroundingSpaces())) {

            for (CSVRecord record : csvParser) {
                Word word = new Word();
                // 1:1映射ECDICT所有字段，容错处理空值
                word.setWord(getRecordValue(record, "word"));
                word.setPhonetic(getRecordValue(record, "phonetic"));
                word.setDefinition(getRecordValue(record, "definition"));
                word.setTranslation(getRecordValue(record, "translation"));
                word.setPos(getRecordValue(record, "pos"));
                word.setCollins(parseInt(getRecordValue(record, "collins")));
                word.setOxford(parseInt(getRecordValue(record, "oxford")));
                word.setTag(getRecordValue(record, "tag"));
                word.setBnc(parseLong(getRecordValue(record, "bnc")));
                word.setFrq(parseLong(getRecordValue(record, "frq")));
                word.setExchange(getRecordValue(record, "exchange"));
                word.setDetail(getRecordValue(record, "detail"));
                word.setAudio(getRecordValue(record, "audio"));
                word.setReviewCount(0);

                // 过滤无效数据：无单词、无中文释义的直接跳过，避免推送空内容
                if (!StringUtils.hasLength(word.getWord()) || !StringUtils.hasLength(word.getTranslation())) {
                    continue;
                }

                wordList.add(word);

                // 批量插入，每1000条存一次，避免内存溢出
                if (wordList.size() >= 1000) {
                    wordRepository.saveAll(wordList);
                    wordList.clear();
                    log.info("已导入 {} 条单词数据", csvParser.getCurrentLineNumber());
                }
            }

            // 插入剩余不足1000条的数据
            if (!wordList.isEmpty()) {
                wordRepository.saveAll(wordList);
            }

            log.info("ECDICT词库导入完成！总计导入 {} 条有效单词数据", wordRepository.count());

        } catch (Exception e) {
            log.error("ECDICT词库导入失败", e);
        }
    }

    // 安全获取CSV字段值，空值返回空字符串
    private String getRecordValue(CSVRecord record, String columnName) {
        if (record.isMapped(columnName) && record.get(columnName) != null) {
            return record.get(columnName).trim();
        }
        return "";
    }

    // 安全解析数字，空值返回0
    private Integer parseInt(String value) {
        if (!StringUtils.hasLength(value)) {
            return 0;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // 安全解析长数字，空值返回null
    private Long parseLong(String value) {
        if (!StringUtils.hasLength(value)) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}