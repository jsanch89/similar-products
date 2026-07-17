package com.julian.product_backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
@TestPropertySource(properties = "similar.products.api.base-url=http://localhost:3001")
class ProductBackendApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void main_startsWithoutException() {
		assertThatCode(() -> ProductBackendApplication.main(new String[]{}))
				.doesNotThrowAnyException();
	}
}
