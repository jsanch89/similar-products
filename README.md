# Similar Products Backend

Spring Boot service that exposes an endpoint to retrieve similar products for a given product ID, delegating to an external products API.

## Requirements

- Java 21
- Maven
- Docker & Docker Compose (for containerized run)

## Running the application

### Locally

The application expects an external API running at `http://localhost:3001` by default (configurable via `application.yml`).

```bash
cd product-backend
./mvnw spring-boot:run
```

The server starts on port **5000**.

### With Docker Compose

```bash
docker-compose up --build
```

This builds the image and starts the service on port **5000**. The external API is expected at `http://host.docker.internal:3001`.

To override the external API URL:

```bash
SIMILAR_PRODUCTS_API_BASE_URL=http://your-api-host:3001 docker-compose up --build
```

## API

### GET /product/{productId}/similar

Returns a list of products similar to the given product.

**Response 200**
```json
[
  { "id": "2", "name": "Product B", "price": 29.99, "availability": true },
  { "id": "3", "name": "Product C", "price": 49.99, "availability": false }
]
```

**Response 404** — product not found
**Response 500** — unexpected error

### Swagger UI

Available at `http://localhost:5000/swagger-ui.html` when the app is running.

## Running the tests

### Unit tests

```bash
cd product-backend
./mvnw test
```

### E2E tests (Cucumber)

The E2E tests use WireMock to stub the external API — no real external service is needed.

```bash
cd product-backend
./mvnw verify
```

Cucumber feature file: `src/test/resources/features/similar_products.feature`

Scenarios covered:
- Successfully retrieve similar products for a valid product
- Return 404 when the product does not exist
- Return empty list when the product has no similar products

## Architecture

The project follows **Hexagonal Architecture** (Ports & Adapters):

```
domain/
  model/          # Product entity
  port/
    in/           # ProductUseCase (input port)
    out/          # ProductRepositoryPort (output port)
  service/        # ProductService (domain logic)
  exception/      # ProductNotFoundException

infrastructure/
  adapter/
    in/rest/      # ProductController, GlobalExceptionHandler
    out/api/      # SimilarProductsApiAdapter (calls external API via RestClient)
  config/         # BeanConfiguration (wires RestClient and ProductService)
```

Key design decisions:

- **RestClient** (Spring 6) is used instead of the deprecated `RestTemplate` for HTTP calls to the external API.
- **GlobalExceptionHandler** translates domain exceptions (`ProductNotFoundException`) to HTTP 404 responses with a structured error body.
- If fetching a specific similar product detail fails with a server error, that product is silently skipped so partial results are still returned.
- The external API base URL is externalised via the `similar.products.api.base-url` property, making it easy to override per environment.
