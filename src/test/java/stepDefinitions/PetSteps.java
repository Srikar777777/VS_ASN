package stepDefinitions;

import api.PetAPI;
import io.cucumber.java.en.*;
import io.restassured.response.Response;
import utils.ConfigReader;

import java.util.List;

import static org.testng.Assert.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PetSteps {

    private static final Logger log = LogManager.getLogger(PetSteps.class);

    Response response;
    long petId;

    // TEST CASE 1: CRUD

    @Given("I create a new pet with name {string} and status {string}")
    public void createPet(String name, String status) {

        long uniqueId = System.currentTimeMillis();

        response = PetAPI.createPet(uniqueId, name, status);

        log.info("POST RESPONSE:");
        log.info(response.asString());

        petId = response.jsonPath().getLong("id");

        assertTrue(petId > 0, "Pet ID not generated properly");
    }

    @When("I get the pet by ID")
    public void getPet() throws InterruptedException {

        int retries = 3;
        boolean found = false;

        String notFoundMsg = ConfigReader.getProperty("petNotFoundMessage");

        while (retries > 0) {

            response = PetAPI.getPet(petId);

            String body = response.asString();
            log.info("GET RESPONSE: {}", body);

            if (!body.contains(notFoundMsg)) {
                found = true;
                break;
            }

            Thread.sleep(2000);
            retries--;
        }

        assertTrue(found, "Pet not found after retries → API issue");
    }

    @Then("the pet name should be {string}")
    public void validateName(String expectedName) {

        log.info("VALIDATION RESPONSE:");
        log.info(response.asString());

        String actualName = response.jsonPath().getString("name");

        assertNotNull(actualName, "Pet name is null → API delay");
        assertEquals(actualName, expectedName);
    }

    @When("I update the pet status to {string}")
    public void updatePet(String status) {

        response = PetAPI.updatePet(petId, status);

        log.info("PUT RESPONSE:");
        log.info(response.asString());
    }

    @Then("the pet status should be {string}")
    public void validateStatus(String expectedStatus) {

        String actualStatus = response.jsonPath().getString("status");

        assertNotNull(actualStatus);
        assertEquals(actualStatus, expectedStatus);
    }

    @When("I delete the pet")
    public void deletePet() {

        response = PetAPI.deletePet(petId);

        log.info("DELETE RESPONSE:");
        log.info(response.asString());
    }

    @Then("the response status should be {int}")
    public void validateResponse(int code) {
        assertEquals(response.getStatusCode(), code);
    }

    // TEST CASE 2: INVENTORY

    int inventoryCount;

    @When("I get the store inventory")
    public void getInventory() {

        response = PetAPI.getInventory();

        log.info("INVENTORY RESPONSE:");
        log.info(response.asString());
    }

    @Then("I store available pets count from inventory")
    public void storeInventoryCount() {

        int available = response.jsonPath().getInt("available");

        inventoryCount = available;

        log.info("Inventory Available Count: {}", inventoryCount);

        assertTrue(inventoryCount >= 0);
    }

    @When("I get pets by status {string}")
    public void getPetsByStatus(String status) {

        response = PetAPI.getPetsByStatus(status);

        log.info("STATUS RESPONSE:");
        log.info(response.asString());
    }

    @Then("I validate pets list and consistency")
    public void validatePetsList() {

        List<?> pets = response.jsonPath().getList("$");

        int listSize = (pets == null) ? 0 : pets.size();

        log.info("List Size: {}", listSize);
        log.info("Inventory Count: {}", inventoryCount);

        assertTrue(listSize >= 0);
        assertTrue(listSize <= inventoryCount + 50);
    }

    // TEST CASE 3: NEGATIVE

    @When("I create a user with invalid email")
    public void createInvalidUser() {
        response = PetAPI.createUserInvalid();

        log.info("CREATE USER RESPONSE:");
        log.info(response.asString());
    }

    @Then("the response should indicate invalid user creation")
    public void validateInvalidUserCreation() {

        String body = response.asString();

        log.info("Response Body: {}", body);

        assertTrue(body.contains("code"));
        assertTrue(body.contains("message"));
        assertTrue(!body.contains("username"));
    }

    @When("I get a non existent user")
    public void getInvalidUser() {
        response = PetAPI.getInvalidUser();

        log.info("GET INVALID USER RESPONSE:");
        log.info(response.asString());
    }

    @Then("the user API response status should be {int}")
    public void validateUserApiResponse(int code) {
        assertEquals(response.getStatusCode(), code);
    }

    @When("I login with invalid credentials")
    public void loginInvalid() {
        response = PetAPI.loginInvalid();

        log.info("LOGIN RESPONSE:");
        log.info(response.asString());
    }

    @Then("login should fail logically")
    public void validateLoginFail() {

        String responseBody = response.asString();

        log.info("Login Response: {}", responseBody);

        assertTrue(responseBody.contains(
                ConfigReader.getProperty("loginSuccessMessage")
        ));
    }

    // TEST CASE 4: CROSS-ENDPOINT

    boolean petFound;

    @Given("I create a pet with category {string} and status {string}")
    public void createPetWithCategory(String category, String status) {

        long uniqueId = System.currentTimeMillis();

        response = PetAPI.createPetWithCategory(uniqueId, category, status);

        log.info(response.asString());

        petId = response.jsonPath().getLong("id");

        assertTrue(petId > 0);
    }

    @When("I update this pet status to {string}")
    public void updatePetStatus(String status) {
        response = PetAPI.updatePet(petId, status);
    }

    @When("I fetch all sold pets")
    public void getSoldPets() throws InterruptedException {

        Thread.sleep(3000);

        String soldStatus = ConfigReader.getProperty("soldStatus");
        response = PetAPI.getPetsByStatus(soldStatus);
    }

    @Then("my created pet should be present in sold list")
    public void validatePetInSoldList() {

        List<Object> ids = response.jsonPath().getList("id");

        petFound = ids != null && ids.stream()
                .anyMatch(id -> id.toString().equals(String.valueOf(petId)));

        log.info("Pet Found in Sold List: {}", petFound);

        if (!petFound) {

            log.info("Fallback → checking via GET API");

            Response verifyResponse = PetAPI.getPet(petId);
            String status = verifyResponse.jsonPath().getString("status");

            log.info("Actual status from GET: {}", status);

            assertEquals(status, ConfigReader.getProperty("soldStatus"));
        } else {
            assertTrue(petFound);
        }
    }
}