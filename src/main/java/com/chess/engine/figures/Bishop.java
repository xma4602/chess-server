package com.chess.engine.figures;

import com.chess.engine.Board;
import com.chess.engine.FigureColor;
import com.chess.engine.FigureType;
import com.chess.engine.Position;
import com.chess.engine.actions.Action;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

public class Bishop extends Figure {
    @Serial
    private static final long serialVersionUID = 1L;

    public Bishop(FigureColor figureColor) {
        super(FigureType.BISHOP, figureColor);
    }
    public Bishop(FigureType figureType, FigureColor figureColor, boolean moved, boolean actioned) {
        super(figureType, figureColor, moved, actioned);
    }

    @Override
    public List<Action> getActions(Board board, Position position) {
        List<Action> actions = new ArrayList<>();
        add(actions, moveOrEatInDirection(board, position, x -> x.rightTop(figureColor)));
        add(actions, moveOrEatInDirection(board, position, x -> x.rightBottom(figureColor)));
        add(actions, moveOrEatInDirection(board, position, x -> x.leftTop(figureColor)));
        add(actions, moveOrEatInDirection(board, position, x -> x.leftBottom(figureColor)));

        return actions;
    }

    @Override
    public Figure clone() {
        return new Bishop(figureType, figureColor, moved, actioned);
    }

}
