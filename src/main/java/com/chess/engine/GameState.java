package com.chess.engine;

import java.io.Serializable;

public enum GameState implements Serializable {
    BLACK_WIN_CHECKMATE,
    WHITE_WIN_CHECKMATE,
    DRAW,
    CONTINUES,
    BLACK_WIN_STALEMATE,
    WHITE_WIN_STALEMATE,
    BLACK_WIN_RESIGN,
    WHITE_WIN_RESIGN,
    WHITE_WIN_TIME_OUT,
    BLACK_WIN_TIME_OUT,
}