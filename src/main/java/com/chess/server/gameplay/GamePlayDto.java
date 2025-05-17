package com.chess.server.gameplay;

import com.chess.engine.GameState;
import com.chess.server.gameconditions.GameConditions;
import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GamePlayDto {
    private UUID id;
    private UUID chatId;
    private UUID creatorId;
    private UUID opponentId;
    private UUID activeUserId;
    private String creatorLogin;
    private String opponentLogin;
    private Integer creatorRating;
    private Integer opponentRating;
    private GameConditions gameConditions;
    private GameState gameState;
    private List<String> madeActions;
    private List<GameActionDto> whiteActions;
    private List<GameActionDto> blackActions;
    private Map<String, String> figures;
}
