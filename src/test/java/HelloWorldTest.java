import org.junit.jupiter.api.Test;
import io.restassured.RestAssured;
import io.restassured.response.Response;


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
