package com.aisafe.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    // Todas as chamadas entre serviços vão por HTTPS e levam o token de serviço deste serviço.
    @Bean
    public RestTemplate restTemplate(ServiceTokenProvider tokens,
                                     @Value("${aisafe.tls.truststore:classpath:aisafe-truststore.p12}") String truststore,
                                     @Value("${aisafe.tls.truststore-password:changeit-dev}") String truststorePassword) {
        RestTemplate restTemplate = new RestTemplate(SecureHttp.requestFactory(truststore, truststorePassword));
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().set(HttpHeaders.AUTHORIZATION, "Bearer " + tokens.getToken());
            return execution.execute(request, body);
        });
        return restTemplate;
    }
}