package com.julian.product_backend.infrastructure.adapter.in.rest.dto;

import com.julian.product_backend.domain.model.Product;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProductResponse {
    String id;
    String name;
    Double price;
    Boolean availability;

    public static ProductResponse fromDomain(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .availability(product.getAvailability())
                .build();
    }
}
