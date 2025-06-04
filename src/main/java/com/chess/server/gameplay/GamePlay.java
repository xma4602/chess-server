package com.chess.server.gameplay;

import com.chess.engine.GameEngine;
import com.chess.server.chat.GameChat;
import com.chess.server.gameconditions.GameConditions;
import com.chess.server.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GamePlay implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @ManyToOne
    @JoinColumn(name = "opponent_id")
    private User opponent;

    @ManyToOne
    @JoinColumn(name = "active_user_id")
    private User activeUser;

    @Builder.Default
    private LocalDateTime startDateTime = LocalDateTime.now();

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(name = "game_conditions_id", nullable = false)
    private GameConditions gameConditions;

    @Convert(converter = GameEngineConverter.class)
    private GameEngine gameEngine;

    @OneToOne(mappedBy = "gamePlay", cascade = CascadeType.ALL)
    private GameChat gameChat;

}
