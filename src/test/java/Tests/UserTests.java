package Tests;

import Controllers.BasicResponse;
import Controllers.UserController;
import Models.User;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.*;
import static TestData.UserTestData.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UserTests {

    UserController userController = new UserController();

    @BeforeAll
    static void setup() {
        RestAssured.defaultParser = Parser.JSON;
    }

    @BeforeEach
    @AfterEach
    void clearTestData(){
        userController.deleteUser(DEFAULT_USER.getUsername());
        userController.deleteUser(INVALID_USER.getUsername());
    }

    @DisplayName("create new user")
    @Tags({@Tag("smoke"), @Tag("API")})
    @Test
    void createUserTest(){
        int expectedStatusCode = 200;
        String expectedResponseType = "unknown";

        Response actualResponse = userController.createUser(DEFAULT_USER);
        BasicResponse createdUserResponse  = actualResponse.as(BasicResponse.class);
        String actualMessage = createdUserResponse.getMessage();

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(actualResponse.getStatusCode()).as("Values must be equal").isEqualTo(expectedStatusCode);
        softly.assertThat(createdUserResponse.getCode()).as("Values must be equal").isEqualTo(expectedStatusCode);
        softly.assertThat(createdUserResponse.getType()).as("Values must be equal").isEqualTo(expectedResponseType);
        softly.assertThat(actualMessage.matches("\\d+")).as("Message must contain only digits (unique number)").isTrue();
        softly.assertAll();
        userController.deleteUser(DEFAULT_USER.getUsername());
    }

    @DisplayName("create user without any fields ")
    @Tags({@Tag("smoke"), @Tag("API")})
    @Test
    void createInvalidUserTest(){
        int expectedStatusCode = 200;
        String expectedResponseType = "unknown";

        Response actualResponse = userController.createUser(INVALID_USER);
        BasicResponse createdUserResponse  = actualResponse.as(BasicResponse.class);
        String actualMessage = createdUserResponse.getMessage();

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(actualResponse.getStatusCode()).as("Values must be equal").isEqualTo(expectedStatusCode);
        softly.assertThat(createdUserResponse.getCode()).as("Values must be equal").isEqualTo(expectedStatusCode);
        softly.assertThat(createdUserResponse.getType()).as("Values must be equal").isEqualTo(expectedResponseType);
        softly.assertThat(actualMessage.matches("\\d+")).as("Message must contain only digits (unique number)").isTrue();
        softly.assertAll();
    }

    @DisplayName("create user and compare it with template")
    @Tags({@Tag("smoke"), @Tag("API")})
    @Test
    void createAndCheckUserTest(){
        User expectedUser = new User
                (33397108721110L,
                "Just created expected user",
                "Steven",
                "Rogers",
                "StevenRogers1899@gmail.com",
                "DestroyNaziRegime",
                "098123756",
                0);

        BasicResponse afterCreationResponse = userController.createUser(expectedUser).as(BasicResponse.class);

        assertThat(afterCreationResponse.getCode()).isEqualTo(200);

        User createdUser = userController.getUserByName(expectedUser.getUsername()).as(User.class);

        assertThat(createdUser).usingRecursiveComparison().ignoringFields("id").isEqualTo(expectedUser);
    }

    @DisplayName("create user and delete it")
    @Tags({@Tag("smoke"), @Tag("API")})
    @Test
    void createAndDeleteUserTest(){
        BasicResponse createdUserResponse = userController.createUser(DEFAULT_USER).as(BasicResponse.class);

        assertThat(createdUserResponse.getCode()).isEqualTo(200);

        Response getCreatedUserResponse = userController.getUserByName(DEFAULT_USER.getUsername());

        assertThat(getCreatedUserResponse.statusCode()).isEqualTo(200);

        User createdUser = getCreatedUserResponse.as(User.class);

        assertThat(createdUser).usingRecursiveComparison().ignoringFields("id").isEqualTo(DEFAULT_USER);

        BasicResponse deleteUserResponse = userController.deleteUser(createdUser.getUsername()).as(BasicResponse.class);

        assertThat(deleteUserResponse.getCode()).isEqualTo(200);

        Response getDeletedUserResponse = userController.getUserByName(DEFAULT_USER.getUsername());

        assertThat(getDeletedUserResponse.statusCode()).isEqualTo(404);
    }

    @DisplayName("create user and update its value")
    @Tags({@Tag("smoke"), @Tag("API")})
    @Test
    void createAndUpdateUserTest(){
        User userToCreate = new User(
                1928374506L,
                "uniqueUsername",
                "Jonathan",
                "Crane",
                "DrCrane@example.com",
                "trytoscareme!",
                "+79068713245093",
                0);

        userController.deleteUser(userToCreate.getUsername());

        BasicResponse createdUserResponse = userController.createUser(userToCreate).as(BasicResponse.class);

        assertThat(createdUserResponse.getCode()).isEqualTo(200);

        Response getCreatedUserResponse = userController.getUserByName(userToCreate.getUsername());

        assertThat(getCreatedUserResponse.statusCode()).isEqualTo(200);

        User createdUser = getCreatedUserResponse.as(User.class);

        assertThat(createdUser).usingRecursiveComparison().ignoringFields("id").isEqualTo(userToCreate);

        User userToUpdate = new User
                (1928374506L,
                userToCreate.getUsername(),
                "Scarecrow",
                "None",
                "DrCrane@example.com",
                "trytoscareme!",
                "+79068713245093",
                0);

        userController.deleteUser(userToUpdate.getUsername());

        Response updatedUserResponse = userController.updateUser(userToUpdate);

        assertThat(updatedUserResponse.statusCode()).isEqualTo(200);

        Response getUpdatedUserResponse = userController.getUserByName(userToUpdate.getUsername());

        assertThat(getUpdatedUserResponse.statusCode()).isEqualTo(200);

        User updatedUser = getUpdatedUserResponse.as(User.class);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(updatedUser.getFirstName()).isEqualTo("Scarecrow");
        softly.assertThat(updatedUser.getLastName()).isEqualTo("None");
        softly.assertAll();

        userController.deleteUser(updatedUser.getUsername());
    }
}
