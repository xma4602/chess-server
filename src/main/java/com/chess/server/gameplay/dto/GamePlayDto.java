package com.chess.server.gameplay.dto;

import com.chess.engine.GameEngine;
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
    private String creatorLogin;
    private String opponentLogin;
    private GameConditions gameConditions;
    private GameEngine.GameState gameState;
    private List<String > madeActions;
    private List<GameActionDto> whiteActions;
    private List<GameActionDto> blackActions;
    private Map<String, String> figures;
}
