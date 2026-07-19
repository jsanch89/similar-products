package com.julian.product_backend.domain.port.out;

import java.util.List;
import java.util.Optional;

import com.julian.product_backend.domain.model.Product;

public interface ProductRepositoryPort {

    List<String> fetchSimilarIds(String productId);

    Optional<Product> fetchProductDetail(String similarId);
}
