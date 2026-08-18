package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import io.qameta.allure.Epic;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;



public class UserGetTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    public void testGetUserDataNotAuth(){
        Response responseUserData = RestAssured
                .get("https://playground.learnqa.ru/api_dev/user/2")
                .andReturn();

        Assertions.assertJsonHasField(responseUserData, "username");
        Assertions.assertJsonHasNotField(responseUserData, "firstName");
        Assertions.assertJsonHasNotField(responseUserData, "lastName");
        Assertions.assertJsonHasNotField(responseUserData, "email");

    }




    @Test
    public void testGetUserDetailsAuthAsSameUser(){
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api_dev/user/login")
                .andReturn();
        String header  = this.getHeader(responseGetAuth,"x-csrf-token" );
        String cookie = this.getCookie(responseGetAuth, "auth_sid");


        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", header)
                .cookie("auth_sid", cookie)
                .get("https://playground.learnqa.ru/api_dev/user/2")
                .andReturn();

        String[] expectedFields = {"username","firstName","lastName", "email" };
        Assertions.assertJsonHasFields(responseUserData, expectedFields);




    }


    @Test
    @Description("This test checks that user cannot see another user's full data (only username)")
    @DisplayName("Test negative: get another user's data while being authorized as different user")
    public void testGetUserDataAuthAsDifferentUser() {
        // 1. Логин под пользователем "vinkotov@example.com"
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api_dev/user/login", authData);

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        // 2. Запрос данных другого пользователя (с ID = 1)
        Response responseUserData = apiCoreRequests.makeGetRequest(
                "https://playground.learnqa.ru/api_dev/user/1", header, cookie);

        // 3. Проверки
        Assertions.assertJsonHasField(responseUserData, "username");
        Assertions.assertJsonHasNotField(responseUserData, "firstName");
        Assertions.assertJsonHasNotField(responseUserData, "lastName");
        Assertions.assertJsonHasNotField(responseUserData, "email");
    }




}
