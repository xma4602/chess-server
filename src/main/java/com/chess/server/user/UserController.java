package com.chess.server.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/chess/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String login, @RequestParam String password) {
        try {
            UserDto user = userService.singIn(login, password);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registration(@RequestParam String login, @RequestParam String password) {
        try {
            UserDto user = userService.singUp(login, password);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/roles")
    public ResponseEntity<List<String>> getAllRoles() {
        List<String> allUsers = userService.getAllRoles();
        return ResponseEntity.ok(allUsers);
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> allUsers = userService.getAllUsers();
        return ResponseEntity.ok(allUsers);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(@RequestBody UserDto user, @PathVariable UUID userId) throws AccountNotFoundException {
        UserDto userDto = userService.updateUser(userId, user);
        return ResponseEntity.ok(userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID userId) throws AccountNotFoundException {
        userService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }
}
