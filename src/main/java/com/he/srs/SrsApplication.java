package com.he.srs;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author hezhizhen
 * @Description TODO
 * @CreateTime 2021/12/09 10:58
 */
@EnableAsync
@EnableScheduling
@SpringBootApplication
@MapperScan("com.he.srs.dao")
public class SrsApplication {
    public static void main(String[] args) {
        SpringApplication springApplication =
                new SpringApplication(SrsApplication.class);
        springApplication.setBannerMode(Banner.Mode.OFF);
        springApplication.run(args);
    }
}
