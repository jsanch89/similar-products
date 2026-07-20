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

  Scenario Outline: Distinguish between a product with no similar products and one that does not exist
    Given <precondition>
    When I request similar products for product <productId>
    Then the response status should be <expectedStatus>

    Examples:
      | precondition                                     | productId | expectedStatus |
      | product "10" has no similar products             | "10"      | 200            |
      | product "999" does not exist in the external API | "999"     | 404            |

  Scenario: Return 404 for a product ID with invalid format
    Given product "abc" does not exist in the external API
    When I request similar products for product "abc"
    Then the response status should be 404

  Scenario: Return empty list when the similarids external service responds with 5xx
    Given the similarids service returns a 5xx error for product "5"
    When I request similar products for product "5"
    Then the response status should be 200
    And the response should contain 0 products

  Scenario: Return empty list when the external provider times out
    Given the external provider times out for product "6"
    When I request similar products for product "6"
    Then the response status should be 200
    And the response should contain 0 products
