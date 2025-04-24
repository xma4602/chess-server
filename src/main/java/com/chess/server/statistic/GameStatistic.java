package com.chess.server.statistic;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class GameStatistic {
    @Id
    private UUID gameId;
    private UUID creatorId;
    private UUID opponentId;
    private String creatorLogin;
    private String opponentLogin;
    private GameResult creatorResult;
    private String date;
    private long duration;

    public GameStatistic(UUID gameId, UUID creatorId, UUID opponentId, long duration, GameResult creatorResult) {
        this.gameId = gameId;
        this.creatorId = creatorId;
        this.opponentId = opponentId;
        this.duration = duration;
        this.creatorResult = creatorResult;
        this.date = LocalDate.now().toString();
    }
}
