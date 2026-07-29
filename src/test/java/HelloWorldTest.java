import org.junit.jupiter.api.Test;
import io.restassured.RestAssured;
import io.restassured.response.Response;


public class HelloWorldTest {
    @Test
    public void testHelloWorld() {
        String testUrl = "https://playground.learnqa.ru/api/long_redirect";
    while (true){
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get(testUrl)
                .andReturn();

        if (response.getStatusCode() == 200) {
            System.out.println("Ответ: " + testUrl);
            break;
        }
        String newTestUrl = response.getHeader("Location");
        testUrl = newTestUrl;
    }

    }
}
