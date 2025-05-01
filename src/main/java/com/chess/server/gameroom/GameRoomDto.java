package com.chess.server.gameroom;

import com.chess.server.gameconditions.GameConditions;
import lombok.*;

import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameRoomDto {
    private UUID id;
    private UUID creatorId;
    private UUID opponentId;
    private String creatorLogin;
    private String opponentLogin;
    private Integer creatorRating;
    private Integer opponentRating;
    private GameConditions gameConditions;
}
