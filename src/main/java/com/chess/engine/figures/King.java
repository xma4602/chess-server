package com.chess.engine.figures;

import com.chess.engine.Board;
import com.chess.engine.FigureColor;
import com.chess.engine.FigureType;
import com.chess.engine.Position;
import com.chess.engine.actions.Action;
import com.chess.engine.actions.ActionEat;
import com.chess.engine.actions.ActionMove;

import java.io.Serial;
import java.util.LinkedList;
import java.util.List;

public class King extends Figure {
    @Serial
    private static final long serialVersionUID = 1L;

    public King(FigureColor figureColor) {
        super(FigureType.KING, figureColor);
    }

    public King(FigureType figureType, FigureColor figureColor, boolean moved, boolean actioned) {
        super(figureType, figureColor, moved, actioned);
    }

    @Override
    public List<Action> getActions(Board board, Position position) {
        List<Action> actions = getEatActions(board, position);

        add(actions, castling(board, figureColor, position, false));
        add(actions, castling(board, figureColor, position, true));

        return actions;
    }

    @Override
    public List<Action> getEatActions(Board board, Position position) {
        List<Action> actions = new LinkedList<>();

        add(actions, moveOrEat(board, figureColor, position, position.top(figureColor)));
        add(actions, moveOrEat(board, figureColor, position, position.rightTop(figureColor)));
        add(actions, moveOrEat(board, figureColor, position, position.right(figureColor)));
        add(actions, moveOrEat(board, figureColor, position, position.rightBottom(figureColor)));
        add(actions, moveOrEat(board, figureColor, position, position.bottom(figureColor)));
        add(actions, moveOrEat(board, figureColor, position, position.leftBottom(figureColor)));
        add(actions, moveOrEat(board, figureColor, position, position.left(figureColor)));
        add(actions, moveOrEat(board, figureColor, position, position.leftTop(figureColor)));

        return actions;
    }

    @Override
    public Figure clone() {
        return new King(figureType, figureColor, moved, actioned);
    }

    private Action castling(Board board, FigureColor figureColor, Position kingPosition, boolean isRight) {
        //условия для рокировки
        Position rookPosition = (figureColor == FigureColor.WHITE) ?
                (isRight ? Position.H1 : Position.A1) :
                (isRight ? Position.H8 : Position.A8);

        Figure king = board.getFigureByPosition(kingPosition);
        Figure rook = board.getFigureByPosition(rookPosition);

        if (rook.getFigureType() != FigureType.ROOK //если там не ладья
                || rook.getFigureColor() != figureColor // если не моего цвета
                || king.isMoved()  //если король двигался
                || rook.isMoved()) //если ладья двигалась
        {
            return null; //то нет рокировки
        }

        //путь короля для рокировки
        List<Position> castlingPathPositions = isRight ?
                List.of(kingPosition.offset(0, 1), kingPosition.offset(0, 2)) :
                List.of(kingPosition.offset(0, -1), kingPosition.offset(0, -2), kingPosition.offset(0, -3));

        for (Position position : castlingPathPositions) {
            //фигура на пути
            Figure figure = board.getFigureByPosition(position);
            //фигура на пути != пустая ячейка
            if (figure.getFigureType() != FigureType.NONE) {
                //то рокировка невозможна
                return null;
            }
        }

        //позиции фигур противника
        List<Position> opponentPositions = board.getFigurePositionsByColor(figureColor.reverseColor());

        for (Position position : opponentPositions) {
            //фигура противника
            Figure figure = board.getFigureByPosition(position);
            //действия фигуры противника
            List<Action> actions = figure.getEatActions(board, position);
            for (Action action : actions) {
                //если действие "съесть"
                if (action instanceof ActionEat actionEat) {
                    //если король под шахом
                    if (actionEat.getEatenPosition().equals(kingPosition)){
                        //то рокировка невозможна
                        return null;
                    }
                    //если действие "сдвинуться"
                } else if (action instanceof ActionMove actionMove) {
                    //если атакуется позиция по пути короля
                    if (castlingPathPositions.contains(actionMove.getEndPosition())) {
                        //то рокировка невозможна
                        return null;
                    }
                }
            }
        }

        return Action.castling(kingPosition, rookPosition);
    }
}
