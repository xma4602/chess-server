package com.chess.server.statistic;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StatisticRepository extends JpaRepository<GameStatistic, UUID> {
    List<GameStatistic> getGameStatisticsByCreatorIdOrOpponentId(UUID creatorId, UUID opponentId);
}
