package com.chess.engine.figures;

import com.chess.engine.Board;
import com.chess.engine.FigureColor;
import com.chess.engine.FigureType;
import com.chess.engine.Position;
import com.chess.engine.actions.Action;

import java.util.ArrayList;
import java.util.List;

public class Queen extends Figure {

    public Queen(FigureColor figureColor) {
        super(FigureType.QUEEN, figureColor);
    }

    public Queen(FigureType figureType, FigureColor figureColor, boolean moved, boolean actioned) {
        super(figureType, figureColor, moved, actioned);
    }


    @Override
    public List<Action> getActions(Board board, Position position) {
        List<Action> actions = new ArrayList<>();
        FigureColor figureColor = board.getFigureColor(position);

        add(actions, moveOrEatInDirection(board, figureColor, position, x -> x.top(figureColor)));
        add(actions, moveOrEatInDirection(board, figureColor, position, x -> x.rightTop(figureColor)));
        add(actions, moveOrEatInDirection(board, figureColor, position, x -> x.right(figureColor)));
        add(actions, moveOrEatInDirection(board, figureColor, position, x -> x.rightBottom(figureColor)));
        add(actions, moveOrEatInDirection(board, figureColor, position, x -> x.bottom(figureColor)));
        add(actions, moveOrEatInDirection(board, figureColor, position, x -> x.leftBottom(figureColor)));
        add(actions, moveOrEatInDirection(board, figureColor, position, x -> x.left(figureColor)));
        add(actions, moveOrEatInDirection(board, figureColor, position, x -> x.leftTop(figureColor)));

        return actions;
    }

    @Override
    public Figure clone() {
        return new Queen(figureType, figureColor, moved, actioned);
    }
}
