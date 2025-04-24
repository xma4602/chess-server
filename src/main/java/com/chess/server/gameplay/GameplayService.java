package com.chess.server.gameplay;

import com.chess.server.gameconditions.FigureColor;
import com.chess.server.gameconditions.GameConditions;
import com.chess.server.gameconditions.GameConditionsRepository;
import com.chess.server.gameplay.dto.GameEnd;
import com.chess.server.gameroom.GameRoom;
import com.chess.server.gameroom.GameRoomService;
import com.chess.server.statistic.GameResult;
import com.chess.server.statistic.StatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameplayService {

    private final GameConditionsRepository gameConditionsRepository;
    private final GameplayRepository gameplayRepository;
    private final GameRoomService gameRoomService;
    private final StatisticService statisticService;

    public GamePlay createGameplay(UUID guestRoomId) {
        GameRoom gameRoom = gameRoomService.getGameRoom(guestRoomId);

        GameConditions gameConditions = gameRoom.getGameConditions();
        if (gameConditions.getFigureColor() == FigureColor.RANDOM) {
            gameConditions.setFigureColor(FigureColor.randomValue());
            gameRoom.setGameConditions(gameConditionsRepository.save(gameConditions));
        }

        GamePlay gameplay = new GamePlay(gameRoom);
        gameRoomService.delete(gameRoom.getId());
        return gameplayRepository.save(gameplay);
    }

    public UUID makeAction(UUID gameId, UUID userId, String action) {
        GamePlay gameplay = getGameplay(gameId);
        if (!gameplay.hasUser(userId)) {
            throw new IllegalArgumentException("Пользователь ID(%s) не учавствует в игре ID(%s) и не может выполнять действия"
                    .formatted(gameId, userId));
        } else if (!gameplay.isActiveUser(userId)) {
            throw new IllegalArgumentException("Пользователь ID(%s) в игре ID(%s) в данный момент не владеет правом хода"
                    .formatted(gameId, userId));
        } else {
            gameplay.switchActiveUser();
            gameplayRepository.save(gameplay);
            return gameplay.getActiveUserId();
        }
    }

    public GameEnd endGame(UUID gameId, UUID userId, GameResult gameResult) {
        GamePlay gameplay = getGameplay(gameId);
        if (!gameplay.hasUser(userId)) {
            throw new IllegalArgumentException("Пользователь ID(%s) не учавствует в игре ID(%s) и не может выполнять действия"
                    .formatted(gameId, userId));
        }

        gameplay = closeGame(gameplay);
        statisticService.saveStatisticGame(gameplay, userId, gameResult);
        UUID notifiedUserId = gameplay.getCreatorId().equals(userId) ? gameplay.getOpponentId() : gameplay.getCreatorId();

        return new GameEnd(notifiedUserId, gameResult.inverse());

    }

    private GamePlay closeGame(GamePlay gameplay) {
        gameplay.setEnded(true);
        return gameplayRepository.save(gameplay);
    }


    public GamePlay getGameplay(UUID gameId) {
        Optional<GamePlay> roomOpt = gameplayRepository.findById(gameId);
        return roomOpt.orElseThrow(
                () -> new NoSuchElementException("Активной игры с ID(%s) не существует".formatted(gameId))
        );
    }

    public UUID getOtherUserId(UUID gameId, UUID userId) {
        GamePlay gameplay = getGameplay(gameId);
        if (!gameplay.hasUser(userId)) {
            throw new IllegalArgumentException("Пользователь ID(%s) не учавствует в игре ID(%s) и не может выполнять действия"
                    .formatted(gameId, userId));
        }
        return gameplay.getCreatorId().equals(userId) ? gameplay.getOpponentId() : gameplay.getCreatorId();
    }
}
