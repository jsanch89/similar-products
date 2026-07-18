package com.julian.product_backend.domain.service;

import com.julian.product_backend.domain.model.Product;
import com.julian.product_backend.domain.port.in.ProductUseCase;
import com.julian.product_backend.domain.port.out.ProductRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class ProductService implements ProductUseCase {

    private final ProductRepositoryPort productRepositoryPort;

    @Override
    public Flux<Product> similarProductsByIds(String productId) {
        return productRepositoryPort.fetchSimilarIds(productId)
                .flatMap(productRepositoryPort::fetchProductDetail);
    }
}
