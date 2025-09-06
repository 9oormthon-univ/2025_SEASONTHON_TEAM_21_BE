package com.goorm.sslim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SslimApplication {

    public static void main(String[] args) {
        SpringApplication.run(SslimApplication.class, args);
    }

}
