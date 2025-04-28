
package com.chess.engine;

import com.chess.engine.actions.*;
import com.chess.engine.exceptions.ChessEngineRuntimeException;
import com.chess.engine.figures.*;

import java.io.Serializable;
import java.util.*;


public class Board implements Serializable, Cloneable {

    private final Map<Position, Figure> cells = new EnumMap<>(Position.class);

    public static Board newBoard() {
        Board board = new Board();

        // Заполнение фигур
        // Ладьи
        board.cells.put(Position.A1, new Rook(FigureColor.WHITE));
        board.cells.put(Position.H1, new Rook(FigureColor.WHITE));
        board.cells.put(Position.A8, new Rook(FigureColor.BLACK));
        board.cells.put(Position.H8, new Rook(FigureColor.BLACK));

        // Кони
        board.cells.put(Position.B1, new Knight(FigureColor.WHITE));
        board.cells.put(Position.G1, new Knight(FigureColor.WHITE));
        board.cells.put(Position.B8, new Knight(FigureColor.BLACK));
        board.cells.put(Position.G8, new Knight(FigureColor.BLACK));

        // Слоны
        board.cells.put(Position.C1, new Bishop(FigureColor.WHITE));
        board.cells.put(Position.F1, new Bishop(FigureColor.WHITE));
        board.cells.put(Position.C8, new Bishop(FigureColor.BLACK));
        board.cells.put(Position.F8, new Bishop(FigureColor.BLACK));

        // Короли и королевы
        board.cells.put(Position.D1, new Queen(FigureColor.WHITE));
        board.cells.put(Position.E1, new King(FigureColor.WHITE));
        board.cells.put(Position.D8, new Queen(FigureColor.BLACK));
        board.cells.put(Position.E8, new King(FigureColor.BLACK));

        // Пешки
        for (int column = 0; column < 8; column++) {
            board.cells.put(Position.of(1, column), new Pawn(FigureColor.WHITE));
            board.cells.put(Position.of(6, column), new Pawn(FigureColor.BLACK));
        }

        // Пустые ячейки (все остальные позиции)
        for (int row = 2; row <= 5; row++) {
            for (int col = 0; col < 8; col++) {
                board.cells.put(Position.of(row, col), None.NONE); // Пустая ячейка
            }
        }
        return board;
    }

    private Board() {
    }

    private Board(Map<Position, Figure> cells) {
        for (Map.Entry<Position, Figure> cellEntry : cells.entrySet()) {
            Position position = cellEntry.getKey();
            Figure figure = cellEntry.getValue().clone();
            this.cells.put(position, figure);
        }
    }

    //---------------------------------------------------------------------------------------
    public Map<Position, Figure> getCells() {
        return new HashMap<>(cells);
    }

    public boolean isNone(Position position) {
        return cells.get(position).getFigureType() == FigureType.NONE;
    }

    public boolean isNotNone(Position position) {
        return cells.get(position).getFigureType() != FigureType.NONE;
    }

    public boolean hasAllNone(Position... positions) {
        for (var position : positions) {
            if (isNotNone(position)) {
                return false;
            }
        }
        return true;
    }

    public FigureColor getFigureColor(Position position) {
        return cells.get(position).getFigureColor();
    }

    public boolean getCellColor(Position position) {
        return position.isWhite();
    }

    public boolean wasNotMoving(Position position) {
        return !cells.get(position).isMoved();
    }

    public boolean wasSpecialMove(Position position) {
        return cells.get(position).isActioned();
    }

    public boolean isPawn(Position position) {
        return cells.get(position).getFigureType() == FigureType.PAWN;
    }

    public boolean isRook(Position position) {
        return cells.get(position).getFigureType() == FigureType.ROOK;
    }

    public FigureType typeBy(Position position) {
        return cells.get(position).getFigureType();
    }

    public boolean hasOpponent(Position position, FigureColor actualColor) {
        Figure figure = cells.get(position);
        return figure.getFigureType() != FigureType.NONE && figure.getFigureColor() != actualColor;
    }

    public boolean hasMe(Position position, FigureColor actualColor) {
        Figure figure = cells.get(position);
        return figure.getFigureType() != FigureType.NONE && figure.getFigureColor() == actualColor;
    }

    public Position findKing(FigureColor figureColor) {
        for (Map.Entry<Position, Figure> cellEntry : cells.entrySet()) {
            Figure figure = cellEntry.getValue();
            if ((figure.getFigureType() == FigureType.KING) && (figure.getFigureColor() == figureColor)) {
                return cellEntry.getKey();
            }
        }

        throw new ChessEngineRuntimeException("На доске отсутствует %s король".formatted(figureColor.isWhite() ? "белый" : "черный"));
    }

    public List<Position> getFigurePositionsByColor(FigureColor figureColor) {
        List<Position> positions = new ArrayList<>(16);
        for (Position position : Position.values()) {
            Figure figure = cells.get(position);
            if (figure.getFigureType() != FigureType.NONE && figure.getFigureColor() == figureColor) {
                positions.add(position);
            }
        }
        return positions;
    }

    //---------------------------------------------------------------------------------------

