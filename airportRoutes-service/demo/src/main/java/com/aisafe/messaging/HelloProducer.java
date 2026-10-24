package com.aisafe.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class HelloProducer implements CommandLineRunner {
    private final RabbitTemplate rabbitTemplate;

    public HelloProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void run(String... args) {
        rabbitTemplate.convertAndSend("hello-queue", "Olá RabbitMQ! Mensagem de teste.");
        System.out.println(" [x] Mensagem enviada para o RabbitMQ!");
    }
}