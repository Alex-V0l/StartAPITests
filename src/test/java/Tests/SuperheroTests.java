package Tests;

import Controllers.FluentSuperheroController;
import Models.GenderForSuperhero;
import Models.Superhero;
import Models.SuperheroError;
import io.qameta.allure.Flaky;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;

import java.util.List;

import static Constants.Constants.BASE_SUPERHERO_URL;
import static TestData.SuperheroTestData.*;
import static io.restassured.RestAssured.given;

public class SuperheroTests {

    FluentSuperheroController fluentSuperheroController = new FluentSuperheroController();

    @BeforeEach
    @AfterEach
    void clearTestData(){
        RequestSpecification spec = fluentSuperheroController.getRequestSpecification();

        Superhero[] allHeroes = given(spec)
                .when().get(BASE_SUPERHERO_URL + "superheroes")
                .then().statusCode(200)
                .extract().as(Superhero[].class);

        for (Superhero hero : allHeroes) {
            given(spec).when().delete("superheroes/" + hero.getId()).then().statusCode(200);
        }
    }

    @DisplayName("create a hero and check status code")
    @Tags({@Tag("smoke"), @Tag("API")})
    @Test
    void createHero(){
        fluentSuperheroController.createHero(BASIC_HERO)
                .statusCodeIs(200)
                .getSuperheroFromResponse();
    }

    @DisplayName("create a hero, check status code and response body")
    @Tags({@Tag("smoke"), @Tag("API"), @Tag("Will fail"), @Tag("Reason: phone becomes null after creation")})
    @Test
    void createHeroAndCheckBody(){
        fluentSuperheroController.createHero(BASIC_HERO)
                .statusCodeIs(200)
                .compareWithHero(BASIC_HERO);
    }

    @DisplayName("create a hero without phone, check status code and response body")
    @Tags({@Tag("smoke"), @Tag("API")})
    @Test
    void createHeroWithoutPhoneAndCheckBody(){
        fluentSuperheroController.createHero(HERO_WITHOUT_PHONE)
                .statusCodeIs(200)
                .compareWithHero(HERO_WITHOUT_PHONE);
    }

    @DisplayName("try to create a hero with invalid path and check status code")
    @Tags({@Tag("smoke"), @Tag("API")})
    @Test
    void createHeroWithInvalidPathAndCheckCode(){
        fluentSuperheroController.createHeroWithInvalidPath(BASIC_HERO).statusCodeIs(404);
    }

    @DisplayName("try to create a hero, check status code and response body")
    @Tags({@Tag("smoke"), @Tag("API")})
    @Test
    void createHeroWithInvalidPathAndCheckCodeAndBody(){
        fluentSuperheroController.createHeroWithInvalidPath(BASIC_HERO)
                .statusCodeIs(404)
                .compareWithErrorBody(RESPONSE_WITH_NO_AVAILABLE_MESSAGE);
    }

    @DisplayName("try create a hero with invalid date, check status code and response body")
    @Tags({@Tag("smoke"), @Tag("API")})
    @Test
    void createHeroWithInvalidDateAndCheckBody(){
        String expectedStatusValue = "400";
        String expectedMessagePart = "JSON parse error";
        String expectedError = "Bad Request";

        fluentSuperheroController.createHero(HERO_WITH_INVALID_DATE)
                .statusCodeIs(400)
                .jsonValueCompare("status", expectedStatusValue)
                .jsonValueCompare("error", expectedError)
                .jsonValueIsNotNull("message")
                .getJsonValue("message").contains(expectedMessagePart);
    }

    @DisplayName("try create a hero without main skill, check status code and response body")
    @Tags({@Tag("smoke"), @Tag("API")})
    @Test
    void createHeroWithoutMainSkillAndCheckBody(){
        String expectedStatusValue = "500";
        String expectedMessagePart = "could not execute statement";
        String expectedError = "Internal Server Error";

        fluentSuperheroController.createHero(HERO_WITHOUT_SKILL)
                .statusCodeIs(500)
                .jsonValueCompare("status", expectedStatusValue)
                .jsonValueCompare("error", expectedError)
                .jsonValueIsNotNull("message")
                .getJsonValue("message").contains(expectedMessagePart);
    }

