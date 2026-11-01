package com.aisafe.config;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Testa o controlo de acesso (RBAC) com a cadeia de segurança real do serviço. */
@SpringBootTest
class RbacSecurityTest {

    // AirportController: ADMIN, ATCC, BACKOFFICE, SERVICE
    private static final String AIRPORTS_URL = "/api/airports/grouped";
    // RouteController: ADMIN, ATCC
    private static final String ROUTES_URL = "/api/routes/active/sorted";

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    @Test
    void semTokenDevolve401() throws Exception {
        mvc.perform(get(AIRPORTS_URL)).andExpect(status().isUnauthorized());
        mvc.perform(get(ROUTES_URL)).andExpect(status().isUnauthorized());
    }

    @Test
    void tokenInvalidoDevolve401() throws Exception {
        mvc.perform(get(AIRPORTS_URL).header("Authorization", "Bearer token.invalido.qualquer"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void roleSemPermissaoDevolve403() throws Exception {
        mvc.perform(get(AIRPORTS_URL).with(user("u").roles("MAINTENANCE_TECH"))).andExpect(status().isForbidden());
        mvc.perform(get(ROUTES_URL).with(user("u").roles("MAINTENANCE_TECH"))).andExpect(status().isForbidden());
    }

    @Test
    void contaDeServicoPodeLerAeroportosMasNaoRotas() throws Exception {
        // SERVICE só tem acesso de leitura aos aeroportos (usado entre serviços)
        int airports = mvc.perform(get(AIRPORTS_URL).with(user("svc").roles("SERVICE")))
                .andReturn().getResponse().getStatus();
        assertNotEquals(401, airports);
        assertNotEquals(403, airports);
        mvc.perform(get(ROUTES_URL).with(user("svc").roles("SERVICE"))).andExpect(status().isForbidden());
    }

    @Test
    void roleComPermissaoNaoEBloqueada() throws Exception {
        int status = mvc.perform(get(ROUTES_URL).with(user("u").roles("ATCC")))
                .andReturn().getResponse().getStatus();
        assertNotEquals(401, status);
        assertNotEquals(403, status);
    }
}