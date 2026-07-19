package com.julian.product_backend.infrastructure.adapter.in.rest;

import com.julian.product_backend.domain.model.Product;
import com.julian.product_backend.domain.port.in.ProductUseCase;
import com.julian.product_backend.infrastructure.adapter.in.rest.dto.ProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Operations related to product similarity")
public class ProductController {

    private final ProductUseCase productUseCase;

    @Operation(summary = "Get similar products", description = "Returns a list of products similar to the given product ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Similar products found",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductResponse.class)))),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content)
    })
    @GetMapping("/{productId}/similar")
    public ResponseEntity<List<ProductResponse>> getSimilarProducts(
            @Parameter(description = "ID of the product to find similarities for", required = true)
            @PathVariable String productId) {
        log.info("GET /product/{}/similar", productId);
        List<Product> products = productUseCase.similarProductsByIds(productId);
        List<ProductResponse> response = products.stream()
                .map(ProductResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(response);
    }
}
