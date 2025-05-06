package com.chess.server.gamehostory;

import com.chess.engine.FigureColor;
import com.chess.engine.GameState;
import com.chess.server.gameconditions.TimeControl;
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
    private FigureColor creatorFigureColor;
    private TimeControl timeControl;
    private String timestamp;

}
