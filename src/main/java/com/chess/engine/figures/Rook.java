package com.chess.engine.figures;

import com.chess.engine.Board;
import com.chess.engine.FigureColor;
import com.chess.engine.FigureType;
import com.chess.engine.Position;
import com.chess.engine.actions.Action;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

public class Rook extends Figure {
    @Serial
    private static final long serialVersionUID = 1L;
    public Rook(FigureColor figureColor) {
        super(FigureType.ROOK, figureColor);
    }

    public Rook(FigureType figureType, FigureColor figureColor, boolean moved, boolean actioned) {
        super(figureType, figureColor, moved, actioned);
    }

    @Override
    public List<Action> getActions(Board board, Position position) {
        List<Action> actions = new ArrayList<>();

        add(actions, moveOrEatInDirection(board, figureColor, position, x -> x.right(figureColor)));
        add(actions, moveOrEatInDirection(board, figureColor, position, x -> x.left(figureColor)));
        add(actions, moveOrEatInDirection(board, figureColor, position, x -> x.top(figureColor)));
        add(actions, moveOrEatInDirection(board, figureColor, position, x -> x.bottom(figureColor)));

        return actions;
    }

    @Override
    public Figure clone() {
        return new Rook(figureType, figureColor, moved, actioned);
    }

}
