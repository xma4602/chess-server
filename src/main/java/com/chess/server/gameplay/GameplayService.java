package com.chess.server.gameplay;

import com.chess.engine.FigureColor;
import com.chess.engine.FigureType;
import com.chess.engine.GameEngine;
import com.chess.engine.actions.*;
import com.chess.engine.exceptions.ChessEngineIllegalArgumentException;
import com.chess.engine.exceptions.ChessEngineIllegalStateException;
import com.chess.server.gameconditions.GameConditions;
import com.chess.server.gameconditions.GameConditionsRepository;
import com.chess.server.gameplay.dto.GameActionDto;
import com.chess.server.gameplay.dto.GamePlayDto;
import com.chess.server.gameroom.GameRoom;
import com.chess.server.gameroom.GameRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameplayService {

    private final GameConditionsRepository gameConditionsRepository;
    private final GameplayRepository gameplayRepository;
    private final GameRoomService gameRoomService;

    private static final Map<UUID, GameEngine> games = new HashMap<>();

    public GamePlay createGameplay(UUID guestRoomId) {
        GameRoom gameRoom = gameRoomService.getGameRoom(guestRoomId);

        GameConditions gameConditions = gameRoom.getGameConditions();
        if (gameConditions.getFigureColor() == FigureColor.RANDOM) {
            gameConditions.setFigureColor(FigureColor.randomValue());
        }
        gameConditions = gameConditionsRepository.save(gameConditions);
        gameRoom.setGameConditions(gameConditions);

        GamePlay gameplay = gameplayFromRoom(gameRoom);
//        gameRoomService.delete(gameRoom.getId()); todo вернуть в проде
        GamePlay gamePlay = gameplayRepository.save(gameplay);
        games.put(gamePlay.getId(), new GameEngine());

        return gamePlay;
    }

    private GamePlay gameplayFromRoom(GameRoom gameRoom) {
        return GamePlay.builder()
                .creatorId(gameRoom.getCreatorId())
                .creatorLogin(gameRoom.getCreatorLogin())
                .opponentId(gameRoom.getOpponentId())
                .opponentLogin(gameRoom.getOpponentLogin())
                .gameConditions(gameRoom.getGameConditions())
                .build();
    }

    public GamePlayDto makeAction(UUID gameId, UUID userId, String actionString) throws ChessEngineIllegalArgumentException, ChessEngineIllegalStateException {
        GamePlay gameplay = getGameplay(gameId);
        GameEngine gameEngine = games.get(gameId);

        FigureColor activePlayerColor = gameEngine.getActivePlayerColor();

        FigureColor playerColor = gameplay.getCreatorId().equals(userId) ?
                gameplay.getGameConditions().getFigureColor() :
                gameplay.getGameConditions().getFigureColor().reverseColor();
        if (activePlayerColor == playerColor) {
            Action action = Action.parse(actionString, playerColor)
                    .orElseThrow(() -> new ChessEngineIllegalArgumentException("Not valid action: " + actionString));
            gameEngine.makeAction(playerColor, action);
        }

        return toGamePlayDto(gameplay);
    }

    private GamePlay closeGame(GamePlay gameplay) {
        gameplay.setEnded(true);
        return gameplayRepository.save(gameplay);
    }

    public GamePlayDto getGamePlayDto(UUID gameId) {
        GamePlay gameplay = getGameplay(gameId);
        return toGamePlayDto(gameplay);
    }

    private GamePlayDto toGamePlayDto(GamePlay gamePlay) {
        GameEngine gameEngine = games.get(gamePlay.getId());
        List<GameActionDto> whiteActions = gameEngine.getActionsByPlayerColor(FigureColor.WHITE)
                .stream().map(this::toGameActionDto).toList();
        List<GameActionDto> blackActions = gameEngine.getActionsByPlayerColor(FigureColor.BLACK)
                .stream().map(this::toGameActionDto).toList();
        Map<String, String> figures = gameEngine.getBoardState()
                .entrySet().stream().map(this::toFigure).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return GamePlayDto.builder()
                .id(gamePlay.getId())
                .creatorId(gamePlay.getCreatorId())
                .creatorLogin(gamePlay.getCreatorLogin())
                .opponentId(gamePlay.getOpponentId())
                .opponentLogin(gamePlay.getOpponentLogin())
                .gameConditions(gamePlay.getGameConditions())
                .whiteActions(whiteActions)
                .blackActions(blackActions)
                .figures(figures)
                .build();
    }

    private Map.Entry<String, String> toFigure(Map.Entry<String, Map.Entry<FigureType, FigureColor>> stringEntryEntry) {
        FigureType figureType = stringEntryEntry.getValue().getKey();
        FigureColor figureColor = stringEntryEntry.getValue().getValue();
        return Map.entry(
                stringEntryEntry.getKey(),
                figureColor.getId() + "_" + figureType.getId()
        );
    }

    private GameActionDto toGameActionDto(Action action) {
        GameActionDto.GameActionDtoBuilder dtoBuilder = GameActionDto.builder()
                .actionNotation(action.toString())
                .actionType(action.getActionType().name());
        switch (action.getActionType()) {
            case MOVE -> {
                ActionMove actionMove = (ActionMove) action;
                dtoBuilder.startPosition(actionMove.getStartPosition().toString())
                        .endPosition(actionMove.getEndPosition().toString());
            }
            case EAT -> {
                ActionEat actionMove = (ActionEat) action;
                dtoBuilder.startPosition(actionMove.getStartPosition().toString())
                        .eatenPosition(actionMove.getEatenPosition().toString());
            }
            case SWAP -> {
                ActionSwap actionSwap = (ActionSwap) action;
                dtoBuilder.startPosition(actionSwap.getStartPosition().toString())
                        .endPosition(actionSwap.getEndPosition().toString())
                        .figureCode(actionSwap.getSwapType().name());
            }
            case CASTLING -> {
                ActionCastling actionCastling = (ActionCastling) action;
                dtoBuilder.kingStartPosition(actionCastling.getKingStartPosition().toString())
                        .kingEndPosition(actionCastling.getKingEndPosition().toString())
                        .rookStartPosition(actionCastling.getRookStartPosition().toString())
                        .rookEndPosition(actionCastling.getRookEndPosition().toString());
            }
            case TAKING -> {
                ActionTaking actionTaking = (ActionTaking) action;
                dtoBuilder.startPosition(actionTaking.getStartPosition().toString())
                        .endPosition(actionTaking.getEndPosition().toString())
                        .eatenPosition(actionTaking.getEatenPosition().toString());
            }
        }
        return dtoBuilder.build();
    }

    public GamePlay getGameplay(UUID gameId) {
        Optional<GamePlay> roomOpt = gameplayRepository.findById(gameId);
        return roomOpt.orElseThrow(
                () -> new NoSuchElementException("Активной игры с ID(%s) не существует".formatted(gameId))
        );
    }

}