    @DisplayName("create a hero without phone, get hero and check status code")
    @Tags({@Tag("smoke"), @Tag("API"), @Tag("will fail"), @Tag("Reason: get sometimes finds hero with different id")})
    @Test
    void createAndGetHeroNoPhone() throws InterruptedException {
        Superhero createHeroResponse = fluentSuperheroController.createHero(HERO_WITHOUT_PHONE)
                .statusCodeIs(200)
                .getSuperheroFromResponse();

        Thread.sleep(5000);
        fluentSuperheroController.getHero(createHeroResponse.getId()).statusCodeIs(200);
    }

    @DisplayName("create a hero without phone, get hero, check status code and response body")
    @Tags({@Tag("smoke"), @Tag("API"), @Tag("unstable"), @Tag("Reason: sometimes get work with wrong id")})
    @Test
    void createGetHeroNoPhoneAndCheckCodeAndBody() throws InterruptedException {
        Superhero createHeroResponse = fluentSuperheroController.createHero(HERO_WITHOUT_PHONE)
                .statusCodeIs(200)
                .getSuperheroFromResponse();

        Thread.sleep(3000);
        fluentSuperheroController.getHero(createHeroResponse.getId())
                .statusCodeIs(200)
                .jsonValueCompare("birthDate", HERO_WITHOUT_PHONE.getBirthDate())
                .jsonValueCompare("city", HERO_WITHOUT_PHONE.getCity())
                .jsonValueCompare("fullName", HERO_WITHOUT_PHONE.getFullName())
                .jsonValueCompare("gender", String.valueOf(HERO_WITHOUT_PHONE.getGender()))
                .jsonValueCompare("id", String.valueOf(createHeroResponse.getId()))
                .jsonValueCompare("mainSkill", HERO_WITHOUT_PHONE.getMainSkill())
                .jsonValueIsNull("phone");
    }

    @DisplayName("create a hero, get hero, check status code and response body")
    @Tags({@Tag("smoke"), @Tag("API"), @Tag("will fail"), @Tag("Reason: phone becomes null after creation")})
    @Test
    void createGetHeroAndCheckCodeAndBody() throws InterruptedException {
        Superhero createHeroResponse = fluentSuperheroController.createHero(BASIC_HERO)
                .statusCodeIs(200)
                .getSuperheroFromResponse();

        Thread.sleep(3000);
        fluentSuperheroController.getHero(createHeroResponse.getId())
                .statusCodeIs(200)
                .jsonValueCompare("birthDate", BASIC_HERO.getBirthDate())
                .jsonValueCompare("city", BASIC_HERO.getCity())
                .jsonValueCompare("fullName", BASIC_HERO.getFullName())
                .jsonValueCompare("gender", String.valueOf(BASIC_HERO.getGender()))
                .jsonValueCompare("id", String.valueOf(createHeroResponse.getId()))
                .jsonValueCompare("mainSkill", BASIC_HERO.getMainSkill())
                .jsonValueCompare("phone", BASIC_HERO.getPhone());
    }

    @DisplayName("create a hero, get hero and check status code")
    @Tags({@Tag("smoke"), @Tag("API"), @Tag("will fail"), @Tag("Reason: get sometimes finds hero with different id")})
    @Test
    void createAndGetHero() throws InterruptedException {
        Superhero createHeroResponse = fluentSuperheroController.createHero(BASIC_HERO)
                .statusCodeIs(200)
                .getSuperheroFromResponse();

        Thread.sleep(5000);
        fluentSuperheroController.getHero(createHeroResponse.getId()).statusCodeIs(200);
    }

    @DisplayName("try to get hero with non used ID")
    @Tags({@Tag("smoke"), @Tag("API")})
    @Test
    void getHeroWithNonUsedID() {
        SuperheroError expectedResponse =
                new SuperheroError(String.format("Superhero with id '%d' was not found", NON_USED_ID), "NOT_FOUND");

        fluentSuperheroController.getHero(NON_USED_ID)
                .statusCodeIs(400)
                .compareWithErrorBody(expectedResponse);
    }

