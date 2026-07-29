import org.junit.jupiter.api.Test;
import io.restassured.RestAssured;
import io.restassured.response.Response;


public class HelloWorldTest {
    @Test
    public void testHelloWorld() {

        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get("https://playground.learnqa.ru/api/long_redirect")
                .andReturn();
        String redirectUrl = response.getHeader("Location");
        System.out.println("Ответ: " + redirectUrl);
    }
}
