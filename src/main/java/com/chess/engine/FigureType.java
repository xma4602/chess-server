package com.chess.engine;

import com.chess.engine.exceptions.ChessEngineRuntimeException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public enum FigureType {
    NONE(0, 'X', '◼', '◻', "Пусто"),
    PAWN(1, 'P', '♟', '♙', "Пешка"),
    KNIGHT(2, 'N', '♞', '♘', "Конь"),
    BISHOP(3, 'B', '♝', '♗', "Слон"),
    ROOK(4, 'R', '♜', '♖', "Ладья"),
    QUEEN(5, 'Q', '♛', '♕', "Ферзь"),
    KING(6, 'K', '♚', '♔', "Король");

    private static final List<FigureType> swapTypes = List.of(KNIGHT, BISHOP, ROOK, QUEEN);

    private final int code;
    private final char notationChar;
    private final char imageWhiteChar;
    private final char imageBlackChar;
    private final String name;

    public char getImageChar(FigureColor figureColor) {
        return figureColor.isWhite() ? imageWhiteChar : imageBlackChar;
    }

    public static FigureType getTypeByCode(int code) {
        if (code < 0 || code > 6) {
            throw new ChessEngineRuntimeException("Не найдено типа фигуры с кодом: " + code);
        }

        return FigureType.values()[code];
    }

    public static FigureType getTypeByNotationChar(char notationChar) {
        for (var figure : FigureType.values()) {
            if (figure.notationChar == notationChar) {
                return figure;
            }
        }
        throw new ChessEngineRuntimeException("Не найдено типа фигуры с символом: " + notationChar);
    }

    public static FigureType getTypeByImageChar(char imageChar) {
        for (var figure : FigureType.values()) {
            if (figure.imageWhiteChar == imageChar || figure.imageBlackChar == imageChar) {
                return figure;
            }
        }
        throw new ChessEngineRuntimeException("Не найдено типа фигуры с символом: " + imageChar);
    }

    public static boolean getColorByImageChar(char imageChar) {
        for (var figure : FigureType.values()) {
            if (figure.imageWhiteChar == imageChar) {
                return true;
            }
            if (figure.imageBlackChar == imageChar) {
                return false;
            }
        }
        throw new ChessEngineRuntimeException("Не найдено типа фигуры с символом: " + imageChar);
    }

    @Override
    public String toString() {
        return getName();
    }
}
