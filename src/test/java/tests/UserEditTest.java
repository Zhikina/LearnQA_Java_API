package tests;

import io.restassured.RestAssured;
import io.restassured.internal.common.assertion.Assertion;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Description;
import org.junit.jupiter.api.DisplayName;


public class UserEditTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    public void testEditJustCreatedTest(){
        //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api_dev/user/")
                .jsonPath();

        String userId = responseCreateAuth.getString("id");



        //LOGIN
        Map<String,String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api_dev/user/login")
                .andReturn();






        //EDIT

        String  newName = "Change Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);


        Response responseEditUsr = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .body(editData)
                .put("https://playground.learnqa.ru/api_dev/user/" + userId)
                .andReturn();



//GET
      Response responseUserData = RestAssured
              .given()
              .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
              .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
              .get("https://playground.learnqa.ru/api_dev/user/" + userId)
              .andReturn();


        Assertions.asserJsonByName(responseUserData, "firstName", newName);

    }


    // 1. Попытка редактировать без авторизации
    @Test
    @Description("This test checks that user cannot edit without authorization")
    @DisplayName("Negative: edit user without auth")
    public void testEditUserNotAuthorized() {

        Map<String, String> userData = DataGenerator.getRegistrationData();
        Response responseCreate = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api_dev/user/", userData);
        Assertions.assertResponseCodeEquals(responseCreate, 200);
        String userId = responseCreate.jsonPath().getString("id");


        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", "NewName");

        Response responseEdit = apiCoreRequests.makePutRequestWithoutAuth(
                "https://playground.learnqa.ru/api_dev/user/" + userId, editData);


        Assertions.assertResponseCodeEquals(responseEdit, 400);
    }

    // 2. Попытка редактировать пользователя, будучи авторизованным другим пользователем
    @Test
    @Description("This test checks that user cannot edit another user's data")
    @DisplayName("Negative: edit user by different user")
    public void testEditUserByDifferentUser() {

        Map<String, String> userData1 = DataGenerator.getRegistrationData();
        Response responseCreate1 = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api_dev/user/", userData1);
        Assertions.assertResponseCodeEquals(responseCreate1, 200);
        String userId1 = responseCreate1.jsonPath().getString("id");


        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");
        Response responseAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api_dev/user/login", authData);
        String token = this.getHeader(responseAuth, "x-csrf-token");
        String cookie = this.getCookie(responseAuth, "auth_sid");


        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", "HackedName");

        Response responseEdit = apiCoreRequests.makePutRequest(
                "https://playground.learnqa.ru/api_dev/user/" + userId1, token, cookie, editData);


        Assertions.assertResponseCodeEquals(responseEdit, 400);
    }


    // 3. Попытка изменить email на невалидный (без @), авторизованным тем же пользователем
    @Test
    @Description("This test checks that user cannot set invalid email (without @)")
    @DisplayName("Negative: edit email with invalid format")
    public void testEditEmailWithInvalidFormat() {

        Map<String, String> userData = DataGenerator.getRegistrationData();
        Response responseCreate = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api_dev/user/", userData);
        Assertions.assertResponseCodeEquals(responseCreate, 200);
        String userId = responseCreate.jsonPath().getString("id");
        String email = userData.get("email");
        String password = userData.get("password");


        Map<String, String> authData = new HashMap<>();
        authData.put("email", email);
        authData.put("password", password);
        Response responseAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api_dev/user/login", authData);
        String token = this.getHeader(responseAuth, "x-csrf-token");
        String cookie = this.getCookie(responseAuth, "auth_sid");


        Map<String, String> editData = new HashMap<>();
        editData.put("email", "invalidEmailNoAtSymbol.com");

        Response responseEdit = apiCoreRequests.makePutRequest(
                "https://playground.learnqa.ru/api_dev/user/" + userId, token, cookie, editData);


        Assertions.assertResponseCodeEquals(responseEdit, 400);
        Assertions.asserJsonByName(responseEdit, "error", "Invalid email format");
    }




    // 4. Попытка изменить firstName на очень короткое значение (1 символ)
    @Test
    @Description("This test checks that user cannot set firstName shorter than 2 characters")
    @DisplayName("Negative: edit firstName too short")
    public void testEditFirstNameTooShort() {

        Map<String, String> userData = DataGenerator.getRegistrationData();
        Response responseCreate = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api_dev/user/", userData);
        Assertions.assertResponseCodeEquals(responseCreate, 200);
        String userId = responseCreate.jsonPath().getString("id");
        String email = userData.get("email");
        String password = userData.get("password");


        Map<String, String> authData = new HashMap<>();
        authData.put("email", email);
        authData.put("password", password);
        Response responseAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api_dev/user/login", authData);
        String token = this.getHeader(responseAuth, "x-csrf-token");
        String cookie = this.getCookie(responseAuth, "auth_sid");


        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", "A");

        Response responseEdit = apiCoreRequests.makePutRequest(
                "https://playground.learnqa.ru/api_dev/user/" + userId, token, cookie, editData);


        Assertions.assertResponseCodeEquals(responseEdit, 400);
        Assertions.asserJsonByName(responseEdit, "error", "The value for field `firstName` is too short");
    }

}
