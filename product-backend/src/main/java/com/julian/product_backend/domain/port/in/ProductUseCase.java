package com.julian.product_backend.domain.port.in;

import com.julian.product_backend.domain.model.Product;
import reactor.core.publisher.Flux;

public interface ProductUseCase {
    Flux<Product> similarProductsByIds(String productId);
}
