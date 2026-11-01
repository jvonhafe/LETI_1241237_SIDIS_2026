package com.aisafe.auth.audit;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Audit logging: uma linha por pedido HTTP (quem, o quê, quando, resultado).
 * Corre DENTRO da cadeia de segurança, antes da validação do token, para registar também os pedidos
 * recusados (401/403). Nunca regista tokens, passwords, corpos nem parâmetros da URL.
 * Não é um @Component: é adicionado ao SecurityFilterChain (evita registo duplicado).
 */
public class AuditLoggingFilter extends OncePerRequestFilter {

    private static final Logger AUDIT = LoggerFactory.getLogger("AUDIT");

    private final String serviceName;

    public AuditLoggingFilter(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        long start = System.nanoTime();
        int status = 500; // se o pedido rebentar, fica registado como erro
        try {
            chain.doFilter(request, response);
            status = response.getStatus();
        } finally {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String actor = "anonymous";
            String role = "-";
            if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
                actor = auth.getName();
                role = auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).filter(Objects::nonNull)
                        .filter(a -> a.startsWith("ROLE_"))
                        .collect(Collectors.joining(","));
            }
            long ms = (System.nanoTime() - start) / 1_000_000;
            AUDIT.info("service={} action=HTTP actor={} role={} method={} path={} status={} outcome={} ip={} durationMs={}",
                    serviceName, sanitize(actor), sanitize(role), sanitize(request.getMethod()),
                    sanitize(request.getRequestURI()), status, outcome(status),
                    sanitize(request.getRemoteAddr()), ms);
        }
    }

    private static String outcome(int status) {
        if (status == 401 || status == 403) return "DENIED";
        if (status >= 500) return "ERROR";
        if (status >= 400) return "REJECTED";
        return "SUCCESS";
    }

    /** Evita log injection: remove quebras de linha e aspas de valores controlados pelo cliente. */
    private static String sanitize(String v) {
        if (v == null || v.isEmpty()) return "-";
        String s = v.replaceAll("[\\r\\n\"]", "_");
        return s.length() > 200 ? s.substring(0, 200) : s;
    }
}