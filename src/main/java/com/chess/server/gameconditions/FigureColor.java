package com.chess.server.gameconditions;

public enum FigureColor {
    BLACK,
    WHITE,
    RANDOM;

    public static FigureColor randomValue() {
        return System.currentTimeMillis() % 2 == 0 ? WHITE : BLACK;
    }
}
