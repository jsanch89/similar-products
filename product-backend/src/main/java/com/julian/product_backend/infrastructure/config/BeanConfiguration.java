package com.julian.product_backend.infrastructure.config;

import com.julian.product_backend.domain.port.out.ProductRepositoryPort;
import com.julian.product_backend.domain.service.ProductService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class BeanConfiguration {

    @Bean
    public RestClient restClient(
            @Value("${similar.products.api.base-url}") String baseUrl,
            @Value("${similar.products.api.timeout-ms:2000}") int timeoutMs) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(timeoutMs);
        factory.setReadTimeout(timeoutMs);
        return RestClient.builder().baseUrl(baseUrl).requestFactory(factory).build();
    }

    @Bean
    public ProductService productService(ProductRepositoryPort productRepositoryPort) {
        return new ProductService(productRepositoryPort);
    }
}
