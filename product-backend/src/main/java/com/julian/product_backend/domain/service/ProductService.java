package com.julian.product_backend.domain.service;

import com.julian.product_backend.domain.model.Product;
import com.julian.product_backend.domain.port.in.ProductUseCase;
import com.julian.product_backend.domain.port.out.ProductRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class ProductService implements ProductUseCase {

    private final ProductRepositoryPort productRepositoryPort;

    @Override
    public List<Product> similarProductsByIds(String productId) {
        log.info("Fetching similar products for productId={}", productId);
        List<Product> products = new ArrayList<>();
        for (String similarId : productRepositoryPort.fetchSimilarIds(productId)) {
            productRepositoryPort.fetchProductDetail(similarId).ifPresent(products::add);
        }
        log.info("Found {} similar products for productId={}", products.size(), productId);
        return products;
    }
}
