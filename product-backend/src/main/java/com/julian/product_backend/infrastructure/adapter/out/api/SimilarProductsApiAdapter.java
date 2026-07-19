package com.julian.product_backend.infrastructure.adapter.out.api;

import com.julian.product_backend.domain.exception.ProductNotFoundException;
import com.julian.product_backend.domain.model.Product;
import com.julian.product_backend.domain.port.out.ProductRepositoryPort;
import com.julian.product_backend.infrastructure.adapter.out.api.dto.ProductApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SimilarProductsApiAdapter implements ProductRepositoryPort {

    private final RestClient restClient;

    public List<String> fetchSimilarIds(String productId) {
        log.debug("Fetching similar IDs for productId={}", productId);
        try {
            String[] response = restClient.get()
                    .uri("/product/{productId}/similarids", productId)
                    .retrieve()
                    .body(String[].class);
            List<String> ids = Optional.ofNullable(response)
                    .map(Arrays::asList)
                    .orElse(Collections.emptyList());
            log.debug("Similar IDs for productId={}: {}", productId, ids);
            return ids;
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Similar IDs not found for productId={}", productId);
            throw new ProductNotFoundException(productId);
        } catch (HttpServerErrorException e) {
            log.error("Server error fetching similar IDs for productId={}: {} {}", productId, e.getStatusCode(), e.getMessage());
            return Collections.emptyList();
        }
    }

    public Optional<Product> fetchProductDetail(String productId) {
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
        } catch (HttpServerErrorException e) {
            log.error("Server error fetching product detail for productId={}: {} {}", productId, e.getStatusCode(), e.getMessage());
            return Optional.empty();
        }
    }
}
