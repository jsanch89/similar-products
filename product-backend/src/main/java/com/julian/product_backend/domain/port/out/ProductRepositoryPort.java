package com.julian.product_backend.domain.port.out;

import com.julian.product_backend.domain.model.Product;

import java.util.List;

public interface ProductRepositoryPort {
    List<Product> findSimilarByProductId(String productId);
}
