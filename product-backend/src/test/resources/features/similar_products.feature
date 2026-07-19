Feature: Similar Products API

  Scenario: Successfully retrieve similar products for a valid product
    Given product "1" has similar product IDs "2" and "3"
    And product "2" has name "Product B", price 29.99 and is available
    And product "3" has name "Product C", price 49.99 and is not available
    When I request similar products for product "1"
    Then the response status should be 200
    And the response should contain 2 products
    And one product should have id "2" and name "Product B"
    And one product should have id "3" and name "Product C"

  Scenario: Return 404 when product does not exist
    Given product "999" does not exist in the external API
    When I request similar products for product "999"
    Then the response status should be 404

  Scenario: Return empty list when product has no similar products
    Given product "10" has no similar products
    When I request similar products for product "10"
    Then the response status should be 200
    And the response should contain 0 products
