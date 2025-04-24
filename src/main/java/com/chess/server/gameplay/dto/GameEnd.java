package com.chess.server.gameplay.dto;

import com.chess.server.statistic.GameResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameEnd {
    UUID notifiedUserId;
    GameResult gameResult;
}
