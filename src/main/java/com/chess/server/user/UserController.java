package com.chess.server.user;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.security.auth.login.AccountNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/chess/users")
@RequiredArgsConstructor
public class UserController {
    private final static String DEFAULT_AVATAR_PATH = "classpath:static/default_avatar.png"; // Путь к стандартному аватару

    private final UserService userService;
    private final ResourceLoader resourceLoader;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String login, @RequestParam String password) {
        try {
            UserDto user = userService.findByLogin(login)
                    .map(userService::toUserDto)
                    .get();
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

    @GetMapping("/{id}/avatar")
    public ResponseEntity<Resource> getUserAvatar(@PathVariable UUID id) {
        Optional<User> userOptional = userService.findById(id);
        Resource avatarResource;

        if (userOptional.isPresent() && userOptional.get().getAvatar() != null) {
            // Если у пользователя есть аватар, возвращаем его
            byte[] avatar = userOptional.get().getAvatar();
            avatarResource = new ByteArrayResource(avatar);
        } else {
            // Если у пользователя нет аватара, возвращаем стандартное изображение
            avatarResource = resourceLoader.getResource(DEFAULT_AVATAR_PATH);
        }

        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<>(avatarResource, headers, HttpStatus.OK);
    }

    @PutMapping("/{userId}/profile")
    public ResponseEntity<UserDto> updateUserProfile(
            @PathVariable UUID userId,
            @RequestParam(value = "login", required = false) String login,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "rating", required = false) Integer rating,
            @RequestParam(value = "roles", required = false) List<String> roles,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar) throws IOException, AccountNotFoundException {

        UserDto userDto = UserDto.builder()
                .id(userId)
                .login(login)
                .password(password == null ? null : password.isEmpty() ? null : password)
                .rating(rating)
                .roles(roles)
                .avatar(avatar != null ? avatar.getBytes() : null)
                .build();

        userDto = userService.updateUser(userId, userDto);

        // Верните обновленного пользователя
        return ResponseEntity.ok(userDto);
    }
}
