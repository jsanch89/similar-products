package com.julian.product_backend.infrastructure.adapter.out.api;

import com.julian.product_backend.domain.model.Product;
import com.julian.product_backend.infrastructure.adapter.out.api.dto.ProductApiResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimilarProductsApiAdapterTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private SimilarProductsApiAdapter adapter;

    @Test
    void findSimilarByProductId_returnsMappedProducts() {
        String productId = "1";
        String[] similarIds = {"2", "3"};

        ProductApiResponse response2 = buildApiResponse("2", "Phone B", 150.0, true);
        ProductApiResponse response3 = buildApiResponse("3", "Phone C", 200.0, false);

        when(restTemplate.getForObject(eq("/product/{productId}/similarids"), eq(String[].class), eq(productId)))
                .thenReturn(similarIds);
        when(restTemplate.getForObject(eq("/product/{productId}"), eq(ProductApiResponse.class), eq("2")))
                .thenReturn(response2);
        when(restTemplate.getForObject(eq("/product/{productId}"), eq(ProductApiResponse.class), eq("3")))
                .thenReturn(response3);

        List<Product> result = adapter.findSimilarByProductId(productId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo("2");
        assertThat(result.get(0).getName()).isEqualTo("Phone B");
        assertThat(result.get(0).getPrice()).isEqualTo(150.0);
        assertThat(result.get(0).getAvailability()).isTrue();
        assertThat(result.get(1).getId()).isEqualTo("3");
        assertThat(result.get(1).getAvailability()).isFalse();
    }

    @Test
    void findSimilarByProductId_returnsEmptyList_whenSimilarIdsNotFound() {
        String productId = "99";
        when(restTemplate.getForObject(eq("/product/{productId}/similarids"), eq(String[].class), eq(productId)))
                .thenThrow(HttpClientErrorException.NotFound.class);

        List<Product> result = adapter.findSimilarByProductId(productId);

        assertThat(result).isEmpty();
    }

    @Test
    void findSimilarByProductId_skipsProduct_whenDetailNotFound() {
        String productId = "1";
        String[] similarIds = {"2", "3"};

        ProductApiResponse response2 = buildApiResponse("2", "Phone B", 150.0, true);

        when(restTemplate.getForObject(eq("/product/{productId}/similarids"), eq(String[].class), eq(productId)))
                .thenReturn(similarIds);
        when(restTemplate.getForObject(eq("/product/{productId}"), eq(ProductApiResponse.class), eq("2")))
                .thenReturn(response2);
        when(restTemplate.getForObject(eq("/product/{productId}"), eq(ProductApiResponse.class), eq("3")))
                .thenThrow(HttpClientErrorException.NotFound.class);

        List<Product> result = adapter.findSimilarByProductId(productId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("2");
    }

    @Test
    void findSimilarByProductId_returnsEmptyList_whenSimilarIdsResponseIsNull() {
        String productId = "1";
        when(restTemplate.getForObject(eq("/product/{productId}/similarids"), eq(String[].class), eq(productId)))
                .thenReturn(null);

        List<Product> result = adapter.findSimilarByProductId(productId);

        assertThat(result).isEmpty();
    }

    private ProductApiResponse buildApiResponse(String id, String name, Double price, Boolean availability) {
        ProductApiResponse response = new ProductApiResponse();
        response.setId(id);
        response.setName(name);
        response.setPrice(price);
        response.setAvailability(availability);
        return response;
    }
}
