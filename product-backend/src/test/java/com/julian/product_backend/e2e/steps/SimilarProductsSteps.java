package com.julian.product_backend.e2e.steps;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.julian.product_backend.e2e.CucumberSpringConfiguration;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

public class SimilarProductsSteps {

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private ObjectMapper objectMapper;

    private ResponseEntity<String> response;

    @Before
    public void resetWireMock() {
        // Prevent RestTemplate from throwing on 4xx/5xx so steps can assert the status code
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override public boolean hasError(ClientHttpResponse r) { return false; }
        });
        CucumberSpringConfiguration.WIRE_MOCK_SERVER.resetAll();
    }

    @Given("product {string} has similar product IDs {string} and {string}")
    public void productHasSimilarProductIds(String productId, String id1, String id2) {
        CucumberSpringConfiguration.WIRE_MOCK_SERVER.stubFor(
                get(urlEqualTo("/product/" + productId + "/similarids"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody("[\"" + id1 + "\", \"" + id2 + "\"]"))
        );
    }

    @And("product {string} has name {string}, price {double} and is available")
    public void productHasDetailsAndIsAvailable(String productId, String name, double price) {
        stubProductDetail(productId, name, price, true);
    }

    @And("product {string} has name {string}, price {double} and is not available")
    public void productHasDetailsAndIsNotAvailable(String productId, String name, double price) {
        stubProductDetail(productId, name, price, false);
    }

    @Given("product {string} does not exist in the external API")
    public void productDoesNotExist(String productId) {
        CucumberSpringConfiguration.WIRE_MOCK_SERVER.stubFor(
                get(urlEqualTo("/product/" + productId + "/similarids"))
                        .willReturn(aResponse().withStatus(404))
        );
    }

    @Given("product {string} has no similar products")
    public void productHasNoSimilarProducts(String productId) {
        CucumberSpringConfiguration.WIRE_MOCK_SERVER.stubFor(
                get(urlEqualTo("/product/" + productId + "/similarids"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody("[]"))
        );
    }

    @When("I request similar products for product {string}")
    public void iRequestSimilarProductsForProduct(String productId) {
        response = restTemplate.getForEntity(
                "http://localhost:" + port + "/product/" + productId + "/similar", String.class);
    }

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int expectedStatus) {
        assertThat(response.getStatusCode().value()).isEqualTo(expectedStatus);
    }

    @And("the response should contain {int} products")
    public void theResponseShouldContainProducts(int expectedCount) throws Exception {
        List<Map<String, Object>> products = objectMapper.readValue(
                response.getBody(), new TypeReference<>() {});
        assertThat(products).hasSize(expectedCount);
    }

    @And("one product should have id {string} and name {string}")
    public void oneProductShouldHaveIdAndName(String expectedId, String expectedName) throws Exception {
        List<Map<String, Object>> products = objectMapper.readValue(
                response.getBody(), new TypeReference<>() {});
        assertThat(products).anySatisfy(product -> {
            assertThat(product.get("id")).isEqualTo(expectedId);
            assertThat(product.get("name")).isEqualTo(expectedName);
        });
    }

    private void stubProductDetail(String productId, String name, double price, boolean availability) {
        String body = """
                {"id": "%s", "name": "%s", "price": %s, "availability": %s}
                """.formatted(productId, name, price, availability);

        CucumberSpringConfiguration.WIRE_MOCK_SERVER.stubFor(
                get(urlEqualTo("/product/" + productId))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(body))
        );
    }
}
