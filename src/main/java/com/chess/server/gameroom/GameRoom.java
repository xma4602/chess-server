package com.chess.server.gameroom;

import com.chess.server.gameconditions.FigureColor;
import com.chess.server.gameconditions.GameConditions;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GameRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID creatorId;
    private UUID opponentId;
    private String creatorLogin;
    private String opponentLogin;
    @OneToOne(cascade = CascadeType.ALL)
    private GameConditions gameConditions;

    public GameRoom(UUID creatorId, String creatorLogin, GameConditions gameConditions) {
        this.creatorId = creatorId;
        this.creatorLogin = creatorLogin;
        this.gameConditions = gameConditions;
    }

    public int getMoveTime() {
        return gameConditions.getMoveTime();
    }

    public FigureColor getCreatorFigureColor() {
        return gameConditions.getFigureColor();
    }

}
