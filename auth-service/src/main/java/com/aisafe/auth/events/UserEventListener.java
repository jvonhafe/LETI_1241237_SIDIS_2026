package com.aisafe.auth.events;

import com.aisafe.auth.audit.AuditLogger;
import com.aisafe.auth.model.Role;
import com.aisafe.auth.model.User;
import com.aisafe.auth.repository.UserRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UserEventListener {

    private final UserRepository users;
    private final AuditLogger audit;
    private final String instanceId;

    public UserEventListener(UserRepository users, AuditLogger audit,
                             @Value("${aisafe.instance-id}") String instanceId) {
        this.users = users;
        this.audit = audit;
        this.instanceId = instanceId;
    }

    @RabbitListener(queues = "#{userEventsQueue.name}")
    public void onUserCreated(String message) {
        try {
            UserCreatedEvent e = UserCreatedEvent.fromMessage(message);
            if (instanceId.equals(e.origin())) return;            // evento da propria instancia
            if (e.role() == Role.SERVICE) {                        // defesa em profundidade
                audit.log("USER_REPLICATE", e.origin(), "DENIED", "-", "role SERVICE nao replicavel");
                return;
            }
            if (users.existsByUsername(e.username())) return;      // idempotente
            users.save(new User(e.username(), e.passwordHash(), e.role()));
            audit.log("USER_REPLICATE", e.origin(), "SUCCESS", "-",
                    "created=" + e.username() + " role=" + e.role());
        } catch (Exception ex) {
            // Mensagem invalida: descartamos (senao o broker reentregava-a em ciclo).
            audit.log("USER_REPLICATE", "-", "ERROR", "-", "mensagem invalida descartada");
        }
    }
}