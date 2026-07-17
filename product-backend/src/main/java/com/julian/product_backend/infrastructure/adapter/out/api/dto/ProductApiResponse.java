package com.julian.product_backend.infrastructure.adapter.out.api.dto;

import com.julian.product_backend.domain.model.Product;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductApiResponse {
    private String id;
    private String name;
    private Double price;
    private Boolean availability;

    public Product toDomain() {
        return Product.builder()
                .id(id)
                .name(name)
                .price(price)
                .availability(availability)
                .build();
    }
}
