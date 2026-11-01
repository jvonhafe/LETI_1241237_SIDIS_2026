package com.aisafe.config;


import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuditLoggingFilterTest {

    private static final String SERVICE = "aircraft-service";

    private Logger auditLogger;
    private ListAppender<ILoggingEvent> appender;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        auditLogger = (Logger) LoggerFactory.getLogger("AUDIT");
        appender = new ListAppender<>();
        appender.start();
        auditLogger.addAppender(appender);
    }

    @AfterEach
    void tearDown() {
        auditLogger.detachAppender(appender);
        SecurityContextHolder.clearContext();
    }

    private static Authentication user(String name, String role) {
        return new UsernamePasswordAuthenticationToken(name, null, List.of(new SimpleGrantedAuthority(role)));
    }

    /** Executa o filtro com um pedido simulado; a "cadeia" define o estado HTTP e, opcionalmente, o utilizador. */
    private String run(String path, int status, Authentication auth) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", path);
        request.addHeader("Authorization", "Bearer eyJsegredo.payload.assinatura");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = (rq, rs) -> {
            ((HttpServletResponse) rs).setStatus(status);
            if (auth != null) {
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        };
        new AuditLoggingFilter(SERVICE).doFilter(request, response, chain);
        assertEquals(1, appender.list.size(), "deve existir exatamente uma linha de auditoria");
        return appender.list.get(0).getFormattedMessage();
    }

    @Test
    void pedidoSemTokenFicaRegistadoComoDeniedAnonimo() throws Exception {
        String line = run("/api/aircraft-models", 401, null);
        assertTrue(line.contains("actor=anonymous"));
        assertTrue(line.contains("status=401"));
        assertTrue(line.contains("outcome=DENIED"));
    }

    @Test
    void acessoProibidoFicaRegistadoComoDeniedComUtilizadorERole() throws Exception {
        String line = run("/api/aircraft-models", 403, user("tech", "ROLE_MAINTENANCE_TECH"));
        assertTrue(line.contains("actor=tech"));
        assertTrue(line.contains("role=ROLE_MAINTENANCE_TECH"));
        assertTrue(line.contains("outcome=DENIED"));
    }

    @Test
    void pedidoComSucessoFicaRegistadoComoSuccess() throws Exception {
        String line = run("/api/aircraft-models", 200, user("atcc_user", "ROLE_ATCC"));
        assertTrue(line.contains("service=" + SERVICE));
        assertTrue(line.contains("outcome=SUCCESS"));
    }

    @Test
    void erroDoServidorFicaRegistadoComoError() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/aircraft-models");
        FilterChain chain = (rq, rs) -> { throw new IllegalStateException("falha"); };
        assertThrows(IllegalStateException.class,
                () -> new AuditLoggingFilter(SERVICE).doFilter(request, new MockHttpServletResponse(), chain));
        String line = appender.list.get(0).getFormattedMessage();
        assertTrue(line.contains("status=500"));
        assertTrue(line.contains("outcome=ERROR"));
    }

    @Test
    void nuncaRegistaOTokenNemOCabecalhoAuthorization() throws Exception {
        String line = run("/api/aircraft-models", 200, user("atcc_user", "ROLE_ATCC"));
        assertFalse(line.contains("eyJ"));
        assertFalse(line.contains("Bearer"));
        assertFalse(line.contains("assinatura"));
    }

    @Test
    void caminhoComQuebrasDeLinhaNaoPermiteInjetarLinhasFalsas() throws Exception {
        String line = run("/api/x\nservice=fake action=LOGIN outcome=SUCCESS", 200, null);
        assertFalse(line.contains("\n"));
        assertFalse(line.contains("\r"));
    }
}