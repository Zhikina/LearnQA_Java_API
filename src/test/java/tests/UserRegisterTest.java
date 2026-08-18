package tests;

import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

public class UserRegisterTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    @Test
    @Severity(SeverityLevel.NORMAL)
    @Tag("regression")
    @Step("Attempt to register with an existing email (negative)")
    public void testCreateUserWithExistingEmail(){
        String email = "vinkotov@example.com";

        Map<String, String> userData = new HashMap<>();

        userData = DataGenerator.getRegistrationData(userData);
        userData.put("email",email);

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api_dev/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Users with email '" + email+ "' already exists");



    }


    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Tag("smoke")
    @Tag("regression")
    @Step("Register a new user successfully (positive)")
    public void testCreateUserSuccessfully(){
        String email = DataGenerator.getRandomEmail();

        Map<String, String> userData = DataGenerator.getRegistrationData();


        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api_dev/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 200);
       Assertions.assertJsonHasField(responseCreateAuth,"id");



    }

    // 1. Создание пользователя с некорректным email (без символа @)
    @Test
    @Severity(SeverityLevel.NORMAL)
    @Tag("regression")
    @Step("Register with invalid email (without @) - negative")
    public void testCreateUserWithInvalidEmail() {
        Map<String, String> userData = DataGenerator.getRegistrationData();
        userData.put("email", "invalidEmailWithoutAtSymbol.com");

        Response response = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api_dev/user/", userData);

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "Invalid email format");

    }
    // 2. Создание пользователя без одного из обязательных полей (параметризованный тест)
    @ParameterizedTest
    @ValueSource(strings = {"email", "password", "username", "firstName", "lastName"})
    @Severity(SeverityLevel.NORMAL)
    @Tag("regression")
    @Step("Attempt to register without required field: {missingField} (negative)")
    public void testCreateUserWithoutRequiredField(String missingField) {
        Map<String, String> userData = DataGenerator.getRegistrationData();
        userData.remove(missingField);

        Response response = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api_dev/user/", userData);

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response,
                "The following required params are missed: " + missingField);

    }

    // 3. Создание пользователя с именем из одного символа
    @Test
    @Severity(SeverityLevel.NORMAL)
    @Tag("regression")
    @Step("Register with first name of 1 character (negative)")
    public void testCreateUserWithShortName() {
        Map<String, String> userData = DataGenerator.getRegistrationData();
        userData.put("firstName", "A");

        Response response = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api_dev/user/", userData);

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response,
                "The value of 'firstName' field is too short");

    }

    // 4. Создание пользователя с именем длиннее 250 символов
    @Test
    @Severity(SeverityLevel.NORMAL)
    @Tag("regression")
    @Step("Register with first name longer than 250 characters (negative)")
    public void testCreateUserWithVeryLongName() {
        String longName = new String(new char[251]).replace('\0', 'a'); // 251 символ

        Map<String, String> userData = DataGenerator.getRegistrationData();
        userData.put("firstName", longName);

        Response response = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api_dev/user/", userData);

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response,
                "The value of 'firstName' field is too long");

    }






}
