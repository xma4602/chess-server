package com.chess.server.user.auth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Password {

    public static final int MIN_LENGTH = 4;
    private static final int MAX_LENGTH = 20;

    public static String hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }

    public static boolean equals(String enteredPassword, String savedPassword) {
        return hash(enteredPassword).equals(savedPassword);
    }

    public static void validate(String password) throws IllegalPasswordException {
        if (password.length() < MIN_LENGTH) {
            throw new IllegalPasswordException("Пароль должен состоять как минимум из %d символов".formatted(MIN_LENGTH));
        }
        if (password.length() > MAX_LENGTH) {
            throw new IllegalPasswordException("Пароль должен состоять должен быть не больше %d символов".formatted(MAX_LENGTH));
        }
    }

    public static class IllegalPasswordException extends Exception {
        public IllegalPasswordException(String message) {
            super(message);
        }
    }
}
