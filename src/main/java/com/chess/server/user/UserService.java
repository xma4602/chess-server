package com.chess.server.user;

import com.chess.server.user.auth.Login;
import com.chess.server.user.auth.Password;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.experimental.StandardException;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UUID singIn(String login, String password) throws AuthException, AccountNotFoundException {
        Optional<User> userOpt = userRepository.findByLogin(login);
        User user = userOpt.orElseThrow(() -> new AccountNotFoundException("Не сущестувет пользователя с логином " + login));
//        if (!Password.equals(password, user.getPassword())) {
//            throw new AuthException("Неверное имя и/или логин пользователя");
//        }
        if (!password.equals(user.getPassword())) {
            throw new AuthException("Неверное имя и/или логин пользователя");
        }
        return user.getId();
    }

    public UUID singUp(String login, String password) throws Password.IllegalPasswordException, Login.IllegalLoginException, RegisterException {
        Password.validate(password);
        Login.validate(login);
        if (userRepository.existsUserByLogin(login)) {
            throw RegisterException.duplicateUser(login);
        }

//        password = Password.hash(password);
        User user = new User(login, password);
        user = userRepository.save(user);
        return user.getId();
    }

    public String getUserLogin(UUID userId) {
        return userRepository.findById(userId).map(User::getLogin).orElseThrow(
                () -> new NoSuchElementException("Не существует пользователя с ID(%s)".formatted(userId))
        );
    }

    @StandardException
    public static class RegisterException extends Exception {
        public static RegisterException duplicateUser(String login) {
            return new RegisterException("Пользователь с логином \"%s\" уже существует".formatted(login));
        }
    }
}
