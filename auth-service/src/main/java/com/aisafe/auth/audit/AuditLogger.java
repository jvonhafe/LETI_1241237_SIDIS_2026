package com.aisafe.auth.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Audit logging: uma linha por evento de seguranca, formato key=value, num logger proprio ("AUDIT")
 * para poder ser encaminhado para ficheiro/SIEM. Nunca regista passwords nem tokens.
 */
@Component
public class AuditLogger {

    private static final Logger AUDIT = LoggerFactory.getLogger("AUDIT");

    public void log(String action, String actor, String outcome, String ip, String detail) {
        AUDIT.info("service=auth-service action={} actor={} outcome={} ip={} detail=\"{}\"",
                action, sanitize(actor), outcome, sanitize(ip), sanitize(detail));
    }

    /** Evita log injection (quebras de linha / aspas) em valores controlados pelo cliente. */
    static String sanitize(String v) {
        if (v == null) return "-";
        String s = v.replaceAll("[\\r\\n\"]", "_");
        return s.length() > 100 ? s.substring(0, 100) : s;
    }
}
