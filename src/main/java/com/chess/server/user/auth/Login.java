package com.chess.server.user.auth;

public class Login {
    private static final int MIN_LENGTH = 4;
    private static final int MAX_LENGTH = 20;

    public static void validate(String login) throws IllegalLoginException {
        if (login.length() < MIN_LENGTH) {
            throw new IllegalLoginException("Логин должен состоять как минимум из %d символов".formatted(MIN_LENGTH));
        }
        if (login.length() > MAX_LENGTH) {
            throw new IllegalLoginException("Логин должен состоять должен быть не больше %d символов".formatted(MAX_LENGTH));
        }
    }

    public static class IllegalLoginException extends Exception {
        public IllegalLoginException(String message) {
            super(message);
        }
    }
}
