package com.aisafe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.aisafe"})
public class AisafeApplication {

    public static void main(String[] args) {
        SpringApplication.run(AisafeApplication.class, args);
    }
}