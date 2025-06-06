package com.chess.server.gameplay;

import com.chess.engine.FigureColor;
import com.chess.engine.FigureType;
import com.chess.engine.GameEngine;
import com.chess.engine.GameState;
import com.chess.engine.actions.*;
import com.chess.engine.exceptions.ChessEngineIllegalArgumentException;
import com.chess.engine.exceptions.ChessEngineIllegalStateException;
import com.chess.server.chat.GameChatService;
import com.chess.server.gameconditions.GameConditions;
import com.chess.server.gameconditions.GameConditionsRepository;
import com.chess.server.gamehistory.GameHistoryService;
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
    private final GameChatService gameChatService;
    private final GameHistoryService gameHistoryService;

    public GamePlay createGameplay(UUID guestRoomId) {
        GameRoom gameRoom = gameRoomService.getGameRoom(guestRoomId);

        GameConditions gameConditions = gameRoom.getGameConditions();
        if (gameConditions.getCreatorFigureColor() == FigureColor.RANDOM) {
            gameConditions.setCreatorFigureColor(FigureColor.randomValue());
        }
        gameConditions = gameConditionsRepository.save(gameConditions);
        gameRoom.setGameConditions(gameConditions);

        GamePlay gameplay = gameplayFromRoom(gameRoom);

        if (gameplay.getGameConditions().getCreatorFigureColor() == FigureColor.WHITE) {
            gameplay.setActiveUser(gameplay.getCreator());
        } else {
            gameplay.setActiveUser(gameplay.getOpponent());
        }

        gameplay.setGameEngine(new GameEngine());
//        gameRoomService.delete(gameRoom.getId()); todo вернуть в проде
        gameplay = gameplayRepository.save(gameplay);

        gameChatService.createChat(gameplay);

        return gameplay;
    }

    private GamePlay gameplayFromRoom(GameRoom gameRoom) {
        return GamePlay.builder()
                .creator(gameRoom.getCreator())
                .opponent(gameRoom.getOpponent())
                .gameConditions(gameRoom.getGameConditions())
                .build();
    }

    public GamePlayDto makeAction(UUID gameId, UUID userId, String actionString) throws ChessEngineIllegalArgumentException, ChessEngineIllegalStateException {
        GamePlay gameplay = getGameplay(gameId);

        FigureColor activePlayerColor = gameplay.getGameEngine().getActivePlayerColor();

        FigureColor playerColor = getPlayerColor(userId, gameplay);
        if (activePlayerColor == playerColor) {
            gameplay.getGameEngine().makeAction(playerColor, actionString);

            if (gameplay.getActiveUser().getId().equals(gameplay.getCreator().getId())) {
                gameplay.setActiveUser(gameplay.getOpponent());
            } else {
                gameplay.setActiveUser(gameplay.getCreator());
            }

            gameplay = gameplayRepository.save(gameplay);
            checkGameOver(gameplay);
        }

        return toGamePlayDto(gameplay);
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

        List<String> madeActions = gameEngine.getMadeActions().stream()
                .map(Action::getAlgebraicNotation)
                .collect(Collectors.toList());

        return GamePlayDto.builder()
                .id(gamePlay.getId())
                .chatId(gamePlay.getGameChat().getId())
                .creatorId(gamePlay.getCreator().getId())
                .creatorLogin(gamePlay.getCreator().getLogin())
                .activeUserId(gamePlay.getActiveUser().getId())
                .opponentId(gamePlay.getOpponent().getId())
                .opponentLogin(gamePlay.getOpponent().getLogin())
                .creatorRating(gamePlay.getCreator().getRating())
                .opponentRating(gamePlay.getOpponent().getRating())
                .gameState(gameEngine.getGameState())
                .gameConditions(gamePlay.getGameConditions())
                .madeActions(madeActions)
                .whiteActions(whiteActions)
                .blackActions(blackActions)
                .figures(figures)
                .build();
    }

    private GameActionDto toGameActionDto(Action action) {
        GameActionDto.GameActionDtoBuilder actionDtoBuilder = GameActionDto.builder()
                .codeNotation(action.getCodeNotation())
                .algebraicNotation(action.getAlgebraicNotation())
                .actionType(action.getActionType().name());
        switch (action.getActionType()) {
            case MOVE -> {
                ActionMove actionMove = (ActionMove) action;
                actionDtoBuilder
                        .startPosition(actionMove.getStartPosition().toString())
                        .endPosition(actionMove.getEndPosition().toString());
            }
            case EAT -> {
                ActionEat actionMove = (ActionEat) action;
                actionDtoBuilder
                        .startPosition(actionMove.getStartPosition().toString())
                        .eatenPosition(actionMove.getEatenPosition().toString());
            }
            case SWAP -> {
                ActionSwap actionSwap = (ActionSwap) action;
                actionDtoBuilder
                        .startPosition(actionSwap.getStartPosition().toString())
                        .endPosition(actionSwap.getEndPosition().toString())
                        .figureCode(actionSwap.getSwapType().name());
            }
            case CASTLING -> {
                ActionCastling actionCastling = (ActionCastling) action;
                actionDtoBuilder
                        .startPosition(actionCastling.getKingStartPosition().toString())
                        .endPosition(actionCastling.getKingEndPosition().toString());
            }
            case TAKING -> {
                ActionTaking actionTaking = (ActionTaking) action;
                actionDtoBuilder
                        .startPosition(actionTaking.getStartPosition().toString())
                        .endPosition(actionTaking.getEndPosition().toString())
                        .eatenPosition(actionTaking.getEatenPosition().toString());
            }
        }
        return actionDtoBuilder.build();
    }

    public GamePlay getGameplay(UUID gameId) {
        Optional<GamePlay> roomOpt = gameplayRepository.findById(gameId);
        return roomOpt.orElseThrow(
                () -> new NoSuchElementException("Активной игры с ID(%s) не существует".formatted(gameId))
        );
    }

    public synchronized Optional<GamePlayDto> timeout(UUID gameId, UUID userId) {
        GamePlay gameplay = getGameplay(gameId);
        FigureColor playerColor = getPlayerColor(userId, gameplay);

        if (gameplay.getGameEngine().getGameState() == GameState.CONTINUES) {
            gameplay.getGameEngine().makeTimeOut(playerColor);
            checkGameOver(gameplay);
            return Optional.of(toGamePlayDto(gameplay));
        } else {
            return Optional.empty();
        }
    }

    private static FigureColor getPlayerColor(UUID userId, GamePlay gameplay) {
        return gameplay.getCreator().getId().equals(userId) ?
                gameplay.getGameConditions().getCreatorFigureColor() :
                gameplay.getGameConditions().getCreatorFigureColor().reverseColor();
    }

    public UUID drawRequest(UUID gameId, UUID userId) {
        GamePlay gameplay = getGameplay(gameId);
        return gameplay.getCreator().getId().equals(userId) ?
                gameplay.getOpponent().getId() : gameplay.getCreator().getId();
    }

    public Object drawResponse(UUID gameId, UUID userId, boolean result) {
        GamePlay gameplay = getGameplay(gameId);

        if (result) {
            gameplay.getGameEngine().makeDraw();
            checkGameOver(gameplay);

            return toGamePlayDto(gameplay);
        } else {
            return gameplay.getCreator().getId().equals(userId) ?
                    gameplay.getOpponent().getId() : gameplay.getCreator().getId();
        }
    }

    public GamePlayDto resign(UUID gameId, UUID userId) {
        GamePlay gameplay = getGameplay(gameId);
        FigureColor playerColor = getPlayerColor(userId, gameplay);
        gameplay.getGameEngine().makeResign(playerColor);

        gameplay = gameplayRepository.save(gameplay);

        checkGameOver(gameplay);

        return toGamePlayDto(gameplay);
    }

    private void checkGameOver(GamePlay gamePlay) {
        if (gamePlay.getGameEngine().getGameState() != GameState.CONTINUES) {
            gameHistoryService.createGameHistory(gamePlay);
        }
    }


}
