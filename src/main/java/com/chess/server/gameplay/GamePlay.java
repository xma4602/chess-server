package com.chess.server.gameplay;

import com.chess.engine.GameEngine;
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
    private UUID id;
    private UUID creatorId;
    private UUID opponentId;
    private String creatorLogin;
    private String opponentLogin;
    @Builder.Default
    private LocalDateTime startDateTime = LocalDateTime.now();
    @Builder.Default
    private boolean ended = false;
    @OneToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "game_conditions_id")
    private GameConditions gameConditions;
    @Convert(converter = GameEngineConverter.class)
    private GameEngine gameEngine;
}
