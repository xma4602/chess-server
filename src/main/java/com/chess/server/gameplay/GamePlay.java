package com.chess.server.gameplay;

import com.chess.engine.GameEngine;
import com.chess.server.chat.GameChat;
import com.chess.server.gameconditions.GameConditions;
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

    private UUID creatorId;

    private UUID opponentId;

    private UUID activeUserId;

    private String creatorLogin;

    private String opponentLogin;

    @Builder.Default
    private LocalDateTime startDateTime = LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private boolean ended = false;

    @OneToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "game_conditions_id")
    private GameConditions gameConditions;

    @Convert(converter = GameEngineConverter.class)
    private GameEngine gameEngine;

    @OneToOne(mappedBy = "gamePlay", optional = false, orphanRemoval = true)
    private GameChat gameChat;

}
