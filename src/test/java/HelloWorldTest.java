import org.junit.jupiter.api.Test;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class HelloWorldTest {
    @Test
    public void testHelloWorld()  throws InterruptedException {
        String testUrl = "https://playground.learnqa.ru/api/longtime_job";

        Response response = RestAssured
                .given()
                .get(testUrl)
                .andReturn();

        String token = response.jsonPath().getString("token");
        int seconds = response.jsonPath().getInt("seconds");


        Response notReadyResponse = RestAssured
                .given()
                .queryParam("token", token)
                .get(testUrl)
                .andReturn();


        String statusBefore = notReadyResponse.jsonPath().getString("status");
        assertEquals("Job is NOT ready", statusBefore);

        Thread.sleep(seconds * 1000L);

        Response readyResponse = RestAssured
                .given()
                .queryParam("token", token)
                .get(testUrl)
                .andReturn();


        String statusAfter = readyResponse.jsonPath().getString("status");
        assertEquals("Job is ready", statusAfter);


        String result = readyResponse.jsonPath().getString("result");
        assertNotNull(result, "Поле result не должно быть null");

    }
}
