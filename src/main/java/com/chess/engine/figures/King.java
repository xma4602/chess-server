package com.chess.engine.figures;

import com.chess.engine.Board;
import com.chess.engine.FigureColor;
import com.chess.engine.FigureType;
import com.chess.engine.Position;
import com.chess.engine.actions.Action;
import com.chess.engine.actions.ActionEat;

import java.io.Serial;
import java.util.LinkedList;
import java.util.List;

public class King extends Figure {
    @Serial
    private static final long serialVersionUID = 1L;
    public King(FigureColor figureColor) {
        super(FigureType.KING, figureColor);
    }

    public King(FigureColor figureColor, boolean moved, boolean actioned) {
        super(FigureType.KING, figureColor, moved, actioned);
    }

    @Override
    public List<Action> getActions(Board board, Position position) {
        List<Action> actions = getEatActions(board, position);

        add(actions, castling(board, position, false));
        add(actions, castling(board, position, true));

        return actions;
    }

    @Override
    public List<Action> getEatActions(Board board, Position position) {
        List<Action> actions = new LinkedList<>();

        add(actions, moveOrEat(board, position, position.top(figureColor)));
        add(actions, moveOrEat(board, position, position.rightTop(figureColor)));
        add(actions, moveOrEat(board, position, position.right(figureColor)));
        add(actions, moveOrEat(board, position, position.rightBottom(figureColor)));
        add(actions, moveOrEat(board, position, position.bottom(figureColor)));
        add(actions, moveOrEat(board, position, position.leftBottom(figureColor)));
        add(actions, moveOrEat(board, position, position.left(figureColor)));
        add(actions, moveOrEat(board, position, position.leftTop(figureColor)));

        return actions;
    }

    @Override
    public Figure clone() {
        return new King(figureColor, moved, actioned);
    }

    private Action castling(Board board, Position kingPosition, boolean isRight) {
        //условия для рокировки
        if (board.wasNotMoving(kingPosition)) {
            Position rookPosition = (figureColor == FigureColor.WHITE) ?
                    (isRight ? Position.H1 : Position.A1) :
                    (isRight ? Position.H8 : Position.A8);

            Figure figure = board.getFigureByPosition(rookPosition);

            if (figure.getFigureType() == FigureType.ROOK
                    && figure.getFigureColor() == figureColor
                    && figure.isNotMoved()
                    && board.hasAllNone(getCastlingPathPositions(kingPosition, isRight))
                    && hasNotEatKingActions(board, kingPosition, figureColor.reverseColor())
            ) {
                return Action.castling(kingPosition, rookPosition);
            }
        }

        return null;
    }

    public static boolean hasNotEatKingActions(Board board, Position kingPosition, FigureColor opponentFigureColor) {
        for (Position position : board.getFigurePositionsByColor(opponentFigureColor)) {
            Figure figure = board.getFigureByPosition(position);
            List<Action> actions = figure.getEatActions(board, position);
            for (Action action : actions) {
                if (action instanceof ActionEat actionEat) {
                    if (actionEat.getEatenPosition().equals(kingPosition)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private Position[] getCastlingPathPositions(Position kingPosition, boolean isRight) {
        return isRight ?
                new Position[]{kingPosition.offset(0, 1), kingPosition.offset(0, 2)} :
                new Position[]{kingPosition.offset(0, -1), kingPosition.offset(0, -2), kingPosition.offset(0, -3)};
    }

}
