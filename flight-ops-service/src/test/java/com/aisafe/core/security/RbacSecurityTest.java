package com.aisafe.core.security;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Testa o controlo de acesso (RBAC) com a cadeia de segurança real do serviço. */
@SpringBootTest
class RbacSecurityTest {

    private static final String URL = "/api/flights";
    private static final String ALLOWED_ROLE = "ATCC";
    private static final String DENIED_ROLE = "MAINTENANCE_TECH";

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    @Test
    void semTokenDevolve401() throws Exception {
        mvc.perform(get(URL)).andExpect(status().isUnauthorized());
    }

    @Test
    void tokenInvalidoDevolve401() throws Exception {
        mvc.perform(get(URL).header("Authorization", "Bearer token.invalido.qualquer"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void roleComPermissaoDevolve200() throws Exception {
        mvc.perform(get(URL).with(user("u").roles(ALLOWED_ROLE))).andExpect(status().isOk());
    }

    @Test
    void roleSemPermissaoDevolve403() throws Exception {
        mvc.perform(get(URL).with(user("u").roles(DENIED_ROLE))).andExpect(status().isForbidden());
    }

    @Test
    void contaDeServicoNaoAcedeAosVoos() throws Exception {
        mvc.perform(get(URL).with(user("svc").roles("SERVICE"))).andExpect(status().isForbidden());
    }
}