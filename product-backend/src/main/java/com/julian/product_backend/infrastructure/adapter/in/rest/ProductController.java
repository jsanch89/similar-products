package com.julian.product_backend.infrastructure.adapter.in.rest;

import com.julian.product_backend.domain.model.Product;
import com.julian.product_backend.domain.port.in.ProductUseCase;
import com.julian.product_backend.infrastructure.adapter.in.rest.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductUseCase productUseCase;

    @GetMapping("/{productId}/similar")
    public ResponseEntity<List<ProductResponse>> getSimilarProducts(@PathVariable String productId) {
        log.info("GET /product/{}/similar", productId);
        List<Product> products = productUseCase.similarProductsByIds(productId);
        List<ProductResponse> response = products.stream()
                .map(ProductResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(response);
    }
}
