package com.chess.engine.exceptions;

public class ChessEngineIllegalArgumentException extends Exception {
    public ChessEngineIllegalArgumentException(String message) {
        super(message);
    }

    public ChessEngineIllegalArgumentException(Throwable e) {
        super(e);
    }
}
