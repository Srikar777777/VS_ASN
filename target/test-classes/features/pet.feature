Feature: Pet Lifecycle API Testing

  Scenario Outline: Create, Get, Update and Delete a Pet

    Given I create a new pet with name "<name>" and status "<initialStatus>"
    When I get the pet by ID
    Then the pet name should be "<name>"

    When I update the pet status to "<updatedStatus>"
    Then the pet status should be "<updatedStatus>"

    When I delete the pet
    Then the response status should be 200

    Examples:
      | name   | initialStatus | updatedStatus |
      | Tommy  | available     | sold          |
      | Bruno  | available     | sold          |
      | Kitty  | pending       | sold          |

  Scenario: Inventory Analysis

    When I get the store inventory
    Then I store available pets count from inventory

    When I get pets by status "available"
    Then I validate pets list and consistency

  Scenario: Validate invalid user operations

    When I create a user with invalid email
    Then the response should indicate invalid user creation

    When I get a non existent user
    Then the user API response status should be 404

    When I login with invalid credentials
    Then login should fail logically

  Scenario: Cross Endpoint Data Consistency

    Given I create a pet with category "HighValue-Bulldog" and status "available"
    When I update this pet status to "sold"
    And I fetch all sold pets
    Then my created pet should be present in sold list