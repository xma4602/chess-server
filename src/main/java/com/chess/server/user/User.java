package com.chess.server.user;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "app_user") // Измените имя таблицы
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(nullable = false, unique = true)
    private String login;
    @Column(nullable = false)
    private String password;
    @Column(columnDefinition = "DEFAULT 100")
    private Integer rating;

    @ManyToMany
    @JoinTable(name = "app_user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "roles_id"))
    private List<Role> roles = new ArrayList<>();

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }
}
