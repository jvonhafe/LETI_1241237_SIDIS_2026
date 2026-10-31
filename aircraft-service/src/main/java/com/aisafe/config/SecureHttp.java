package com.aisafe.config;


import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.security.KeyStore;

/** Cria clientes HTTP que confiam no certificado dos serviços AISafe (truststore). */
public final class SecureHttp {

    private SecureHttp() {}

    public static ClientHttpRequestFactory requestFactory(String truststoreLocation, String truststorePassword) {
        try {
            Resource resource = new DefaultResourceLoader().getResource(truststoreLocation);
            if (!resource.exists()) {
                // Sem truststore usa a confiança por omissão da JVM (o certificado continua a ser validado).
                return new SimpleClientHttpRequestFactory();
            }
            KeyStore trustStore = KeyStore.getInstance("PKCS12");
            try (InputStream in = resource.getInputStream()) {
                trustStore.load(in, truststorePassword.toCharArray());
            }
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);
            HttpClient client = HttpClient.newBuilder()
                    .sslContext(sslContext)
                    .version(HttpClient.Version.HTTP_1_1)
                    .build();
            return new JdkClientHttpRequestFactory(client);
        } catch (Exception e) {
            throw new IllegalStateException("Falha a configurar TLS do cliente HTTP", e);
        }
    }
}