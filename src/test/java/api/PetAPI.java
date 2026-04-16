package api;

import io.restassured.response.Response;
import static io.restassured.RestAssured.*;
import io.restassured.RestAssured;
import utils.ConfigReader;

public class PetAPI {

    static {
        RestAssured.baseURI = ConfigReader.getBaseUrl();
    }

    public static Response createPet(long id, String name, String status) {

        String body = "{\n" +
                "  \"id\": " + id + ",\n" +
                "  \"name\": \"" + name + "\",\n" +
                "  \"status\": \"" + status + "\"\n" +
                "}";

        return given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/pet");
    }

    public static Response getPet(long petId) {
        return given()
                .when()
                .get("/pet/" + petId);
    }

    public static Response updatePet(long petId, String status) {

        String body = "{\n" +
                "  \"id\": " + petId + ",\n" +
                "  \"name\": \"updatedName\",\n" +
                "  \"status\": \"" + status + "\"\n" +
                "}";

        return given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .put("/pet");
    }

    public static Response deletePet(long petId) {
        return given()
                .when()
                .delete("/pet/" + petId);
    }
    public static Response getInventory() {
        return given()
                .when()
                .get("/store/inventory");
    }

    public static Response getPetsByStatus(String status) {
        return given()
                .queryParam("status", status)
                .when()
                .get("/pet/findByStatus");
    }
    public static Response createUserInvalid() {

        String body = "{\n" +
                "  \"id\": 101,\n" +
                "  \"username\": \"testuser\",\n" +
                "  \"email\": \"invalid_email\"\n" +
                "}";

        return given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/user");
    }

    public static Response getInvalidUser() {
        return given()
                .when()
                .get("/user/nonExistentUser123");
    }

    public static Response loginInvalid() {
        return given()
                .queryParam("username", "wrong")
                .queryParam("password", "wrong")
                .when()
                .get("/user/login");
    }
//testcase 4
    public static Response createPetWithCategory(long id, String category, String status) {

        String body = "{\n" +
                "  \"id\": " + id + ",\n" +
                "  \"category\": {\"id\": 1, \"name\": \"" + category + "\"},\n" +
                "  \"name\": \"Bulldog\",\n" +
                "  \"status\": \"" + status + "\"\n" +
                "}";

        return given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/pet");
    }
}