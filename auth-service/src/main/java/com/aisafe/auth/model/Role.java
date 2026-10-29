package com.aisafe.auth.model;

/** Roles do AISafe (as mesmas do PSOFT). No JWT vao como "ADMIN", "ATCC", ... e o Spring junta o prefixo ROLE_. */
public enum Role {
    ADMIN,
    BACKOFFICE,
    ATCC,
    MAINTENANCE_TECH,
    MAINTENANCE_SUPERVISOR,
    /** Papel tecnico: tokens emitidos a servicos (nao a pessoas). */
    SERVICE
}
