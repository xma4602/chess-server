package com.chess.server.gameplay;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameActionDto {
    private String actionNotation;
    private String actionType;
    private String startPosition;
    private String endPosition;
    private String eatenPosition;
    private String figureCode;
    private String kingStartPosition;
    private String rookStartPosition;
    private String kingEndPosition;
    private String rookEndPosition;
}
