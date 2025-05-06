package com.chess.server.gamehostory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GameHistoryRepository extends JpaRepository<GameHistory, UUID> {

    List<GameHistory> getGameHistoriesByCreatorIdOrOpponentId(UUID creatorId, UUID opponentId);
}