    @DisplayName("try to get hero with invalid ID")
    @Tags({@Tag("smoke"), @Tag("API")})
    @Test
    void getHeroWithInvalidID() {
        SuperheroError expectedResponse =
                new SuperheroError(String.format("Superhero with id '%d' was not found", INVALID_ID), "NOT_FOUND");

        fluentSuperheroController.getHero(INVALID_ID)
                .statusCodeIs(400)
                .compareWithErrorBody(expectedResponse);
    }

    @DisplayName("create a hero and try to get hero with invalid path")
    @Tags({@Tag("smoke"), @Tag("API")})
    @Test
    void createHeroAndGetHeroWithInvalidPath() {
        String expectedError = "Not Found";
        String expectedMessage = "No message available";

        Superhero createdHeroResponse = fluentSuperheroController.createHero(BASIC_HERO)
                .statusCodeIs(200)
                .getSuperheroFromResponse();

        fluentSuperheroController.getHeroWithInvalidPath(createdHeroResponse.getId())
                .statusCodeIs(404)
                .jsonValueCompare("error", expectedError)
                .jsonValueCompare("message", expectedMessage);
    }

    @DisplayName("get all heroes and check status code")
    @Tags({@Tag("smoke"), @Tag("API")})
    @Test
    void getAllHeroes() {
        fluentSuperheroController.getAllHeroes()
                .statusCodeIs(200);
    }

    @DisplayName("try to get all heroes with invalid path and check ")
    @Tags({@Tag("smoke"), @Tag("API")})
    @Test
    void getAllHeroesWithInvalidPath() {
        String expectedError = "Not Found";
        String expectedMessage = "No message available";

        fluentSuperheroController.getAllHeroesWithInvalidPath()
                .statusCodeIs(404)
                .jsonValueCompare("error", expectedError)
                .jsonValueCompare("message", expectedMessage);
    }

    @DisplayName("get all heroes, check status code and response body")
    @Tags({@Tag("smoke"), @Tag("API")})
    @Test
    void getAllHeroesAndCheck() {
        Superhero ironMan =
                fluentSuperheroController.createHero(HERO_WITH_ALL_FIELDS).statusCodeIs(200).getSuperheroFromResponse();
        Superhero captainAmerica =
                fluentSuperheroController.createHero(BASIC_HERO).statusCodeIs(200).getSuperheroFromResponse();
        List<Superhero> expectedHeroes = List.of(ironMan, captainAmerica);

        fluentSuperheroController.getAllHeroes()
                .statusCodeIs(200)
                .getAllHeroesAndCompareBodies(expectedHeroes);
    }

    @DisplayName("create a hero, update hero and check status code")
    @Tags({@Tag("smoke"), @Tag("API"), @Tag("will fail"), @Tag("Reason: get sometimes finds hero with different id")})
    @Test
    void createAndUpdateHero() throws InterruptedException {
        Superhero heroToUpdate = Superhero.builder()
                .birthDate("1756-05-11")
                .city("Boston")
                .fullName("Wolverine")
                .gender(GenderForSuperhero.M)
                .mainSkill("Regeneration")
                .phone("987891")
                .build();

        Superhero createHeroResponse = fluentSuperheroController.createHero(HERO_FOR_UPDATE)
                .statusCodeIs(200)
                .getSuperheroFromResponse();

        Thread.sleep(5000);
        fluentSuperheroController.updateHero(heroToUpdate, createHeroResponse.getId()).statusCodeIs(200);
    }

