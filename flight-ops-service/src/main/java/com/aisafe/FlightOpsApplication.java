package com.aisafe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.aisafe"})
public class FlightOpsApplication {
    public static void main(String[] args) {
        SpringApplication.run(FlightOpsApplication.class, args);
    }
}