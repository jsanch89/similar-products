package com.julian.product_backend.infrastructure.config;

import com.julian.product_backend.domain.port.out.ProductRepositoryPort;
import com.julian.product_backend.domain.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class BeanConfigurationTest {

    private final BeanConfiguration beanConfiguration = new BeanConfiguration();

    @Test
    void restClient_isConfiguredWithBaseUrl() {
        RestClient restClient = beanConfiguration.restClient("http://localhost:3001");

        assertThat(restClient).isNotNull();
    }

    @Test
    void productService_isCreatedWithRepositoryPort() {
        ProductRepositoryPort repositoryPort = mock(ProductRepositoryPort.class);

        ProductService productService = beanConfiguration.productService(repositoryPort);

        assertThat(productService).isNotNull();
    }
}
