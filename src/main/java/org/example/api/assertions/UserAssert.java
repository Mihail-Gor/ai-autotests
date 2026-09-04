package org.example.api.assertions;

import org.assertj.core.api.AbstractAssert;
import org.example.api.models.user.UserDto;

import java.util.Objects;

public class UserAssert extends AbstractAssert<UserAssert, UserDto> {

    public UserAssert(UserDto actual) {
        super(actual, UserAssert.class);
    }

    public static UserAssert assertThatUser(UserDto actual) {
        return new UserAssert(actual);
    }

    public UserAssert hasId(Integer expectedId) {
        isNotNull();
        if (!Objects.equals(actual.getId(), expectedId)) {
            failWithMessage("Expected user id to be <%s> but was <%s>", expectedId, actual.getId());
        }
        return this;
    }

    public UserAssert hasFirstName(String expectedFirstName) {
        isNotNull();
        if (!Objects.equals(actual.getFirstName(), expectedFirstName)) {
            failWithMessage("Expected user firstName to be <%s> but was <%s>", expectedFirstName, actual.getFirstName());
        }
        return this;
    }

    public UserAssert hasLastName(String expectedLastName) {
        isNotNull();
        if (!Objects.equals(actual.getLastName(), expectedLastName)) {
            failWithMessage("Expected user lastName to be <%s> but was <%s>", expectedLastName, actual.getLastName());
        }
        return this;
    }

    public UserAssert hasEmail(String expectedEmail) {
        isNotNull();
        if (!Objects.equals(actual.getEmail(), expectedEmail)) {
            failWithMessage("Expected user email to be <%s> but was <%s>", expectedEmail, actual.getEmail());
        }
        return this;
    }

    public UserAssert isDeleted() {
        isNotNull();
        if (!Boolean.TRUE.equals(actual.getIsDeleted())) {
            failWithMessage("Expected user to be marked as deleted, but isDeleted was <%s>", actual.getIsDeleted());
        }
        if (actual.getDeletedOn() == null || actual.getDeletedOn().isBlank()) {
            failWithMessage("Expected user deletion timestamp (deletedOn) to be present, but was empty");
        }
        return this;
    }

    public UserAssert matchesCreatedRequest(UserDto request) {
        isNotNull();
        if (request.getFirstName() != null) {
            hasFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            hasLastName(request.getLastName());
        }
        if (request.getEmail() != null) {
            hasEmail(request.getEmail());
        }
        return this;
    }
}
