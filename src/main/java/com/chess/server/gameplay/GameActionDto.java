package com.chess.server.gameplay;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameActionDto {
    private String codeNotation;
    private String algebraicNotation;
    private String actionType;
    private String startPosition;
    private String endPosition;
    private String eatenPosition;
    private String figureCode;
}
