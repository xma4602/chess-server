package com.chess.engine.figures;

import com.chess.engine.Board;
import com.chess.engine.FigureColor;
import com.chess.engine.FigureType;
import com.chess.engine.Position;
import com.chess.engine.actions.Action;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class King extends Figure {

    public King(FigureColor figureColor) {
        super(FigureType.KING, figureColor);
    }
    public King(FigureType figureType, FigureColor figureColor, boolean moved, boolean actioned) {
        super(figureType, figureColor, moved, actioned);
    }
    @Override
    public List<Action> getActions(Board board, Position position) {
        List<Action> actions = new LinkedList<>();

        add(actions, moveOrEat(board, figureColor, position, position.top(figureColor)));
        add(actions, moveOrEat(board, figureColor, position, position.rightTop(figureColor)));
        add(actions, moveOrEat(board, figureColor, position, position.right(figureColor)));
        add(actions, moveOrEat(board, figureColor, position, position.rightBottom(figureColor)));
        add(actions, moveOrEat(board, figureColor, position, position.bottom(figureColor)));
        add(actions, moveOrEat(board, figureColor, position, position.leftBottom(figureColor)));
        add(actions, moveOrEat(board, figureColor, position, position.left(figureColor)));
        add(actions, moveOrEat(board, figureColor, position, position.leftTop(figureColor)));

        add(actions, castling(board, figureColor, position, position.offsetOptional(0, -4), false));
        add(actions, castling(board, figureColor, position, position.offsetOptional(0, +3), true));

        return actions;
    }

    @Override
    public Figure clone() {
        return new King(figureType, figureColor, moved, actioned);
    }

    private Action castling(Board board, FigureColor figureColor, Position kingPosition, Optional<Position> rookPosition, boolean isRight) {
        //условия для рокировки
        if (rookPosition.isPresent()) {
            var rookPos = rookPosition.get();
            if (board.isRook(rookPos) //на позиции ладья?
                    && board.hasMe(rookPos, figureColor) //это наша ладья?
                    && board.wasNotMoving(kingPosition) //король двигался?
                    && board.wasNotMoving(rookPos)) { //ладья двигалась?

                //между королем и ладьей не пусто?
                if (board.hasAllNone(getCastlingPathPositions(kingPosition, isRight))) {
                    return Action.castling(kingPosition, rookPos);
                }
            }
        }

        return null;
    }

    private Position[] getCastlingPathPositions(Position kingPosition, boolean isRight) {
        return isRight ?
                new Position[]{kingPosition.offset(0, 1), kingPosition.offset(0, 2)} :
                new Position[]{kingPosition.offset(0, -1), kingPosition.offset(0, -2), kingPosition.offset(0, -3)};
    }

}
