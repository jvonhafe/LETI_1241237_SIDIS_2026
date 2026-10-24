package com.aisafe.config; // ou o package onde colocaste

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Bean
    public Queue helloQueue() {
        // Mudar de false para true!
        return new Queue("hello-queue", true);
    }
}