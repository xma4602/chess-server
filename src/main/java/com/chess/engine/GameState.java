package com.chess.engine;

import java.io.Serializable;

public enum GameState implements Serializable {
    BLACK_WIN,
    WHITE_WIN,
    DRAW,
    CONTINUES
}