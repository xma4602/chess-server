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

    public GamePlay createGameplay(UUID guestRoomId) {
        GameRoom gameRoom = gameRoomService.getGameRoom(guestRoomId);

        GameConditions gameConditions = gameRoom.getGameConditions();
        if (gameConditions.getFigureColor() == FigureColor.RANDOM) {
            gameConditions.setFigureColor(FigureColor.randomValue());
        }
        gameConditions = gameConditionsRepository.save(gameConditions);
        gameRoom.setGameConditions(gameConditions);

        GamePlay gameplay = gameplayFromRoom(gameRoom);
        gameplay.setGameEngine(new GameEngine());
//        gameRoomService.delete(gameRoom.getId()); todo вернуть в проде
        return gameplayRepository.save(gameplay);
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

        FigureColor activePlayerColor = gameplay.getGameEngine().getActivePlayerColor();

        FigureColor playerColor = gameplay.getCreatorId().equals(userId) ?
                gameplay.getGameConditions().getFigureColor() :
                gameplay.getGameConditions().getFigureColor().reverseColor();
        if (activePlayerColor == playerColor) {
            Action action = Action.parse(actionString, playerColor)
                    .orElseThrow(() -> new ChessEngineIllegalArgumentException("Not valid action: " + actionString));
            gameplay.getGameEngine().makeAction(playerColor, action);
            gameplay = gameplayRepository.save(gameplay);
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
        GameEngine gameEngine = gamePlay.getGameEngine();

        List<GameActionDto> whiteActions = new ArrayList<>();
        if (gameEngine.getActivePlayerColor() == FigureColor.WHITE) {
            whiteActions = gameEngine.getActionsByPlayerColor(FigureColor.WHITE)
                    .stream().map(this::toGameActionDto).toList();
        }

        List<GameActionDto> blackActions = new ArrayList<>();
        if (gameEngine.getActivePlayerColor() == FigureColor.BLACK) {
            blackActions = gameEngine.getActionsByPlayerColor(FigureColor.BLACK)
                    .stream().map(this::toGameActionDto).toList();
        }

        Map<String, String> figures = gameEngine.getBoardState().entrySet().stream()
                .filter(entry -> entry.getValue().getFigureType() != FigureType.NONE)
                .collect(Collectors.toMap(
                        entry -> entry.getKey().toString(),
                        entry -> entry.getValue().getId()));

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
