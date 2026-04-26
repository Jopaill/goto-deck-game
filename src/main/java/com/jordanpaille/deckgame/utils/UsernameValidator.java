package com.jordanpaille.deckgame.utils;

public final class UsernameValidator {
    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MAX_USERNAME_LENGTH = 32;
    private static final String USERNAME_PATTERN = "^[A-Za-z0-9_-]+$";

    private UsernameValidator() {
    }

    public static String validate(String username) {
        if (username == null) {
            return "Username cannot be null";
        }

        String trimmedUsername = username.trim();

        if (trimmedUsername.isEmpty()) {
            return "Username cannot be blank";
        }

        if (trimmedUsername.length() < MIN_USERNAME_LENGTH) {
            return "Username must be at least " + MIN_USERNAME_LENGTH + " characters long";
        }

        if (trimmedUsername.length() > MAX_USERNAME_LENGTH) {
            return "Username cannot be longer than " + MAX_USERNAME_LENGTH + " characters";
        }

        if (!trimmedUsername.matches(USERNAME_PATTERN)) {
            return "Username may only contain letters, numbers, underscores, and hyphens";
        }

        return null;
    }
}