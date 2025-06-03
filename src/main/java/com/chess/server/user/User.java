package com.chess.server.user;

import com.chess.server.gamehistory.GameHistory;
import com.chess.server.gameplay.GamePlay;
import com.chess.server.gameroom.GameRoom;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "app_user") // Измените имя таблицы
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(nullable = false, unique = true)
    private String login;
    @Column(nullable = false)
    private String password;
    @Column
    private Integer rating;

    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "app_user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "roles_id"))
    private List<Role> roles = new ArrayList<>();

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "avatar")
    private byte[] avatar;

    @OneToMany(mappedBy = "creator", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<GamePlay> gamePlaysCreator = new LinkedHashSet<>();

    @OneToMany(mappedBy = "opponent", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<GamePlay> gamePlaysOpponent = new LinkedHashSet<>();

    @OneToMany(mappedBy = "activeUser", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<GamePlay> gamePlaysActiveUser = new LinkedHashSet<>();

    @OneToMany(mappedBy = "creator", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<GameHistory> gameHistoriesCreator = new ArrayList<>();

    @OneToMany(mappedBy = "opponent", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<GameHistory> gameHistoriesOpponent = new ArrayList<>();

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
    private List<GameRoom> gameRoomsCreator = new ArrayList<>();

    @OneToMany(mappedBy = "opponent", cascade = CascadeType.ALL)
    private List<GameRoom> gameRoomsOpponent = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getUsername() {
        return login;
    }
}
