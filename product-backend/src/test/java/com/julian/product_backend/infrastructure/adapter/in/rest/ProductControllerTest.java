package com.julian.product_backend.infrastructure.adapter.in.rest;

import com.julian.product_backend.domain.model.Product;
import com.julian.product_backend.domain.port.in.ProductUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductUseCase productUseCase;

    @InjectMocks
    private ProductController productController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
    }

    @Test
    void getSimilarProducts_returns200WithProducts() throws Exception {
        String productId = "1";
        List<Product> products = List.of(
                Product.builder().id("2").name("Phone B").price(150.0).availability(true).build(),
                Product.builder().id("3").name("Phone C").price(200.0).availability(false).build()
        );
        when(productUseCase.similarProductsByIds(productId)).thenReturn(products);

        mockMvc.perform(get("/product/{productId}/similar", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("2"))
                .andExpect(jsonPath("$[0].name").value("Phone B"))
                .andExpect(jsonPath("$[0].price").value(150.0))
                .andExpect(jsonPath("$[0].availability").value(true))
                .andExpect(jsonPath("$[1].id").value("3"))
                .andExpect(jsonPath("$[1].name").value("Phone C"))
                .andExpect(jsonPath("$[1].availability").value(false));
    }

    @Test
    void getSimilarProducts_returns200WithEmptyList_whenNoSimilarProducts() throws Exception {
        String productId = "99";
        when(productUseCase.similarProductsByIds(productId)).thenReturn(List.of());

        mockMvc.perform(get("/product/{productId}/similar", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
