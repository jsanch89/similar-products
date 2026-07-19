package com.julian.product_backend.infrastructure.adapter.out.api;

import com.julian.product_backend.domain.exception.ProductNotFoundException;
import com.julian.product_backend.domain.model.Product;
import com.julian.product_backend.infrastructure.adapter.out.api.dto.ProductApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("rawtypes")
class SimilarProductsApiAdapterTest {

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private SimilarProductsApiAdapter adapter;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        adapter = new SimilarProductsApiAdapter(restClient);
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(String.class), any(Object[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void fetchSimilarIds_returnsIds() {
        String[] similarIds = {"2", "3"};
        when(responseSpec.body(String[].class)).thenReturn(similarIds);

        List<String> result = adapter.fetchSimilarIds("1");

        assertThat(result).containsExactly("2", "3");
    }

    @Test
    void fetchSimilarIds_returnsEmptyList_whenResponseIsNull() {
        when(responseSpec.body(String[].class)).thenReturn(null);

        List<String> result = adapter.fetchSimilarIds("1");

        assertThat(result).isEmpty();
    }

    @Test
    void fetchSimilarIds_throwsProductNotFoundException_whenNotFound() {
        when(responseSpec.body(String[].class)).thenThrow(HttpClientErrorException.NotFound.class);

        assertThatThrownBy(() -> adapter.fetchSimilarIds("99"))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void fetchSimilarIds_returnsEmptyList_onServerError() {
        when(responseSpec.body(String[].class))
                .thenThrow(HttpServerErrorException.InternalServerError.class);

        List<String> result = adapter.fetchSimilarIds("1");

        assertThat(result).isEmpty();
    }

    @Test
    void fetchProductDetail_returnsMappedProduct() {
        ProductApiResponse response = buildApiResponse("2", "Phone B", 150.0, true);
        when(responseSpec.body(ProductApiResponse.class)).thenReturn(response);

        Optional<Product> result = adapter.fetchProductDetail("2");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("2");
        assertThat(result.get().getName()).isEqualTo("Phone B");
        assertThat(result.get().getPrice()).isEqualTo(150.0);
        assertThat(result.get().getAvailability()).isTrue();
    }

    @Test
    void fetchProductDetail_throwsProductNotFoundException_whenNotFound() {
        when(responseSpec.body(ProductApiResponse.class)).thenThrow(HttpClientErrorException.NotFound.class);

        assertThatThrownBy(() -> adapter.fetchProductDetail("99"))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void fetchProductDetail_returnsEmpty_onServerError() {
        when(responseSpec.body(ProductApiResponse.class))
                .thenThrow(HttpServerErrorException.InternalServerError.class);

        Optional<Product> result = adapter.fetchProductDetail("2");

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
