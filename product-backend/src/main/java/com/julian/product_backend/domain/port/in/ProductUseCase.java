package com.julian.product_backend.domain.port.in;

import com.julian.product_backend.domain.model.Product;

import java.util.List;

public interface ProductUseCase {
    List<Product> similarProductsByIds(String productId);
}
