package com.bolao.brasileirao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BolaoApplication {
    public static void main(String[] args) {
        SpringApplication.run(BolaoApplication.class, args);
    }
}
