package com.chess.server.user;

import com.chess.server.user.auth.Login;
import com.chess.server.user.auth.Password;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountException;
import javax.security.auth.login.AccountNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private static final Integer DEFAULT_RATING = 1600;
    private final static String DEFAULT_AVATAR_PATH = "classpath:static/default_avatar.png"; // Путь к стандартному аватару
    public static final String PLAYER = "player";

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ResourceLoader resourceLoader;

    public UserDto singUp(String login, String password) throws Password.IllegalPasswordException, Login.IllegalLoginException, AccountException, IOException {
        Password.validate(password);
        Login.validate(login);
        if (userRepository.existsUserByLogin(login)) {
            throw new AccountException("Duplicate user login=" + login);
        }

        User user = new User();
        user.setLogin(login);
        user.setPassword(passwordEncoder.encode(password));
        user.setRating(DEFAULT_RATING);
        user.setRoles(List.of(getDefaultRole()));
        user.setAvatar(getDefaultAvatar());

        user = userRepository.save(user);
        return toUserDto(user);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toUserDto)
                .toList();
    }

    public UserDto updateUser(UUID userId, UserDto userData) throws AccountNotFoundException {
        User user = findById(userId)
                .orElseThrow(() -> new AccountNotFoundException("No such user with id=" + userId));

        if (userData.getLogin() != null) {
            user.setLogin(userData.getLogin());
        }
        if (userData.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userData.getPassword()));
        }
        if (userData.getAvatar() != null) {
            user.setAvatar(userData.getAvatar());
        }
        if (userData.getRating() != null) {
            user.setRating(userData.getRating());
        }
        if (userData.getRoles() != null) {
            List<Role> roleList = userData.getRoles().stream()
                    .map(roleRepository::findByName)
                    .map(Optional::orElseThrow)
                    .collect(Collectors.toList());
            user.setRoles(roleList);
        }

        user = userRepository.save(user);
        return toUserDto(user);
    }

    public UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .login(user.getLogin())
                .rating(user.getRating())
                .roles(user.getRoles().stream().map(Role::getName).toList())
                .build();
    }

    public void deleteUser(UUID userId) throws AccountNotFoundException {
        User user = findById(userId)
                .orElseThrow(() -> new AccountNotFoundException("No such user with id=" + userId));
        userRepository.delete(user);
    }

    public List<String> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(Role::getName)
                .toList();
    }

    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("No such user with username=" + username));
    }

    public Optional<User> findByLogin(String username) {
        return userRepository.findByLogin(username);
    }

    private Role getDefaultRole() {
        return roleRepository.findByName(PLAYER).get();
    }

    private byte[] getDefaultAvatar() throws IOException {
        return resourceLoader.getResource(DEFAULT_AVATAR_PATH).getContentAsByteArray();
    }
}


