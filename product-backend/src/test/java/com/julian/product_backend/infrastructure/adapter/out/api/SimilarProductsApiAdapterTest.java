package com.julian.product_backend.infrastructure.adapter.out.api;

import com.julian.product_backend.domain.model.Product;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class SimilarProductsApiAdapterTest {

    private MockWebServer mockWebServer;
    private SimilarProductsApiAdapter adapter;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();
        adapter = new SimilarProductsApiAdapter(webClient);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void fetchSimilarIds_shouldReturnIds_whenResponseIsSuccessful() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setBody("[\"1\", \"2\", \"3\"]")
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(adapter.fetchSimilarIds("1"))
                .expectNext("1", "2", "3")
                .verifyComplete();

        RecordedRequest recordedRequest = mockWebServer.takeRequest(1, TimeUnit.SECONDS);
        assertThat(recordedRequest).isNotNull();
        assertThat(recordedRequest.getPath()).isEqualTo("/product/1/similarids");
    }

    @Test
    void fetchSimilarIds_shouldReturnEmptyFlux_whenResponseIsEmptyArray() {
        mockWebServer.enqueue(new MockResponse()
                .setBody("[]")
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(adapter.fetchSimilarIds("1"))
                .verifyComplete();
    }

    @Test
    void fetchSimilarIds_shouldReturnEmptyFlux_whenNotFound() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));

        StepVerifier.create(adapter.fetchSimilarIds("unknown"))
                .verifyComplete();
    }

    @Test
    void fetchSimilarIds_shouldPropagateError_whenServerError() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        StepVerifier.create(adapter.fetchSimilarIds("1"))
                .expectError()
                .verify();
    }

    @Test
    void fetchProductDetail_shouldReturnProduct_whenResponseIsSuccessful() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"id\":\"1\",\"name\":\"Product 1\",\"price\":10.5,\"availability\":true}")
                .addHeader("Content-Type", "application/json"));

        Product expected = Product.builder()
                .id("1")
                .name("Product 1")
                .price(10.5)
                .availability(true)
                .build();

        StepVerifier.create(adapter.fetchProductDetail("1"))
                .expectNext(expected)
                .verifyComplete();

        RecordedRequest recordedRequest = mockWebServer.takeRequest(1, TimeUnit.SECONDS);
        assertThat(recordedRequest).isNotNull();
        assertThat(recordedRequest.getPath()).isEqualTo("/product/1");
    }

    @Test
    void fetchProductDetail_shouldReturnEmptyMono_whenNotFound() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));

        StepVerifier.create(adapter.fetchProductDetail("unknown"))
                .verifyComplete();
    }

    @Test
    void fetchProductDetail_shouldPropagateError_whenServerError() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        StepVerifier.create(adapter.fetchProductDetail("1"))
                .expectError()
                .verify();
    }
}
