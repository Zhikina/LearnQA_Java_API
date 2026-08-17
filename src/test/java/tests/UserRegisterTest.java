package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

public class UserRegisterTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    @Test
    public void testCreateUserWithExistingEmail(){
        String email = "vinkotov@example.com";

        Map<String, String> userData = new HashMap<>();

        userData = DataGenerator.getRegistrationData(userData);
        userData.put("email",email);

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Users with email '" + email+ "' already exists");



    }


    @Test
    public void testCreateUserSuccessfully(){
        String email = DataGenerator.getRandomEmail();

        Map<String, String> userData = DataGenerator.getRegistrationData();


        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 200);
       Assertions.assertJsonHasField(responseCreateAuth,"id");



    }

    // 1. Создание пользователя с некорректным email (без символа @)
    @Test
    public void testCreateUserWithInvalidEmail() {
        Map<String, String> userData = DataGenerator.getRegistrationData();
        userData.put("email", "invalidEmailWithoutAtSymbol.com");

        Response response = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "Invalid email format");

    }
    // 2. Создание пользователя без одного из обязательных полей (параметризованный тест)
    @ParameterizedTest
    @ValueSource(strings = {"email", "password", "username", "firstName", "lastName"})
    public void testCreateUserWithoutRequiredField(String missingField) {
        Map<String, String> userData = DataGenerator.getRegistrationData();
        userData.remove(missingField);

        Response response = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response,
                "The following required params are missed: " + missingField);

    }

    // 3. Создание пользователя с именем из одного символа
    @Test
    public void testCreateUserWithShortName() {
        Map<String, String> userData = DataGenerator.getRegistrationData();
        userData.put("firstName", "A");

        Response response = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response,
                "The value of 'firstName' field is too short");

    }

    // 4. Создание пользователя с именем длиннее 250 символов
    @Test
    public void testCreateUserWithVeryLongName() {
        String longName = new String(new char[251]).replace('\0', 'a'); // 251 символ

        Map<String, String> userData = DataGenerator.getRegistrationData();
        userData.put("firstName", longName);

        Response response = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response,
                "The value of 'firstName' field is too long");

    }






}
