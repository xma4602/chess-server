package com.chess.server.user;

import com.chess.server.user.auth.Login;
import com.chess.server.user.auth.Password;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/auth")
    public UUID authentication(@RequestParam String login, @RequestParam String password)
            throws AuthException, AccountNotFoundException {
        return userService.singIn(login, password);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UUID registration(@RequestParam String login, @RequestParam String password)
            throws UserService.RegisterException, Password.IllegalPasswordException, Login.IllegalLoginException {
        return userService.singUp(login, password);
    }

    @ExceptionHandler(UserService.RegisterException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    private void handle(UserService.RegisterException e) {
    }

    @ExceptionHandler(Password.IllegalPasswordException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private void handle(Password.IllegalPasswordException e) {
    }

    @ExceptionHandler(Login.IllegalLoginException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private void handle(Login.IllegalLoginException e) {
    }

    @ExceptionHandler(AuthException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private void handle(AuthException e) {
    }

    @ExceptionHandler(AccountNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private void handle(AccountNotFoundException e) {
    }

}
