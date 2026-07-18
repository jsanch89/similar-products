package com.julian.product_backend.domain.port.out;

import com.julian.product_backend.domain.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductRepositoryPort {

    Flux<String> fetchSimilarIds(String productId);

    Mono<Product> fetchProductDetail(String productId);
    
}
