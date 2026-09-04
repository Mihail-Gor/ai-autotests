package org.example.api.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.example.api.data.UserDataFactory;
import org.example.api.models.user.UserDto;
import org.example.api.models.user.UsersResponseDto;
import org.example.api.specifications.ResponseSpecs;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.api.assertions.UserAssert.assertThatUser;

@Epic("DummyJSON User API")
@Feature("Users Management")
@Owner("QA Automation Engineer")
@Tag("regression")
@Tag("users")
@DisplayName("Users CRUD Test Suite")
public class UserCrudTest extends BaseTest {

    @Nested
    @Story("Create User")
    @DisplayName("Create (POST) Operations")
    class CreateOperations {

        @Test
        @Severity(SeverityLevel.BLOCKER)
        @DisplayName("POST /users/add - Successfully create user with full profile")
        @Description("Verifies creating a user with personal info, address, company and credentials.")
        void shouldCreateUserWithFullProfile() {
            UserDto newUser = UserDataFactory.createValidUser();

            UserDto createdUser = userClient.createUser(newUser);

            assertThatUser(createdUser)
                    .isNotNull()
                    .hasFirstName(newUser.getFirstName())
                    .hasLastName(newUser.getLastName())
                    .hasEmail(newUser.getEmail());

            assertThat(createdUser.getId())
                    .as("Generated user id should be positive")
                    .isNotNull()
                    .isPositive();
        }

        @ParameterizedTest(name = "POST /users/add - Name: {0} {1}, Email: {2}")
        @CsvSource({
                "John, Doe, john.doe.test@example.com",
                "Alice, Smith, alice.smith.test@example.com"
        })
        @Severity(SeverityLevel.CRITICAL)
        @DisplayName("POST /users/add - Successfully create user with minimal details (Parameterized)")
        @Description("Verifies user creation with minimal required fields.")
        void shouldCreateUserWithMinimalDetails(String firstName, String lastName, String email) {
            UserDto minimalUser = UserDataFactory.createMinimalUser(firstName, lastName, email);

            UserDto createdUser = userClient.createUser(minimalUser);

            assertThatUser(createdUser)
                    .isNotNull()
                    .hasFirstName(firstName)
                    .hasLastName(lastName)
                    .hasEmail(email);
        }
    }

    @Nested
    @Story("Read User")
    @DisplayName("Read (GET) Operations")
    class ReadOperations {

        @Test
        @Severity(SeverityLevel.BLOCKER)
        @DisplayName("GET /users/{id} - Successfully get user by ID")
        @Description("Verifies retrieving single user by ID with all detailed properties.")
        void shouldGetUserByIdSuccessfully() {
            int userId = 1;

            UserDto user = userClient.getUserById(userId);

            assertThatUser(user)
                    .isNotNull()
                    .hasId(userId);

            assertThat(user.getFirstName()).isNotBlank();
            assertThat(user.getLastName()).isNotBlank();
            assertThat(user.getEmail()).contains("@");
            assertThat(user.getAddress()).isNotNull();
            assertThat(user.getCompany()).isNotNull();
        }

        @Test
        @Severity(SeverityLevel.CRITICAL)
        @DisplayName("GET /users - Get paginated users list")
        @Description("Verifies retrieving users list with limit and skip query parameters.")
        void shouldGetUsersListWithPagination() {
            int limit = 10;
            int skip = 5;

            UsersResponseDto response = userClient.getUsers(limit, skip);

            assertThat(response).isNotNull();
            assertThat(response.getUsers())
                    .as("Users list size must equal limit")
                    .hasSize(limit);
            assertThat(response.getLimit()).isEqualTo(limit);
            assertThat(response.getSkip()).isEqualTo(skip);
            assertThat(response.getTotal()).isPositive();
        }

        @Test
        @Severity(SeverityLevel.NORMAL)
        @DisplayName("GET /users/{id} - Return 404 for non-existing user")
        @Description("Verifies that requesting a non-existent user returns 404 Not Found.")
        void shouldReturn404ForNonExistingUser() {
            int nonExistentId = 999999;

            Response response = userClient.getUserByIdRaw(nonExistentId);

            response.then().spec(ResponseSpecs.statusNotFound());
        }
    }

    @Nested
    @Story("Update User")
    @DisplayName("Update (PUT/PATCH) Operations")
    class UpdateOperations {

        @Test
        @Severity(SeverityLevel.CRITICAL)
        @DisplayName("PUT /users/{id} - Fully update user information")
        @Description("Verifies updating user data via PUT request.")
        void shouldFullyUpdateUserViaPut() {
            int userId = 1;
            UserDto updateData = UserDto.builder()
                    .firstName("Alexander")
                    .lastName("Great")
                    .age(33)
                    .email("alexander.great@ancient.com")
                    .build();

            UserDto updatedUser = userClient.updateUser(userId, updateData);

            assertThatUser(updatedUser)
                    .isNotNull()
                    .hasId(userId)
                    .hasFirstName(updateData.getFirstName())
                    .hasLastName(updateData.getLastName())
                    .hasEmail(updateData.getEmail());
        }

        @Test
        @Severity(SeverityLevel.NORMAL)
        @DisplayName("PATCH /users/{id} - Partially update user email")
        @Description("Verifies partial update of user fields via PATCH request.")
        void shouldPartiallyUpdateUserViaPatch() {
            int userId = 2;
            String newEmail = "updated.email.patch@example.com";
            UserDto patchData = UserDto.builder()
                    .email(newEmail)
                    .build();

            UserDto patchedUser = userClient.patchUser(userId, patchData);

            assertThatUser(patchedUser)
                    .isNotNull()
                    .hasId(userId)
                    .hasEmail(newEmail);

            assertThat(patchedUser.getFirstName())
                    .as("First name should be retained")
                    .isNotBlank();
        }
    }

    @Nested
    @Story("Delete User")
    @DisplayName("Delete (DELETE) Operations")
    class DeleteOperations {

        @Test
        @Severity(SeverityLevel.CRITICAL)
        @DisplayName("DELETE /users/{id} - Successfully delete user")
        @Description("Verifies deleting user and checking isDeleted and deletedOn flags.")
        void shouldDeleteUserByIdSuccessfully() {
            int userId = 1;

            UserDto deletedUser = userClient.deleteUser(userId);

            assertThatUser(deletedUser)
                    .isNotNull()
                    .hasId(userId)
                    .isDeleted();
        }

        @Test
        @Severity(SeverityLevel.NORMAL)
        @DisplayName("DELETE /users/{id} - Return 404 when deleting non-existing user")
        @Description("Verifies 404 response on deleting non-existent user.")
        void shouldReturn404WhenDeletingNonExistingUser() {
            int nonExistentId = 999999;

            Response response = userClient.deleteUserRaw(nonExistentId);

            response.then().spec(ResponseSpecs.statusNotFound());
        }
    }
}
