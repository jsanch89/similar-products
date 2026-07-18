package com.julian.product_backend.infrastructure.adapter.out.api;

import com.julian.product_backend.domain.model.Product;
import com.julian.product_backend.domain.port.out.ProductRepositoryPort;
import com.julian.product_backend.infrastructure.adapter.out.api.dto.ProductApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class SimilarProductsApiAdapter implements ProductRepositoryPort {

    private final WebClient webClient;

    public Flux<String> fetchSimilarIds(String productId) {
        return webClient.get()
                .uri("/product/{productId}/similarids", productId)
                .retrieve()
                .bodyToMono(String[].class)
                .flatMapMany(Flux::fromArray)
                .onErrorResume(WebClientResponseException.NotFound.class, e -> Flux.empty());
    }

    public Mono<Product> fetchProductDetail(String productId) {
        return webClient.get()
                .uri("/product/{productId}", productId)
                .retrieve()
                .bodyToMono(ProductApiResponse.class)
                .map(ProductApiResponse::toDomain)
                .onErrorResume(WebClientResponseException.NotFound.class, e -> Mono.empty());
    }
}
