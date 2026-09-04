package org.example.api.clients;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.example.api.models.user.UserDto;
import org.example.api.models.user.UsersResponseDto;
import org.example.api.specifications.ResponseSpecs;

public class UserClient extends BaseClient {

    private static final String USERS_ENDPOINT = "/users";
    private static final String USER_BY_ID_ENDPOINT = "/users/{id}";
    private static final String USER_SEARCH_ENDPOINT = "/users/search";
    private static final String USER_ADD_ENDPOINT = "/users/add";

    @Step("Get list of all users")
    public UsersResponseDto getAllUsers() {
        return getAllUsersRaw()
                .then()
                .spec(ResponseSpecs.statusOk())
                .extract()
                .as(UsersResponseDto.class);
    }

    @Step("Get users with limit: {limit}, skip: {skip}")
    public UsersResponseDto getUsers(int limit, int skip) {
        return getRequestSpec()
                .queryParam("limit", limit)
                .queryParam("skip", skip)
                .when()
                .get(USERS_ENDPOINT)
                .then()
                .spec(ResponseSpecs.statusOk())
                .extract()
                .as(UsersResponseDto.class);
    }

    @Step("Search users by query: '{query}'")
    public UsersResponseDto searchUsers(String query) {
        return getRequestSpec()
                .queryParam("q", query)
                .when()
                .get(USER_SEARCH_ENDPOINT)
                .then()
                .spec(ResponseSpecs.statusOk())
                .extract()
                .as(UsersResponseDto.class);
    }

    @Step("Send raw GET request for users list")
    public Response getAllUsersRaw() {
        return getRequestSpec()
                .when()
                .get(USERS_ENDPOINT);
    }

    @Step("Get user by ID: {id}")
    public UserDto getUserById(int id) {
        return getUserByIdRaw(id)
                .then()
                .spec(ResponseSpecs.statusOk())
                .extract()
                .as(UserDto.class);
    }

    @Step("Send raw GET request for user with ID: {id}")
    public Response getUserByIdRaw(Object id) {
        return getRequestSpec()
                .pathParam("id", id)
                .when()
                .get(USER_BY_ID_ENDPOINT);
    }

    @Step("Create new user: {user.firstName} {user.lastName}")
    public UserDto createUser(UserDto user) {
        return createUserRaw(user)
                .then()
                .spec(ResponseSpecs.statusCreated())
                .extract()
                .as(UserDto.class);
    }

    @Step("Send raw POST request to add user")
    public Response createUserRaw(Object body) {
        return getRequestSpec()
                .body(body)
                .when()
                .post(USER_ADD_ENDPOINT);
    }

    @Step("Update user with ID: {id} via PUT")
    public UserDto updateUser(int id, UserDto user) {
        return updateUserRaw(id, user)
                .then()
                .spec(ResponseSpecs.statusOk())
                .extract()
                .as(UserDto.class);
    }

    @Step("Send raw PUT request to update user with ID: {id}")
    public Response updateUserRaw(Object id, Object body) {
        return getRequestSpec()
                .pathParam("id", id)
                .body(body)
                .when()
                .put(USER_BY_ID_ENDPOINT);
    }

    @Step("Partially update user with ID: {id} via PATCH")
    public UserDto patchUser(int id, UserDto user) {
        return patchUserRaw(id, user)
                .then()
                .spec(ResponseSpecs.statusOk())
                .extract()
                .as(UserDto.class);
    }

    @Step("Send raw PATCH request to update user with ID: {id}")
    public Response patchUserRaw(Object id, Object body) {
        return getRequestSpec()
                .pathParam("id", id)
                .body(body)
                .when()
                .patch(USER_BY_ID_ENDPOINT);
    }

    @Step("Delete user with ID: {id}")
    public UserDto deleteUser(int id) {
        return deleteUserRaw(id)
                .then()
                .spec(ResponseSpecs.entityDeleted())
                .extract()
                .as(UserDto.class);
    }

    @Step("Send raw DELETE request for user with ID: {id}")
    public Response deleteUserRaw(Object id) {
        return getRequestSpec()
                .pathParam("id", id)
                .when()
                .delete(USER_BY_ID_ENDPOINT);
    }
}
