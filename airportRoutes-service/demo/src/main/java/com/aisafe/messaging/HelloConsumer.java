package com.aisafe.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class HelloConsumer {

    @RabbitListener(queues = "hello-queue")
    public void receiveMessage(String message) {
        System.out.println(" [x] Mensagem recebida com sucesso: " + message);
    }
}