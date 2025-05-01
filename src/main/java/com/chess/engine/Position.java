package com.chess.engine;

import com.chess.engine.exceptions.ChessEngineRuntimeException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum Position implements Serializable {

    A1("a1", 0, 0, 0, false),
    B1("b1", 0, 1, 1, true),
    C1("c1", 0, 2, 2, false),
    D1("d1", 0, 3, 3, true),
    E1("e1", 0, 4, 4, false),
    F1("f1", 0, 5, 5, true),
    G1("g1", 0, 6, 6, false),
    H1("h1", 0, 7, 7, true),

    A2("a2", 1, 0, 8, true),
    B2("b2", 1, 1, 9, false),
    C2("c2", 1, 2, 10, true),
    D2("d2", 1, 3, 11, false),
    E2("e2", 1, 4, 12, true),
    F2("f2", 1, 5, 13, false),
    G2("g2", 1, 6, 14, true),
    H2("h2", 1, 7, 15, false),

    A3("a3", 2, 0, 16, false),
    B3("b3", 2, 1, 17, true),
    C3("c3", 2, 2, 18, false),
    D3("d3", 2, 3, 19, true),
    E3("e3", 2, 4, 20, false),
    F3("f3", 2, 5, 21, true),
    G3("g3", 2, 6, 22, false),
    H3("h3", 2, 7, 23, true),

    A4("a4", 3, 0, 24, true),
    B4("b4", 3, 1, 25, false),
    C4("c4", 3, 2, 26, true),
    D4("d4", 3, 3, 27, false),
    E4("e4", 3, 4, 28, true),
    F4("f4", 3, 5, 29, false),
    G4("g4", 3, 6, 30, true),
    H4("h4", 3, 7, 31, false),

    A5("a5", 4, 0, 32, false),
    B5("b5", 4, 1, 33, true),
    C5("c5", 4, 2, 34, false),
    D5("d5", 4, 3, 35, true),
    E5("e5", 4, 4, 36, false),
    F5("f5", 4, 5, 37, true),
    G5("g5", 4, 6, 38, false),
    H5("h5", 4, 7, 39, true),

    A6("a6", 5, 0, 40, true),
    B6("b6", 5, 1, 41, false),
    C6("c6", 5, 2, 42, true),
    D6("d6", 5, 3, 43, false),
    E6("e6", 5, 4, 44, true),
    F6("f6", 5, 5, 45, false),
    G6("g6", 5, 6, 46, true),
    H6("h6", 5, 7, 47, false),

    A7("a7", 6, 0, 48, false),
    B7("b7", 6, 1, 49, true),
    C7("c7", 6, 2, 50, false),
    D7("d7", 6, 3, 51, true),
    E7("e7", 6, 4, 52, false),
    F7("f7", 6, 5, 53, true),
    G7("g7", 6, 6, 54, false),
    H7("h7", 6, 7, 55, true),

    A8("a8", 7, 0, 56, true),
    B8("b8", 7, 1, 57, false),
    C8("c8", 7, 2, 58, true),
    D8("d8", 7, 3, 59, false),
    E8("e8", 7, 4, 60, true),
    F8("f8", 7, 5, 61, false),
    G8("g8", 7, 6, 62, true),
    H8("h8", 7, 7, 63, false);
    @Serial
    private static final long serialVersionUID = 1L;

    private final String code;
    private final int row;
    private final int column;
    private final int index;
    private final boolean isWhite;

    public static Position of(int row, int column) throws ChessEngineRuntimeException {
        for (Position position : values()) {
            if (position.row == row && position.column == column) {
                return position;
            }
        }
        return null;
    }

    public static Position of(int index) throws ChessEngineRuntimeException {
        for (Position position : values()) {
            if (position.index == index) {
                return position;
            }
        }
        return null;
    }

    public static Position of(String code) throws ChessEngineRuntimeException {
        code = code.strip().toLowerCase();

        for (Position position : values()) {
            if (position.code.equalsIgnoreCase(code)) {
                return position;
            }
        }
        return null;
    }

    public static Optional<Position> ofOptional(String code) {
        return Optional.ofNullable(of(code));
    }

    public boolean isLastLine() {
        return row == 0 || row == 7;
    }

    //---------------------------------------------------------------

    public Optional<Position> left(FigureColor figureColor) {
        return figureColor.isWhite() ? offsetOptional(0, -1) : offsetOptional(0, 1);
    }

    public Optional<Position> right(FigureColor figureColor) {
        return figureColor.isWhite() ? offsetOptional(0, 1) : offsetOptional(0, -1);
    }

    public Optional<Position> bottom(FigureColor figureColor) {
        return figureColor.isWhite() ? offsetOptional(-1, 0) : offsetOptional(1, 0);
    }

    public Optional<Position> leftTop(FigureColor figureColor) {
        return figureColor.isWhite() ? offsetOptional(1, -1) : offsetOptional(-1, 1);
    }

    public Optional<Position> leftBottom(FigureColor figureColor) {
        return figureColor.isWhite() ? offsetOptional(-1, -1) : offsetOptional(1, 1);
    }


    public Optional<Position> top(FigureColor figureColor) {
        return figureColor.isWhite() ? offsetOptional(1, 0) : offsetOptional(-1, 0);
    }

    public Optional<Position> topTop(FigureColor figureColor) {
        return figureColor.isWhite() ? offsetOptional(2, 0) : offsetOptional(-2, 0);
    }

    public Optional<Position> rightTop(FigureColor figureColor) {
        return figureColor.isWhite() ? offsetOptional(1, 1) : offsetOptional(-1, -1);
    }

    public Optional<Position> rightBottom(FigureColor figureColor) {
        return figureColor.isWhite() ? offsetOptional(-1, 1) : offsetOptional(1, -1);
    }

    public Optional<Position> offsetOptional(int rowOffset, int columnOffset) {
       return Optional.ofNullable(of(row + rowOffset, column + columnOffset));
    }
    public Position offset(int rowOffset, int columnOffset) {
        return Position.of(row + rowOffset, column + columnOffset);
    }

    //---------------------------------------------------------------


    @Override
    public String toString() {
        return String.valueOf((char) (column + 'a')) + (char) (row + '1');
    }

    public boolean equalsString(String springPosition) {
        return code.equals(springPosition.toLowerCase());
    }
}
