package com.julian.product_backend.infrastructure.adapter.in.rest;

import com.julian.product_backend.domain.model.Product;
import com.julian.product_backend.domain.port.in.ProductUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductUseCase productUseCase;

    @InjectMocks
    private ProductController productController;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToController(productController).build();
    }

    @Test
    void getSimilarProducts_returns200WithProducts() {
        String productId = "1";
        when(productUseCase.similarProductsByIds(productId)).thenReturn(Flux.just(
                Product.builder().id("2").name("Phone B").price(150.0).availability(true).build(),
                Product.builder().id("3").name("Phone C").price(200.0).availability(false).build()
        ));

        webTestClient.get()
                .uri("/product/{productId}/similar", productId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].id").isEqualTo("2")
                .jsonPath("$[0].name").isEqualTo("Phone B")
                .jsonPath("$[0].price").isEqualTo(150.0)
                .jsonPath("$[0].availability").isEqualTo(true)
                .jsonPath("$[1].id").isEqualTo("3")
                .jsonPath("$[1].name").isEqualTo("Phone C")
                .jsonPath("$[1].availability").isEqualTo(false);
    }

    @Test
    void getSimilarProducts_returns200WithEmptyList_whenNoSimilarProducts() {
        String productId = "99";
        when(productUseCase.similarProductsByIds(productId)).thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/product/{productId}/similar", productId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(0);
    }
}
