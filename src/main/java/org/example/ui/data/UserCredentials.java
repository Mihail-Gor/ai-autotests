package org.example.ui.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserCredentials {
    STANDARD_USER("standard_user", "secret_sauce", "Standard valid user"),
    LOCKED_OUT_USER("locked_out_user", "secret_sauce", "Locked out user for negative testing"),
    PROBLEM_USER("problem_user", "secret_sauce", "User encountering broken images and behavior"),
    PERFORMANCE_GLITCH_USER("performance_glitch_user", "secret_sauce", "User experiencing artificial delays"),
    ERROR_USER("error_user", "secret_sauce", "User causing system errors"),
    VISUAL_USER("visual_user", "secret_sauce", "User causing layout glitches");

    private final String username;
    private final String password;
    private final String description;
}
