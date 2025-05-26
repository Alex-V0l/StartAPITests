package Tests;

import Models.BasicPetStoreResponse;
import Controllers.UserController;
import Models.User;
import io.qameta.allure.Flaky;
import io.restassured.response.Response;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.*;
import static TestData.UserTestData.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UserTests {

    UserController userController = new UserController();

    @BeforeEach
    @AfterEach
    void clearTestData(){
        userController.clearUser(DEFAULT_USER.getUsername());
        userController.clearUser(INVALID_USER.getUsername());
    }

    @DisplayName("create new user")
    @Tags({@Tag("smoke"), @Tag("API")})
    @Test
    void createUserTest(){
        int expectedStatusCode = 200;
        String expectedResponseType = "unknown";

        Response actualResponse = userController.createUser(DEFAULT_USER);
        BasicPetStoreResponse createdUserResponse  = actualResponse.as(BasicPetStoreResponse.class);
        String actualMessage = createdUserResponse.getMessage();

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(actualResponse.getStatusCode()).as("Values must be equal").isEqualTo(expectedStatusCode);
        softly.assertThat(createdUserResponse.getCode()).as("Values must be equal").isEqualTo(expectedStatusCode);
        softly.assertThat(createdUserResponse.getType()).as("Values must be equal").isEqualTo(expectedResponseType);
        softly.assertThat(actualMessage.matches("\\d+")).as("Message must contain only digits (unique number)").isTrue();
        softly.assertAll();
    }

    @DisplayName("create user without any fields ")
    @Tags({@Tag("smoke"), @Tag("API")})
    @Test
    void createInvalidUserTest(){
        int expectedStatusCode = 200;
        String expectedResponseType = "unknown";

        Response actualResponse = userController.createUser(INVALID_USER);
        BasicPetStoreResponse createdUserResponse  = actualResponse.as(BasicPetStoreResponse.class);
        String actualMessage = createdUserResponse.getMessage();

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(actualResponse.getStatusCode()).as("Values must be equal").isEqualTo(expectedStatusCode);
        softly.assertThat(createdUserResponse.getCode()).as("Values must be equal").isEqualTo(expectedStatusCode);
        softly.assertThat(createdUserResponse.getType()).as("Values must be equal").isEqualTo(expectedResponseType);
        softly.assertThat(actualMessage.matches("\\d+")).as("Message must contain only digits (unique number)").isTrue();
        softly.assertAll();
    }

    @DisplayName("create user and compare it with response body")
    @Tags({@Tag("smoke"), @Tag("API")})
    @Test
    void createAndCheckUserTest(){
        String expectedMessage = String.valueOf(DEFAULT_USER.getId());

        BasicPetStoreResponse afterCreationResponse = userController.createUser(DEFAULT_USER).as(BasicPetStoreResponse.class);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(afterCreationResponse.getCode()).isEqualTo(200);
        softly.assertThat(afterCreationResponse.getMessage()).isEqualTo(expectedMessage);
        softly.assertThat(afterCreationResponse.getType()).isEqualTo("unknown");
        softly.assertAll();

        Response createdUserResponse = userController.getUserByName(DEFAULT_USER.getUsername());

        assertThat(createdUserResponse.statusCode()).isEqualTo(200);

        User createdUser = createdUserResponse.as(User.class);

        assertThat(createdUser).usingRecursiveComparison().ignoringFields("id").isEqualTo(DEFAULT_USER);
    }

    @DisplayName("create user and delete it")
    @Tags({@Tag("smoke"), @Tag("API"), @Tag("only with awaitility")})
    @Flaky
    @Test
    void createAndDeleteUserTest(){
        String expectedMessage = String.valueOf(DEFAULT_USER.getId());
        BasicPetStoreResponse createdUserResponse = userController.createUser(DEFAULT_USER).as(BasicPetStoreResponse.class);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(createdUserResponse.getCode()).isEqualTo(200);
        softly.assertThat(createdUserResponse.getMessage()).isEqualTo(expectedMessage);
        softly.assertThat(createdUserResponse.getType()).isEqualTo("unknown");
        softly.assertAll();

        Response getCreatedUserResponse = userController.getUserByName(DEFAULT_USER.getUsername());

        assertThat(getCreatedUserResponse.statusCode()).isEqualTo(200);

        User createdUser = getCreatedUserResponse.as(User.class);

        assertThat(createdUser).usingRecursiveComparison().ignoringFields("id").isEqualTo(DEFAULT_USER);

        Response deleteUserResponse = userController.deleteUser(createdUser.getUsername());

        assertThat(deleteUserResponse.statusCode()).isEqualTo(200);

        BasicPetStoreResponse getDeletedUserResponse = userController.waitUntilUserIsDeleted(DEFAULT_USER.getUsername())
                .as(BasicPetStoreResponse.class);

        softly.assertThat(getDeletedUserResponse.getCode()).isEqualTo(1);
        softly.assertThat(getDeletedUserResponse.getType()).isEqualTo("error");
        softly.assertThat(getDeletedUserResponse.getMessage()).isEqualTo("User not found");
        softly.assertAll();
    }

    @DisplayName("create user and update its value")
    @Tags({@Tag("smoke"), @Tag("API"), @Tag("with awaitility")})
    @Flaky
    @Test
    void createAndUpdateUserTest(){
        String expectedMessage = String.valueOf(DEFAULT_USER.getId());
        User userToUpdate = new User
                (7609378451L,
                        "updateduserusername",
                        "Neo",
                        "The chosen",
                        "neo2025@example.com",
                        "redpillbluepill87634",
                        "+5781438",
                        0);

        BasicPetStoreResponse createdUserResponse = userController.createUser(DEFAULT_USER).as(BasicPetStoreResponse.class);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(createdUserResponse.getCode()).isEqualTo(200);
        softly.assertThat(createdUserResponse.getMessage()).isEqualTo(expectedMessage);
        softly.assertThat(createdUserResponse.getType()).isEqualTo("unknown");
        softly.assertAll();

        Response getCreatedUserResponse = userController.getUserByName(DEFAULT_USER.getUsername());

        assertThat(getCreatedUserResponse.statusCode()).isEqualTo(200);

        User createdUser = getCreatedUserResponse.as(User.class);

        assertThat(createdUser).usingRecursiveComparison().ignoringFields("id").isEqualTo(DEFAULT_USER);

        Response updatedUserResponse = userController.updateUser(userToUpdate, DEFAULT_USER.getUsername());

        assertThat(updatedUserResponse.statusCode()).isEqualTo(200);

        Response getUpdatedUserResponse = userController.getUserByName(userToUpdate.getUsername());

        assertThat(getUpdatedUserResponse.statusCode()).isEqualTo(200);

        User updatedUser = getUpdatedUserResponse.as(User.class);

        softly.assertThat(updatedUser.getFirstName()).isEqualTo("Neo");
        softly.assertThat(updatedUser.getLastName()).isEqualTo("The chosen");
        softly.assertThat(updatedUser.getPassword()).isEqualTo("redpillbluepill87634");
        softly.assertAll();
    }
}
