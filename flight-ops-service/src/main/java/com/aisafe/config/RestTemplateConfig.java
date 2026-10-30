package com.aisafe.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    // Todas as chamadas entre serviços levam o token de serviço deste serviço.
    @Bean
    public RestTemplate restTemplate(ServiceTokenProvider tokens) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().set(HttpHeaders.AUTHORIZATION, "Bearer " + tokens.getToken());
            return execution.execute(request, body);
        });
        return restTemplate;
    }
}