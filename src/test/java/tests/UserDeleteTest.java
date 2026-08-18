package tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;


    @Epic("User deletion cases")
    @Feature("Delete user")
    public class UserDeleteTest extends BaseTestCase {

        private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

        // 1. Попытка удалить пользователя с ID 2
        @Test
        @Description("This test checks that user with ID 2 cannot be deleted")
        @DisplayName("Negative: attempt to delete protected user ID 2")
        public void testDeleteProtectedUser() {

            Map<String, String> authData = new HashMap<>();
            authData.put("email", "vinkotov@example.com");
            authData.put("password", "1234");

            Response responseAuth = apiCoreRequests.makePostRequest(
                    "https://playground.learnqa.ru/api_dev/user/login", authData);
            String token = this.getHeader(responseAuth, "x-csrf-token");
            String cookie = this.getCookie(responseAuth, "auth_sid");


            Response responseDelete = apiCoreRequests.makeDeleteRequest(
                    "https://playground.learnqa.ru/api_dev/user/2", token, cookie);


            Assertions.assertResponseCodeEquals(responseDelete, 400);

        }



        // 2. Позитивный тест: создать, авторизоваться, удалить, проверить отсутствие
        @Test
        @Description("This test creates a new user, deletes it, and verifies it no longer exists")
        @DisplayName("Positive: delete newly created user")
        public void testDeleteNewlyCreatedUser() {

            Map<String, String> userData = DataGenerator.getRegistrationData();
            Response responseCreate = apiCoreRequests.makePostRequest(
                    "https://playground.learnqa.ru/api_dev/user/", userData);
            Assertions.assertResponseCodeEquals(responseCreate, 200);
            String userId = responseCreate.jsonPath().getString("id");


            Map<String, String> authData = new HashMap<>();
            authData.put("email", userData.get("email"));
            authData.put("password", userData.get("password"));
            Response responseAuth = apiCoreRequests.makePostRequest(
                    "https://playground.learnqa.ru/api_dev/user/login", authData);
            String token = this.getHeader(responseAuth, "x-csrf-token");
            String cookie = this.getCookie(responseAuth, "auth_sid");


            Response responseDelete = apiCoreRequests.makeDeleteRequest(
                    "https://playground.learnqa.ru/api_dev/user/" + userId, token, cookie);
            Assertions.assertResponseCodeEquals(responseDelete, 200); // обычно при успешном удалении возвращается 200


            Response responseGet = apiCoreRequests.makeGetRequest(
                    "https://playground.learnqa.ru/api_dev/user/" + userId, token, cookie);

            Assertions.assertResponseCodeEquals(responseGet, 404);
        }

        // 3. Негативный: попытка удалить пользователя, будучи авторизованным другим пользователем
        @Test
        @Description("This test checks that a user cannot delete another user's account")
        @DisplayName("Negative: delete user by different user")
        public void testDeleteUserByDifferentUser() {

            Map<String, String> userData = DataGenerator.getRegistrationData();
            Response responseCreate = apiCoreRequests.makePostRequest(
                    "https://playground.learnqa.ru/api_dev/user/", userData);
            Assertions.assertResponseCodeEquals(responseCreate, 200);
            String userId = responseCreate.jsonPath().getString("id");


            Map<String, String> authData = new HashMap<>();
            authData.put("email", "vinkotov@example.com");
            authData.put("password", "1234");
            Response responseAuth = apiCoreRequests.makePostRequest(
                    "https://playground.learnqa.ru/api_dev/user/login", authData);
            String token = this.getHeader(responseAuth, "x-csrf-token");
            String cookie = this.getCookie(responseAuth, "auth_sid");


            Response responseDelete = apiCoreRequests.makeDeleteRequest(
                    "https://playground.learnqa.ru/api_dev/user/" + userId, token, cookie);


            Assertions.assertResponseCodeEquals(responseDelete, 400);

        }


}
