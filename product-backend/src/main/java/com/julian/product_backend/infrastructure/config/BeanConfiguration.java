package com.julian.product_backend.infrastructure.config;

import com.julian.product_backend.domain.port.out.ProductRepositoryPort;
import com.julian.product_backend.domain.service.ProductService;
import io.netty.channel.ChannelOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class BeanConfiguration {

    @Bean
    public WebClient webClient(
            @Value("${similar.products.api.base-url}") String baseUrl,
            @Value("${similar.products.api.timeout-ms}") int timeoutMs) {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeoutMs)
                .responseTimeout(Duration.ofMillis(timeoutMs));

        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @Bean
    public ProductService productService(ProductRepositoryPort productRepositoryPort) {
        return new ProductService(productRepositoryPort);
    }
}