    @DisplayName("create a hero, update hero, check status code and changes")
    @Tags({@Tag("smoke"), @Tag("API"), @Tag("may fail"), @Tag("can't always find by id")})
    @Flaky
    @Test
    void createUpdateHeroAndCheckChanges() throws InterruptedException {
        Superhero heroToUpdate = Superhero.builder()
                .birthDate("1975-08-18")
                .city("New York")
                .fullName("Iron Man")
                .gender(GenderForSuperhero.M)
                .id(481)
                .mainSkill("Technology")
                .build();

        Superhero createHeroResponse = fluentSuperheroController.createHero(HERO_WITH_ALL_FIELDS)
                .statusCodeIs(200)
                .getSuperheroFromResponse();

        Thread.sleep(5000);
        fluentSuperheroController.updateHero(heroToUpdate, createHeroResponse.getId())
                .statusCodeIs(200);

        Thread.sleep(5000);
        fluentSuperheroController.getHero(createHeroResponse.getId())
                .statusCodeIs(200)
                .jsonValueCompare("fullName", heroToUpdate.getFullName())
                .jsonValueCompare("mainSkill", heroToUpdate.getMainSkill());
    }

    @DisplayName("create a hero, try to update hero with non used id and check status code")
    @Tags({@Tag("smoke"), @Tag("API")})
    @Test
    void createAndUpdateHeroWithNonUsedID() throws InterruptedException {
        Superhero heroToUpdate = Superhero.builder()
                .birthDate("1756-05-11")
                .city("Boston")
                .fullName("Wolverine")
                .gender(GenderForSuperhero.M)
                .mainSkill("Regeneration")
                .phone("987891")
                .build();
        SuperheroError notFoundResponse =
                new SuperheroError(String.format("Superhero with id '%d' was not found", NON_USED_ID), "NOT_FOUND");

       fluentSuperheroController.createHero(HERO_FOR_UPDATE).statusCodeIs(200);

        Thread.sleep(5000);
        fluentSuperheroController.updateHero(heroToUpdate, NON_USED_ID)
                .statusCodeIs(400)
                .compareWithErrorBody(notFoundResponse);
    }

    @DisplayName("create a hero, try to update hero with invalid id and check status code")
    @Tags({@Tag("smoke"), @Tag("API")})
    @Test
    void createAndUpdateHeroWithInvalidID() throws InterruptedException {
        Superhero heroToUpdate = Superhero.builder()
                .birthDate("1756-05-11")
                .city("Boston")
                .fullName("Wolverine")
                .gender(GenderForSuperhero.M)
                .mainSkill("Regeneration")
                .phone("987891")
                .build();
        SuperheroError notFoundResponse =
                new SuperheroError(String.format("Superhero with id '%d' was not found", INVALID_ID), "NOT_FOUND");

        fluentSuperheroController.createHero(HERO_FOR_UPDATE).statusCodeIs(200);

        Thread.sleep(5000);
        fluentSuperheroController.updateHero(heroToUpdate, INVALID_ID)
                .statusCodeIs(400)
                .compareWithErrorBody(notFoundResponse);
    }

    @DisplayName("create a hero, try to update hero with missing field and check status code")
    @Tags({@Tag("smoke"), @Tag("API")})
    @Test
    void createAndUpdateHeroWithMissingField() throws InterruptedException {
        String expectedStatusValue = "500";
        String expectedMessagePart = "could not execute statement";
        String expectedError = "Internal Server Error";

        Superhero createdHerResponse = fluentSuperheroController.createHero(HERO_FOR_UPDATE)
                .statusCodeIs(200)
                .getSuperheroFromResponse();

        Thread.sleep(5000);
        fluentSuperheroController.updateHero(HERO_WITHOUT_SKILL, createdHerResponse.getId())
                .statusCodeIs(500)
                .jsonValueCompare("error", expectedError)
                .jsonValueCompare("status", expectedStatusValue)
                .getJsonValue("message").contains(expectedMessagePart);
    }