    public void executeAction(Action action) {
        switch (action.getActionType()) {
            case MOVE -> {
                ActionMove actionMove = (ActionMove) action;
                move(actionMove.getStartPosition(), actionMove.getEndPosition());

                Position endPosition = actionMove.getEndPosition();
                if (isPawn(endPosition)) {
                    if (actionMove.isDoubleMove() && !wasSpecialMove(endPosition)) {
                        switchSpecialAction(endPosition);
                    }
                    if (!actionMove.isDoubleMove() && wasSpecialMove(endPosition)) {
                        switchSpecialAction(endPosition);
                    }
                }
            }
            case EAT -> {
                ActionEat actionEat = (ActionEat) action;
                eat(actionEat.getStartPosition(), actionEat.getEatenPosition(), actionEat.getEatenPosition());
            }
            case TAKING -> {
                ActionTaking actionTaking = (ActionTaking) action;
                eat(actionTaking.getStartPosition(), actionTaking.getEndPosition(), actionTaking.getEatenPosition());
            }
            case SWAP -> {
                ActionSwap actionSwap = (ActionSwap) action;
                swap(actionSwap.getStartPosition(), actionSwap.getEndPosition(),
                        actionSwap.getSwapType(), actionSwap.getFigureColor());
            }
            case CASTLING -> {
                ActionCastling actionCastling = (ActionCastling) action;
                move(actionCastling.getKingStartPosition(), actionCastling.getKingEndPosition());
                move(actionCastling.getRookStartPosition(), actionCastling.getRookEndPosition());
            }
        }
    }

    private void move(Position startPosition, Position endPosition) {
        Figure figure = cells.put(startPosition, None.NONE);
        cells.put(endPosition, figure);
    }

    private void eat(Position startPosition, Position endPosition, Position eatenPosition) {
        Figure figure = cells.put(startPosition, None.NONE);
        cells.put(endPosition, figure);
        if (endPosition != eatenPosition) {
            cells.put(eatenPosition, None.NONE);
        }
    }

    private void swap(Position startPosition, Position endPosition, FigureType figureType, FigureColor figureColor) {
        cells.put(startPosition, None.NONE);
        cells.put(endPosition, Figure.fromFigureType(figureType, figureColor));
    }

    private void switchSpecialAction(Position position) {
        cells.get(position).setActioned(true);
    }


    //---------------------------------------------------------------------------------------

    @Override
    public Board clone() {
        return new Board(this.cells);
    }

    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean isReverse) {

        StringBuilder builder = new StringBuilder("  ");
        builder.append(isReverse ? "H  G  F E  D C  B  A" : "A  B  C D  E F  G  H");
        builder.append("\n ╔═════════════════════╗\n");
        for (int row = 7; row >= 0; row--) {
            builder.append(isReverse ? 8 - row : row + 1);
            builder.append("║");
            for (int col = 0; col <= 7; col++) {
                Position position = Position.of(row, col);
                Figure figure = getFigureByPosition(position);
                builder.append(figure.getFigureType().getImageChar(figure.getFigureColor()));
                if (figure.getFigureType() == FigureType.NONE && col % 4 == 1) builder.append(' ');
                builder.append('|');
            }
            builder.replace(builder.length() - 1, builder.length(), " ");
            builder.append("║");
            builder.append(isReverse ? 8 - row : row + 1);
            builder.append('\n');
        }
        builder.append(" ╚═════════════════════╝\n");
        builder.append("  ");
        builder.append(isReverse ? "H  G  F E  D C  B  A" : "A  B  C D  E F  G  H");

        return builder.toString();
    }


//    private Board flipBoard() {
//        Board flippedBoard = new Board();
//
//        for (int row = 0; row < 8; row++) {
//            for (int col = 0; col < 8; col++) {
//                Position originalPosition = Position.of(row, col);
//                Position flippedPosition = Position.of(7 - row, col); // Переворот по вертикали
//
//                Figure originalFigure = cells.get(originalPosition);
//                if (originalFigure != null) {
//                    // Копируем фигуру в перевернутую доску
//                    flippedBoard.cells.put(flippedPosition, Figure.fromFigureType(originalFigure.getFigureType(), originalFigure.getFigureColor()));
//                } else {
//                    // Если ячейка пустая, просто добавляем пустую ячейку
//                    flippedBoard.cells.put(flippedPosition, None.NONE);
//                }
//            }
//        }
//
//        return flippedBoard;
//    }

    public Figure getFigureByPosition(Position position) {
        return cells.get(position);
    }

    /*
    56 57 58 59 60 61 62 63
    48 49 50 51 52 53 54 55
    40 41 42 43 44 45 46 47
    32 33 34 35 36 37 38 39
    24 25 26 27 28 29 30 31
    16 17 18 19 20 21 22 23
    08 09 10 11 12 13 14 15
    00 01 02 03 04 05 06 07

    a8 b8 c8 d8 e8 f8 g8 h8
    a7 b7 c7 d7 e7 f7 g7 h7
    a6 b6 c6 d6 e6 f6 g6 h6
    a5 b5 c5 d5 e5 f5 g5 h5
    a4 b4 c4 d4 e4 f4 g4 h4
    a3 b3 c3 d3 e3 f3 g3 h3
    a2 b2 c2 d2 e2 f2 g2 h2
    a1 b1 c1 d1 e1 f1 g1 h1
    */
}
