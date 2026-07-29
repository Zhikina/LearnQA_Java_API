import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.http.Headers;

import java.util.HashMap;
import java.util.Map;


public class HelloWorldTest {
    @Test
    public void testHelloWorld() {

        Response response = RestAssured
                .given()
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .andReturn();

        String message = response.jsonPath().getString("messages[1].message");
        System.out.println("Ответ: " + message);
    }
}
