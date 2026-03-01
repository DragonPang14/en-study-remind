package com.electrocardiogram.esr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EnStudyRemindApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnStudyRemindApplication.class, args);
    }

}
