package com.aisafe.auth.events;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserEventsConfig {

    public static final String EXCHANGE = "aisafe.auth.users";

    @Bean
    public FanoutExchange userEventsExchange() {
        return new FanoutExchange(EXCHANGE, true, false);
    }

    /** Uma fila por instancia: cada instancia recebe TODOS os eventos (mesmo os que chegarem enquanto esteve em baixo). */
    @Bean
    public Queue userEventsQueue(@Value("${aisafe.instance-id}") String instanceId) {
        return new Queue("auth.users." + instanceId, true);
    }

    @Bean
    public Binding userEventsBinding(Queue userEventsQueue, FanoutExchange userEventsExchange) {
        return BindingBuilder.bind(userEventsQueue).to(userEventsExchange);
    }
}