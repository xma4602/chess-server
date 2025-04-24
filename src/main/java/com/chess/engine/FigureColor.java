package com.chess.engine;

public enum FigureColor {
    WHITE, BLACK;

    public static final FigureColor DEFAULT = WHITE;

    public boolean isWhite() {
        return this == WHITE;
    }

    public FigureColor reverseColor(){
        return this == WHITE ? BLACK : WHITE;
    }

}
