package com.julian.product_backend.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@CucumberContextConfiguration
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "similar.products.api.base-url=http://localhost:8089",
                "similar.products.api.timeout-ms=500",
                // No wait between retries so E2E tests run fast
                "resilience4j.retry.instances.externalApi.wait-duration=0ms",
                // Large window so the circuit never opens across scenarios
                "resilience4j.circuitbreaker.instances.externalApi.sliding-window-size=1000"
        }
)
public class CucumberSpringConfiguration {

    public static final WireMockServer WIRE_MOCK_SERVER =
            new WireMockServer(WireMockConfiguration.options().port(8089));

    static {
        WIRE_MOCK_SERVER.start();
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }
}
