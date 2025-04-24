package com.chess.server.statistic;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum GameResult {
    WIN("победа"),
    LOSS("поражение"),
    DRAW("ничья");

    private final String result;

    @Override
    public String toString() {
        return result;
    }

    public GameResult inverse() {
        return switch (this){
            case WIN -> LOSS;
            case LOSS -> WIN;
            case DRAW -> DRAW;
        };
    }
}