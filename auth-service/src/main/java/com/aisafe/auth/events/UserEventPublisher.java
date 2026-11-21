package com.aisafe.auth.events;

import com.aisafe.auth.audit.AuditLogger;
import com.aisafe.auth.model.User;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UserEventPublisher {

    private final RabbitTemplate rabbit;
    private final AuditLogger audit;
    private final String instanceId;

    public UserEventPublisher(RabbitTemplate rabbit, AuditLogger audit,
                              @Value("${aisafe.instance-id}") String instanceId) {
        this.rabbit = rabbit;
        this.audit = audit;
        this.instanceId = instanceId;
    }

    /** Tolerancia a falhas: se o broker estiver em baixo, o utilizador fica criado localmente e o erro fica no audit. */
    public void publishUserCreated(User user) {
        try {
            UserCreatedEvent event = new UserCreatedEvent(instanceId, user.getUsername(),
                    user.getPasswordHash(), user.getRole());
            rabbit.convertAndSend(UserEventsConfig.EXCHANGE, "", event.toMessage());
            audit.log("USER_EVENT_PUBLISH", instanceId, "SUCCESS", "-", "user=" + user.getUsername());
        } catch (Exception e) {
            audit.log("USER_EVENT_PUBLISH", instanceId, "ERROR", "-", "broker indisponivel");
        }
    }
}