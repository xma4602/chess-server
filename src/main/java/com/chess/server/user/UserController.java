package com.chess.server.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chess/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String login, @RequestParam String password) {
        try {
            User user = userService.singIn(login, password);
            UserDto dto = UserDto.builder()
                    .id(user.getId())
                    .login(user.getLogin())
                    .rating(user.getRating())
                    .roles(user.getRoles().stream().map(Role::getName).toList())
                    .build();
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> registration(@RequestParam String login, @RequestParam String password) {
        try {
            User user = userService.singUp(login, password);
            UserDto dto = UserDto.builder()
                    .id(user.getId())
                    .login(user.getLogin())
                    .rating(user.getRating())
                    .roles(user.getRoles().stream().map(Role::getName).toList())
                    .build();
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
