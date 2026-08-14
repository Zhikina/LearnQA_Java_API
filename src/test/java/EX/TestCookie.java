package EX;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCookie {
    @Test
    public void TestCookie(){
        Response response = RestAssured
                .given()
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();
       // Map<String, String> allCookies = response.getCookies();
       // System.out.println("Все cookie из ответа: " + allCookies);

        String cookieName = "HomeWork";
        String cookieValue =response.getCookie(cookieName);

        assertEquals("hw_value", cookieValue);
    }


    @Test
    public void TestHeader(){
        Response response = RestAssured
                .given()
                .get("https://playground.learnqa.ru/api/homework_header")
                .andReturn();

//Headers responseHeaders = response.getHeaders();
//System.out.println(responseHeaders);



        String headerName = "x-secret-homework-header";
        String headerValue =response.getHeader(headerName);

        assertEquals("Some secret value", headerValue);
    }




}
