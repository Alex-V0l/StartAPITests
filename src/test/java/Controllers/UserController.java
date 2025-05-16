package Controllers;

import Models.User;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static Constants.Constants.*;
import static io.restassured.RestAssured.given;

public class UserController {

    RequestSpecification requestSpecification;

    public static final String ACCEPT_USER_HEADER = "application/json";
    public static final String CONTENT_TYPE_USER_HEADER = "application/json";

    public UserController(){
        this.requestSpecification = given()
                .accept(ACCEPT_USER_HEADER)
                .contentType(CONTENT_TYPE_USER_HEADER)
                .baseUri(BASE_URL);
    }

    @Step("create user")
    public Response createUser(User user){
        return given(this.requestSpecification)
                .body(user)
                .when()
                .post(USER_ENDPOINT)
                .andReturn();
    }

    @Step("update user")
    public Response updateUser(User user){
        return given(this.requestSpecification)
                .body(user)
                .when()
                .put(USER_ENDPOINT + "/" + user.getUsername())
                .andReturn();
    }

    @Step("get user by username")
    public Response getUserByName(String username){
        return given(this.requestSpecification)
                .when()
                .get(USER_ENDPOINT + "/" + username)
                .andReturn();
    }

    @Step("delete user")
    public Response deleteUser(String username){
        return given(this.requestSpecification)
                .when()
                .delete(USER_ENDPOINT + "/" + username)
                .andReturn();
    }
}
