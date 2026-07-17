package com.julian.product_backend.domain.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Product {
    String id;
    String name;
    Double price;
    Boolean availability;
}
