package Tests;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class LoadTests {

    @Test
    void testDownloadHttpClient() {
        String endpoint = "https://alfabank.servicecdn.ru/site-upload/67/dd/356/zayavlenie-IZK.pdf";
        String fileName = "downloaded.pdf";

        Response response =
                given().
                        when().
                        get(endpoint).
                        then().
                        contentType("application/pdf").
                        statusCode(200).
                        extract().response();
        savePdf(response, fileName);

        File downloadedFile = new File(fileName);
        assertThat(downloadedFile).exists();
    }

    private static void savePdf(Response response, String fileName) {
        if (response != null) {

            try (InputStream inputStream = response.getBody().asInputStream()) {
                OutputStream outputStream = new FileOutputStream(fileName);

                int bytesRead;
                byte[] buffer = new byte[4096];
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                inputStream.close();
                outputStream.close();
                System.out.println("PDF успешно сохранен в файл: " + fileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    void apiUploadTest() {
        String apiUrl = "https://petstore.swagger.io/v2/pet/1/uploadImage";
        File file = new File("src/main/resources/cat.png");
        Response response =
                given()
                        .header("accept", "application/json")
                        .contentType("multipart/form-data")
                        .multiPart("file", file, "image/jpeg")
                        .when()
                        .post(apiUrl)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();
        System.out.println("Response: " + response.asString());
    }
}