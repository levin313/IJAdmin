package com.levin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * SpringBoot程序入口
 *
 * @author levin
 * @date 2018-10-01 00:14
 */
@SpringBootApplication
@EnableCaching
@EnableScheduling
@EnableTransactionManagement
public class Application {

    @Bean
    public ThreadPoolTaskScheduler getTaskPoolTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(5);
        return threadPoolTaskScheduler;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
