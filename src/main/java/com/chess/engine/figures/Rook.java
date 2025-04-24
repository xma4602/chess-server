package com.chess.engine.figures;

import com.chess.engine.Board;
import com.chess.engine.FigureColor;
import com.chess.engine.Position;
import com.chess.engine.actions.Action;

import java.util.ArrayList;
import java.util.List;

public class Rook extends AbstractFigure {

    @Override
    public List<Action> getActions(Board board, Position position) {
        List<Action> actions = new ArrayList<>();
        FigureColor figureColor = board.getFigureColor(position);

        add(actions, moveOrEatInDirection(board, figureColor, position, x -> x.right(figureColor)));
        add(actions, moveOrEatInDirection(board, figureColor, position, x -> x.left(figureColor)));
        add(actions, moveOrEatInDirection(board, figureColor, position, x -> x.top(figureColor)));
        add(actions, moveOrEatInDirection(board, figureColor, position, x -> x.bottom(figureColor)));

        return actions;
    }

}
