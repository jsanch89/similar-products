package com.julian.product_backend.infrastructure.adapter.out.api;

import com.julian.product_backend.domain.exception.ProductNotFoundException;
import com.julian.product_backend.domain.model.Product;
import com.julian.product_backend.domain.port.out.ProductRepositoryPort;
import com.julian.product_backend.infrastructure.adapter.out.api.dto.ProductApiResponse;
import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class SimilarProductsApiAdapter implements ProductRepositoryPort {

    private static final String INSTANCE = "externalApi";

    private final RestClient restClient;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RetryRegistry retryRegistry;
    private final BulkheadRegistry bulkheadRegistry;

    public List<String> fetchSimilarIds(String productId) {
        // Decoration order: CB (outer) → Retry → Bulkhead (inner) → method.
        // CB wraps the whole retry sequence, so it records one result per call,
        // not one per individual retry attempt.
        Supplier<List<String>> decorated = CircuitBreaker.decorateSupplier(
                circuitBreakerRegistry.circuitBreaker(INSTANCE),
                Retry.decorateSupplier(
                        retryRegistry.retry(INSTANCE),
                        Bulkhead.decorateSupplier(
                                bulkheadRegistry.bulkhead(INSTANCE),
                                () -> doFetchSimilarIds(productId)
                        )
                )
        );
        try {
            return decorated.get();
        } catch (ProductNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Resilience fallback for fetchSimilarIds productId={}: {}", productId, e.getMessage());
            return Collections.emptyList();
        }
    }

    public Optional<Product> fetchProductDetail(String productId) {
        Supplier<Optional<Product>> decorated = CircuitBreaker.decorateSupplier(
                circuitBreakerRegistry.circuitBreaker(INSTANCE),
                Retry.decorateSupplier(
                        retryRegistry.retry(INSTANCE),
                        Bulkhead.decorateSupplier(
                                bulkheadRegistry.bulkhead(INSTANCE),
                                () -> doFetchProductDetail(productId)
                        )
                )
        );
        try {
            return decorated.get();
        } catch (ProductNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Resilience fallback for fetchProductDetail productId={}: {}", productId, e.getMessage());
            return Optional.empty();
        }
    }

    private List<String> doFetchSimilarIds(String productId) {
        log.debug("Fetching similar IDs for productId={}", productId);
        try {
            String[] response = restClient.get()
                    .uri("/product/{productId}/similarids", productId)
                    .retrieve()
                    .body(String[].class);
            return Optional.ofNullable(response)
                    .map(Arrays::asList)
                    .orElse(Collections.emptyList());
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Similar IDs not found for productId={}", productId);
            throw new ProductNotFoundException(productId);
        }
    }

    private Optional<Product> doFetchProductDetail(String productId) {
        log.debug("Fetching product detail for productId={}", productId);
        try {
            ProductApiResponse response = restClient.get()
                    .uri("/product/{productId}", productId)
                    .retrieve()
                    .body(ProductApiResponse.class);
            return Optional.ofNullable(response).map(ProductApiResponse::toDomain);
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Product detail not found for productId={}", productId);
            throw new ProductNotFoundException(productId);
        }
    }
}
