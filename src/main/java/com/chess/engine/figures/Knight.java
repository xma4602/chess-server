package com.chess.engine.figures;

import com.chess.engine.Board;
import com.chess.engine.FigureColor;
import com.chess.engine.FigureType;
import com.chess.engine.Position;
import com.chess.engine.actions.Action;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

public class Knight extends Figure {
    @Serial
    private static final long serialVersionUID = 1L;
    public Knight(FigureColor figureColor) {
        super(FigureType.KNIGHT, figureColor);
    }

    public Knight(FigureType figureType, FigureColor figureColor, boolean moved, boolean actioned) {
        super(figureType, figureColor, moved, actioned);
    }

    @Override
    public List<Action> getActions(Board board, Position position) {
        List<Action> actions = new ArrayList<>();

        add(actions, moveOrEat(board, position, position.offsetOptional(+2, +1)));
        add(actions, moveOrEat(board, position, position.offsetOptional(+1, +2)));
        add(actions, moveOrEat(board, position, position.offsetOptional(-1, +2)));
        add(actions, moveOrEat(board, position, position.offsetOptional(-2, +1)));
        add(actions, moveOrEat(board, position, position.offsetOptional(-2, -1)));
        add(actions, moveOrEat(board, position, position.offsetOptional(-1, -2)));
        add(actions, moveOrEat(board, position, position.offsetOptional(+1, -2)));
        add(actions, moveOrEat(board, position, position.offsetOptional(+2, -1)));

        return actions;
    }

    @Override
    public Figure clone() {
        return new Knight(figureType, figureColor, moved, actioned);
    }
}
