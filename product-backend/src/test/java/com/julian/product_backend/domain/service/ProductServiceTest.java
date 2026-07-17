package com.julian.product_backend.domain.service;

import com.julian.product_backend.domain.model.Product;
import com.julian.product_backend.domain.port.out.ProductRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepositoryPort productRepositoryPort;

    @InjectMocks
    private ProductService productService;

    @Test
    void similarProductsByIds_returnsProductsFromRepository() {
        String productId = "1";
        List<Product> expected = List.of(
                Product.builder().id("2").name("Phone B").price(150.0).availability(true).build(),
                Product.builder().id("3").name("Phone C").price(200.0).availability(false).build()
        );
        when(productRepositoryPort.findSimilarByProductId(productId)).thenReturn(expected);

        List<Product> result = productService.similarProductsByIds(productId);

        assertThat(result).isEqualTo(expected);
        verify(productRepositoryPort).findSimilarByProductId(productId);
    }

    @Test
    void similarProductsByIds_returnsEmptyList_whenNoSimilarProductsFound() {
        String productId = "99";
        when(productRepositoryPort.findSimilarByProductId(productId)).thenReturn(List.of());

        List<Product> result = productService.similarProductsByIds(productId);

        assertThat(result).isEmpty();
    }
}
