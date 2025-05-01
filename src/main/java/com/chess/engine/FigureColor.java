package com.chess.engine;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Getter
@RequiredArgsConstructor
public enum FigureColor implements Serializable {

    WHITE("WHITE"),
    BLACK("BLACK"),
    RANDOM("RANDOM");
    @Serial
    private static final long serialVersionUID = 1L;
    public static final FigureColor DEFAULT = WHITE;

    private final String id;
    public boolean isWhite() {
        return this == WHITE;
    }

    public FigureColor reverseColor(){
        return this == WHITE ? BLACK : WHITE;
    }

    public static FigureColor randomValue() {
        return System.currentTimeMillis() % 2 == 0 ? WHITE : BLACK;
    }

}
