package com.julian.product_backend.infrastructure.adapter.in.rest;

import com.julian.product_backend.domain.port.in.ProductUseCase;
import com.julian.product_backend.infrastructure.adapter.in.rest.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductUseCase productUseCase;

    @GetMapping("/{productId}/similar")
    public Mono<ResponseEntity<List<ProductResponse>>> getSimilarProducts(@PathVariable String productId) {
        return productUseCase.similarProductsByIds(productId)
                .map(ProductResponse::fromDomain)
                .collectList()
                .map(ResponseEntity::ok);
    }
}
