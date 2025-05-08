package com.chess.server.gamehistory;

import com.chess.engine.GameState;
import com.chess.server.gameconditions.GameConditions;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameHistoryDto {

    private UUID id;
    private String creatorLogin;
    private String opponentLogin;
    private Integer creatorRating;
    private Integer opponentRating;
    private Integer creatorRatingDifference;
    private Integer opponentRatingDifference;
    private GameState gameState;
    private GameConditions gameConditions;
    private String timestamp;

}
