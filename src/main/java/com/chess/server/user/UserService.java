package com.chess.server.user;

import com.chess.server.user.auth.Login;
import com.chess.server.user.auth.Password;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountException;
import javax.security.auth.login.AccountNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserDto singIn(String login, String password) throws AuthException, AccountNotFoundException {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new AccountNotFoundException("Не существует пользователя с логином " + login));

        if (!password.equals(user.getPassword())) {
            throw new AuthException("Неверное имя и/или логин пользователя");
        }
        return toUserDto(user);
    }

    public UserDto singUp(String login, String password) throws Password.IllegalPasswordException, Login.IllegalLoginException, AccountException {
        Password.validate(password);
        Login.validate(login);
        if (userRepository.existsUserByLogin(login)) {
            throw new AccountException("Duplicate user login=" + login);
        }

//        password = Password.hash(password);
        User user = new User(login, password);
        user = userRepository.save(user);
        return toUserDto(user);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toUserDto)
                .toList();
    }

    public UserDto updateUser(UUID userId, UserDto userData) throws AccountNotFoundException {
        User user = getUser(userId);

        user.setLogin(userData.getLogin());
        user.setPassword(userData.getPassword());
        user.setAvatar(userData.getAvatar());

        if (userData.getRating() != null){
            user.setRating(userData.getRating());
        }

        if (userData.getRoles() != null){
            List<Role> roleList = userData.getRoles().stream()
                    .map(roleRepository::findByName)
                    .map(Optional::orElseThrow)
                    .collect(Collectors.toList());
            user.setRoles(roleList);
        }

        user = userRepository.save(user);
        return toUserDto(user);
    }

    private UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .login(user.getLogin())
                .password(user.getPassword())
                .rating(user.getRating())
                .roles(user.getRoles().stream().map(Role::getName).toList())
                .build();
    }

    public void deleteUser(UUID userId) throws AccountNotFoundException {
        User user = getUser(userId);
        userRepository.delete(user);
    }

    private User getUser(UUID userId) throws AccountNotFoundException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new AccountNotFoundException("Не существует пользователя с id " + userId));
    }

    public List<String> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(Role::getName)
                .toList();
    }

    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }
}


