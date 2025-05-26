package Controllers;

import Models.Superhero;
import Models.SuperheroError;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.assertj.core.api.Assertions;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class SuperheroResponse {

    private final ValidatableResponse superResponse;

    public SuperheroResponse(ValidatableResponse response) {
        this.superResponse = response;
    }

    @Step("Check status code")
    public SuperheroResponse statusCodeIs(int status) {
        this.superResponse.statusCode(status);
        return this;
    }

    @Step("compare json value by path with expected value")
    public SuperheroResponse jsonValueCompare(String path, String expectedValue){
        String actualValue = this.superResponse.extract().jsonPath().getString(path);
        Assertions.assertThat(actualValue).as("Actual value is not equal to expected").isEqualTo(expectedValue);
        return this;
    }

    @Step("Check that json value is not null")
    public SuperheroResponse jsonValueIsNotNull(String path) {
        String actualValue = this.superResponse.extract().jsonPath().getString(path);
        Assertions.assertThat(actualValue).isNotNull();
        return this;
    }

    @Step("Check that json value is null")
    public SuperheroResponse jsonValueIsNull(String path) {
        String actualValue = this.superResponse.extract().jsonPath().getString(path);
        Assertions.assertThat(actualValue).isNull();
        return this;
    }

    @Step("Get json value by path")
    public String getJsonValue(String path) {
        String value = this.superResponse.extract().jsonPath().getString(path);
        Assertions.assertThat(value).isNotNull();
        return value;
    }

    @Step("Check response body with deserialization to superhero")
    public SuperheroResponse compareWithHero(Superhero expectedHero){
        Superhero actualHero = this.superResponse.extract().body().as(Superhero.class);
        expectedHero.setId(actualHero.getId());
        Assertions.assertThat(actualHero).usingRecursiveComparison().isEqualTo(expectedHero);
        return this;
    }

    @Step("Check error response body with deserialization to superhero error")
    public SuperheroResponse compareWithErrorBody(SuperheroError expectedHeroError){
        SuperheroError actualHeroError = this.superResponse.extract().body().as(SuperheroError.class);
        Assertions.assertThat(actualHeroError).usingRecursiveComparison().isEqualTo(expectedHeroError);
        return this;
    }

    @Step("parse as superhero")
    public Superhero getSuperheroFromResponse(){
        Superhero hero = this.superResponse.extract().body().as(Superhero.class);
        return hero;
    }

    @Step("turn response of all heroes into list and compare each hero")
    public List<Superhero> getAllHeroesAndCompareBodies(List<Superhero> expectedList){
        Superhero[] response = this.superResponse.extract().body().as(Superhero[].class);
        List<Superhero> actualList = Arrays.asList(response);
        IntStream.range(0, actualList.size()).forEach(i ->{
            Superhero actual = actualList.get(i);
            Superhero expected = expectedList.get(i);
            assertThat(actual).isEqualTo(expected);
        });
        return actualList;
    }
}