    @DisplayName("create a hero, try to update hero with invalid path and check status code")
    @Tags({@Tag("smoke"), @Tag("API")})
    @Test
    void createAndUpdateHeroWithInvalidPath() throws InterruptedException {
        String expectedError = "Not Found";
        String expectedMessage = "No message available";
        Superhero heroToUpdate = Superhero.builder()
                .birthDate("1756-05-11")
                .city("Boston")
                .fullName("Wolverine")
                .gender(GenderForSuperhero.M)
                .mainSkill("Regeneration")
                .phone("987891")
                .build();

        Superhero createHeroResponse = fluentSuperheroController.createHero(HERO_FOR_UPDATE)
                .statusCodeIs(200)
                .getSuperheroFromResponse();

        Thread.sleep(5000);
        fluentSuperheroController.updateHeroWithInvalidPath(heroToUpdate, createHeroResponse.getId())
                .statusCodeIs(404)
                .jsonValueCompare("error", expectedError)
                .jsonValueCompare("message", expectedMessage);
    }

    @DisplayName("create a hero, delete it and check status code")
    @Tags({@Tag("smoke"), @Tag("API")})
    @Test
    void createAndDeleteHero() throws InterruptedException {
        Superhero createHeroResponse = fluentSuperheroController.createHero(BASIC_HERO)
                .statusCodeIs(200)
                .getSuperheroFromResponse();

        Thread.sleep(5000);
        fluentSuperheroController.deleteHero(createHeroResponse.getId())
                .statusCodeIs(200);
    }

    @DisplayName("create a hero, delete it, check status code and get deleted hero after")
    @Tags({@Tag("smoke"), @Tag("API")})
    @Test
    void createDeleteHeroAndGetDeleted() throws InterruptedException {
        Superhero createdHeroResponse = fluentSuperheroController.createHero(BASIC_HERO)
                .statusCodeIs(200)
                .getSuperheroFromResponse();

        SuperheroError errorResponse =
                new SuperheroError(String.format
                        ("Superhero with id '%d' was not found", createdHeroResponse.getId()), "NOT_FOUND");

        Thread.sleep(5000);
        fluentSuperheroController.deleteHero(createdHeroResponse.getId())
                .statusCodeIs(200);

        Thread.sleep(5000);
        fluentSuperheroController.getHero(createdHeroResponse.getId())
                .statusCodeIs(400)
                .compareWithErrorBody(errorResponse);
    }

    @DisplayName("try to delete hero with non used ID")
    @Tags({@Tag("smoke"), @Tag("API"), @Tag("will fail"),
            @Tag("Reason: delete works even if there is no hero under this id")})
    @Test
    void createAndDeleteHeroWithNonUsedID() throws InterruptedException {
        SuperheroError expectedGerResponse =
                new SuperheroError(String.format("Superhero with id '%d' was not found", NON_USED_ID), "NOT_FOUND");

        fluentSuperheroController.getHero(NON_USED_ID)
                .statusCodeIs(400)
                .compareWithErrorBody(expectedGerResponse);

        fluentSuperheroController.deleteHero(NON_USED_ID)
                .statusCodeIs(204);
    }

    @DisplayName("try to delete hero with invalid ID")
    @Tags({@Tag("smoke"), @Tag("API"), @Tag("will fail"),
            @Tag("Reason: delete works even if there is no hero under this id")})
    @Test
    void createAndDeleteHeroWithInvalidID() throws InterruptedException {
        SuperheroError expectedGerResponse =
                new SuperheroError(String.format("Superhero with id '%d' was not found", INVALID_ID), "NOT_FOUND");

        fluentSuperheroController.getHero(INVALID_ID)
                .statusCodeIs(400)
                .compareWithErrorBody(expectedGerResponse);

        fluentSuperheroController.deleteHero(INVALID_ID)
                .statusCodeIs(204);
    }

    @DisplayName("try to delete hero with invalid path")
    @Tags({@Tag("smoke"), @Tag("API")})
    @Test
    void createAndDeleteHeroWithInvalidPath() throws InterruptedException {
        String expectedError = "Not Found";
        String expectedMessage = "No message available";

        Superhero createdHero = fluentSuperheroController.createHero(BASIC_HERO)
                .statusCodeIs(200)
                .getSuperheroFromResponse();

        fluentSuperheroController.deleteHeroWithInvalidPath(createdHero.getId())
                .statusCodeIs(404)
                .jsonValueCompare("error", expectedError)
                .jsonValueCompare("message", expectedMessage);
    }
}
