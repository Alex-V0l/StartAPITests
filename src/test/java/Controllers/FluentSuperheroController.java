package Controllers;

import Models.Superhero;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.specification.RequestSpecification;

import static Constants.Constants.BASE_SUPERHERO_URL;
import static Constants.Constants.SUPERHERO_ENDPOINT;
import static io.restassured.RestAssured.given;

public class FluentSuperheroController {

    private RequestSpecification requestSpecification;

    public FluentSuperheroController(){
        RestAssured.defaultParser = Parser.JSON;
        requestSpecification = new RequestSpecBuilder()
                .setBaseUri(BASE_SUPERHERO_URL)
                .setContentType(ContentType.JSON)
                .addFilter(new AllureRestAssured())
                .build();
    }

    public RequestSpecification getRequestSpecification() {
        return requestSpecification;
    }

    @Step("create a hero")
    public SuperheroResponse createHero(Superhero hero){
        this.requestSpecification.body(hero);
        return new SuperheroResponse(given(this.requestSpecification).post(SUPERHERO_ENDPOINT).then());
    }

    @Step("update created hero")
    public SuperheroResponse updateHero(Superhero hero, long id){
        this.requestSpecification.body(hero);
        return new SuperheroResponse(given(this.requestSpecification).put(SUPERHERO_ENDPOINT + id).then());
    }

    @Step("get hero")
    public SuperheroResponse getHero(long id){
        return new SuperheroResponse(given(this.requestSpecification).get(SUPERHERO_ENDPOINT + id).then());
    }

    @Step("delete hero")
    public SuperheroResponse deleteHero(long id){
        return new SuperheroResponse(given(this.requestSpecification).delete(SUPERHERO_ENDPOINT + id).then());
    }

    @Step("get all heroes")
    public SuperheroResponse getAllHeroes(){
        return new SuperheroResponse(given(this.requestSpecification).get("superheroes").then());
    }

    @Step("create a hero with invalid path")
    public SuperheroResponse createHeroWithInvalidPath(Superhero hero){
        this.requestSpecification.body(hero);
        return new SuperheroResponse(given(this.requestSpecification).post("wrong/").then());
    }

    @Step("get all heroes with invalid path")
    public SuperheroResponse getAllHeroesWithInvalidPath(){
        return new SuperheroResponse(given(this.requestSpecification).get("wrong/").then());
    }

    @Step("get hero with invalid path")
    public SuperheroResponse getHeroWithInvalidPath(long id){
        return new SuperheroResponse(given(this.requestSpecification).get("wrong/" + id).then());
    }

    @Step("delete hero with invalid path")
    public SuperheroResponse deleteHeroWithInvalidPath(long id){
        return new SuperheroResponse(given(this.requestSpecification).delete("wrong/" + id).then());
    }

    @Step("update created hero with invalid path")
    public SuperheroResponse updateHeroWithInvalidPath(Superhero hero, long id){
        this.requestSpecification.body(hero);
        return new SuperheroResponse(given(this.requestSpecification).put("wrong/" + id).then());
    }
}
