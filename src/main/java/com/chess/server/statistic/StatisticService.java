package com.chess.server.statistic;

import com.chess.server.gameplay.GamePlay;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StatisticService {
    private final StatisticRepository statisticRepository;

    public void saveStatisticGame(GamePlay gameplay, UUID userId, GameResult gameResult) {
        long duration = getDuration(gameplay.getStartDateTime());
        GameResult gameResultForCreator = getGameResultForCreator(gameplay.getCreatorId(), userId, gameResult);
        GameStatistic gameStatistic = new GameStatistic(
                gameplay.getId(),
                gameplay.getCreatorId(),
                gameplay.getOpponentId(),
                duration,
                gameResultForCreator
        );
        statisticRepository.save(gameStatistic);
    }

    private static long getDuration(LocalDateTime startDateTime) {
        return Duration.between(startDateTime, LocalDateTime.now()).toSeconds();
    }

    private GameResult getGameResultForCreator(UUID creatorId, UUID userId, GameResult gameState) {
        return creatorId.equals(userId) ? gameState : gameState.inverse();
    }

    public List<GameStatistic> getStatistics(UUID userId) {
        return statisticRepository.getGameStatisticsByCreatorIdOrOpponentId(userId, userId);
    }
}
