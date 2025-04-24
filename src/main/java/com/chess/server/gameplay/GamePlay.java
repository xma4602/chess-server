package com.chess.server.gameplay;

import com.chess.server.gameconditions.GameConditions;
import com.chess.server.gameroom.GameRoom;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GamePlay {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private UUID creatorId;
    private UUID opponentId;
    private String creatorLogin;
    private String opponentLogin;
    private UUID activeUserId;
    private String startDateTime;
    private boolean ended;
    @ManyToOne
    @JoinColumn(name = "game_conditions_id")
    private GameConditions gameConditions;

    public GamePlay(GameRoom gameRoom) {
        this.creatorId = gameRoom.getCreatorId();
        this.opponentId = gameRoom.getOpponentId();
        this.creatorLogin = gameRoom.getCreatorLogin();
        this.opponentLogin = gameRoom.getOpponentLogin();
        this.gameConditions = gameRoom.getGameConditions();
        startDateTime = LocalDateTime.now().toString();
        ended = false;
    }

}
