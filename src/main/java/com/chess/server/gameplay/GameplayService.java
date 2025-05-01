package com.chess.server.gameplay;

import com.chess.engine.FigureColor;
import com.chess.engine.FigureType;
import com.chess.engine.GameEngine;
import com.chess.engine.actions.*;
import com.chess.engine.exceptions.ChessEngineIllegalArgumentException;
import com.chess.engine.exceptions.ChessEngineIllegalStateException;
import com.chess.server.chat.GameChatService;
import com.chess.server.gameconditions.GameConditions;
import com.chess.server.gameconditions.GameConditionsRepository;
import com.chess.server.gameconditions.MatchMode;
import com.chess.server.gameplay.dto.GameActionDto;
import com.chess.server.gameplay.dto.GamePlayDto;
import com.chess.server.gameroom.GameRoom;
import com.chess.server.gameroom.GameRoomService;
import com.chess.server.user.User;
import com.chess.server.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameplayService {

    public static final int RATING = 100;
    private final GameConditionsRepository gameConditionsRepository;
    private final GameplayRepository gameplayRepository;
    private final GameRoomService gameRoomService;
    private final GameChatService gameChatService;
    private final UserRepository userRepository;

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
            Action action = Action.parse(actionString, playerColor)
                    .orElseThrow(() -> new ChessEngineIllegalArgumentException("Not valid action: " + actionString));
            gameplay.getGameEngine().makeAction(playerColor, action);

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
                .map(Action::toString)
                .toList();

        return GamePlayDto.builder()
                .id(gamePlay.getId())
                .chatId(gamePlay.getGameChat().getId())
                .creatorId(gamePlay.getCreator().getId())
                .creatorLogin(gamePlay.getCreator().getLogin())
                .activeUserId(gamePlay.getActiveUser().getId())
                .opponentId(gamePlay.getOpponent().getId())
                .opponentLogin(gamePlay.getOpponent().getLogin())
                .gameState(gameEngine.getGameState())
                .gameConditions(gamePlay.getGameConditions())
                .madeActions(madeActions)
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

    public synchronized Optional<GamePlayDto> timeout(UUID gameId, UUID userId) {
        GamePlay gameplay = getGameplay(gameId);
        FigureColor playerColor = getPlayerColor(userId, gameplay);

        if (gameplay.getGameEngine().getGameState() == GameEngine.GameState.CONTINUES) {
            gameplay.getGameEngine().setGameState(playerColor == FigureColor.WHITE ?
                    GameEngine.GameState.BLACK_WIN : GameEngine.GameState.WHITE_WIN);
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
            gameplay.getGameEngine().setGameState(GameEngine.GameState.DRAW);
            checkGameOver(gameplay);

            return toGamePlayDto(gameplay);
        } else {
            return gameplay.getCreator().getId().equals(userId) ?
                    gameplay.getOpponent().getId() : gameplay.getCreator().getId();
        }
    }

    public GamePlayDto surrender(UUID gameId, UUID userId) {
        GamePlay gameplay = getGameplay(gameId);

        FigureColor playerColor = getPlayerColor(userId, gameplay);

        gameplay.getGameEngine().setGameState(playerColor == FigureColor.WHITE ?
                GameEngine.GameState.BLACK_WIN : GameEngine.GameState.WHITE_WIN);

        gameplay = gameplayRepository.save(gameplay);

        checkGameOver(gameplay);

        return toGamePlayDto(gameplay);
    }

    private void checkGameOver(GamePlay gamePlay) {
        if (gamePlay.getGameEngine().getGameState() != GameEngine.GameState.CONTINUES) {
            if (gamePlay.getGameConditions().getMatchMode() == MatchMode.RATING) {
                User creator = gamePlay.getCreator();
                User opponent = gamePlay.getOpponent();

                Integer creatorRating = creator.getRating();
                Integer opponentRating = opponent.getRating();

                boolean creatorWhiteWin = gamePlay.getGameConditions().getCreatorFigureColor() == FigureColor.WHITE
                        && gamePlay.getGameEngine().getGameState() == GameEngine.GameState.WHITE_WIN;
                boolean creatorBlackWin = gamePlay.getGameConditions().getCreatorFigureColor() == FigureColor.BLACK
                        && gamePlay.getGameEngine().getGameState() == GameEngine.GameState.BLACK_WIN;
                if (creatorWhiteWin || creatorBlackWin) {
                    creatorRating += RATING;
                    opponentRating = Math.max(opponentRating - RATING, 0);
                } else {
                    opponentRating += RATING;
                    creatorRating = Math.max(creatorRating - RATING, 0);
                }

                creator.setRating(creatorRating);
                opponent.setRating(opponentRating);

                userRepository.saveAll(List.of(creator, opponent));
            }
        }
    }
}
