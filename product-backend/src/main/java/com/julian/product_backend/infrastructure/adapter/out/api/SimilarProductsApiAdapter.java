package com.julian.product_backend.infrastructure.adapter.out.api;

import com.julian.product_backend.domain.model.Product;
import com.julian.product_backend.domain.port.out.ProductRepositoryPort;
import com.julian.product_backend.infrastructure.adapter.out.api.dto.ProductApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SimilarProductsApiAdapter implements ProductRepositoryPort {

    private final RestTemplate restTemplate;

    @Override
    public List<Product> findSimilarByProductId(String productId) {
        List<Product> products = new ArrayList<>();
        for (String similarId : fetchSimilarIds(productId)) {
            fetchProductDetail(similarId).ifPresent(products::add);
        }
        return products;
    }

    private List<String> fetchSimilarIds(String productId) {
        try {
            String[] response = restTemplate.getForObject(
                    "/product/{productId}/similarids",
                    String[].class,
                    productId
            );
            return Optional.ofNullable(response)
                    .map(Arrays::asList)
                    .orElse(Collections.emptyList());
        } catch (HttpClientErrorException.NotFound e) {
            return Collections.emptyList();
        }
    }

    private Optional<Product> fetchProductDetail(String productId) {
        try {
            ProductApiResponse response = restTemplate.getForObject(
                    "/product/{productId}",
                    ProductApiResponse.class,
                    productId
            );
            return Optional.ofNullable(response).map(ProductApiResponse::toDomain);
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        }
    }
}
