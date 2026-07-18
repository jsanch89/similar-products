package com.julian.product_backend.domain.service;

import com.julian.product_backend.domain.model.Product;
import com.julian.product_backend.domain.port.out.ProductRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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
        Product p2 = Product.builder().id("2").name("Phone B").price(150.0).availability(true).build();
        Product p3 = Product.builder().id("3").name("Phone C").price(200.0).availability(false).build();

        when(productRepositoryPort.fetchSimilarIds(productId)).thenReturn(Flux.just("2", "3"));
        when(productRepositoryPort.fetchProductDetail("2")).thenReturn(Mono.just(p2));
        when(productRepositoryPort.fetchProductDetail("3")).thenReturn(Mono.just(p3));

        StepVerifier.create(productService.similarProductsByIds(productId))
                .expectNext(p2, p3)
                .verifyComplete();

        verify(productRepositoryPort).fetchSimilarIds(productId);
        verify(productRepositoryPort).fetchProductDetail("2");
        verify(productRepositoryPort).fetchProductDetail("3");
    }

    @Test
    void similarProductsByIds_returnsEmptyFlux_whenNoSimilarProductsFound() {
        String productId = "99";
        when(productRepositoryPort.fetchSimilarIds(productId)).thenReturn(Flux.empty());

        StepVerifier.create(productService.similarProductsByIds(productId))
                .verifyComplete();
    }
}
