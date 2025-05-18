package Tests;

import Controllers.BasicResponse;
import Controllers.UserController;
import Models.User;
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
        userController.deleteUserAndWait(DEFAULT_USER.getUsername());
        userController.deleteUserAndWait(INVALID_USER.getUsername());
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

    @DisplayName("create user and compare it with response body")
    @Tags({@Tag("smoke"), @Tag("API")})
    @Test
    void createAndCheckUserTest(){
        User expectedUser = new User
                (792345098L,
                "Another user with new unique name to create",
                "Travis",
                "Barker",
                "blinkdrums@gmail.com",
                "loveplayingpunk",
                "69871743509",
                0);

        BasicResponse afterCreationResponse = userController.createUser(expectedUser).as(BasicResponse.class);

        assertThat(afterCreationResponse.getCode()).isEqualTo(200);

        Response createdUserResponse = userController.getUserByName(expectedUser.getUsername());

        assertThat(createdUserResponse.statusCode()).isEqualTo(200);

        User createdUser = createdUserResponse.as(User.class);

        assertThat(createdUser).usingRecursiveComparison().ignoringFields("id").isEqualTo(expectedUser);
    }

    @DisplayName("create user and delete it")
    @Tags({@Tag("smoke"), @Tag("API")})
    @Test
    void createAndDeleteUserTest(){
        User expectedUser = new User
                (603714591L,
                        "absolutely brand new user to create for petstore",
                        "Ralph",
                        "Feinnes",
                        "avadakedavra@gmail.com",
                        "nagaina",
                        "67143598",
                        0);

        BasicResponse createdUserResponse = userController.createUser(expectedUser).as(BasicResponse.class);

        assertThat(createdUserResponse.getCode()).isEqualTo(200);

        Response getCreatedUserResponse = userController.getUserByName(expectedUser.getUsername());

        assertThat(getCreatedUserResponse.statusCode()).isEqualTo(200);

        User createdUser = getCreatedUserResponse.as(User.class);

        assertThat(createdUser).usingRecursiveComparison().ignoringFields("id").isEqualTo(expectedUser);

        Response deleteUserResponse = userController.deleteUserAndWait(createdUser.getUsername());

        assertThat(deleteUserResponse.statusCode()).isEqualTo(200);

        BasicResponse getDeletedUserResponse = userController.waitUntilUserIsDeleted(expectedUser.getUsername()).as(BasicResponse.class);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(getDeletedUserResponse.getCode()).isEqualTo(1);
        softly.assertThat(getDeletedUserResponse.getType()).isEqualTo("error");
        softly.assertThat(getDeletedUserResponse.getMessage()).isEqualTo("User not found");
        softly.assertAll();
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

        userController.deleteUserAndWait(userToCreate.getUsername());

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

        userController.deleteUserAndWait(userToUpdate.getUsername());

        Response updatedUserResponse = userController.updateUser(userToUpdate);

        assertThat(updatedUserResponse.statusCode()).isEqualTo(200);

        Response getUpdatedUserResponse = userController.getUserByName(userToUpdate.getUsername());

        assertThat(getUpdatedUserResponse.statusCode()).isEqualTo(200);

        User updatedUser = getUpdatedUserResponse.as(User.class);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(updatedUser.getFirstName()).isEqualTo("Scarecrow");
        softly.assertThat(updatedUser.getLastName()).isEqualTo("None");
        softly.assertAll();

        userController.deleteUserAndWait(updatedUser.getUsername());
    }
}
