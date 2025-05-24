package Controllers;

import Models.User;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.qameta.allure.restassured.AllureRestAssured;

import java.util.Objects;

import static Constants.Constants.*;
import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

public class UserController {

    private final RequestSpecification requestSpecification;

    public UserController(){
        RestAssured.defaultParser = Parser.JSON;
        requestSpecification = new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setBasePath("v2/")
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter( new AllureRestAssured())
                .build();
        }

    @Step("create user")
    public Response createUser(User user){
        return given(requestSpecification)
                .body(user)
                .post(USER_ENDPOINT)
                .andReturn();
    }

    @Step("update user")
    public Response updateUser(User user, String username){
        return given(requestSpecification)
                .body(user)
                .put(USER_ENDPOINT + "/" + username)
                .andReturn();
    }

    @Step("get user and wait until he appears")
    public Response getUserByName(String username) {
        Response response = await()
                .atMost(15, SECONDS)
                .pollInterval(500, MILLISECONDS)
                .until(() -> {
                    Response resp = given(requestSpecification)
                            .get(USER_ENDPOINT + "/" + username)
                            .andReturn();
                    return resp.statusCode() == 200 ? resp : null;
                }, Objects::nonNull);

        if (response == null) {
            throw new RuntimeException("User '" + username + "' did not become available within timeout");
        }

        return response;
    }

    @Step("delete user")
    public Response clearUser(String username){
        return given(this.requestSpecification)
                .when()
                .delete(USER_ENDPOINT + "/" + username)
                .andReturn();
    }

    @Step("delete user and wait until he disappears")
    public Response deleteUser(String username) {
        Response deleteResponse = given(requestSpecification)
                .delete(USER_ENDPOINT + "/" + username)
                .andReturn();

        await()
                .atMost(15, SECONDS)
                .pollInterval(500, MILLISECONDS)
                .until(() -> {
                    Response response = given(requestSpecification)
                            .get(USER_ENDPOINT + "/" + username)
                            .andReturn();
                    return response.statusCode() == 404;
                });
        return deleteResponse;
    }

    @Step("wait until user will be deleted")
    public Response waitUntilUserIsDeleted(String username) {
        await()
                .atMost(15, SECONDS)
                .pollInterval(500, MILLISECONDS)
                .until(() -> {
                    Response resp = given(requestSpecification)
                            .get(USER_ENDPOINT + "/" + username)
                            .andReturn();
                    return resp.statusCode() == 404;
                });

        return given(requestSpecification)
                .get(USER_ENDPOINT + "/" + username)
                .andReturn();
    }
}
