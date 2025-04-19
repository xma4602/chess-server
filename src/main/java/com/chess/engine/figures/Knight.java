package com.chess.engine.figures;

import com.chess.engine.Board;
import com.chess.engine.FigureColor;
import com.chess.engine.Position;
import com.chess.engine.actions.Action;

import java.util.ArrayList;
import java.util.List;

public class Knight extends AbstractFigure {

    @Override
    public List<Action> getActions(Board board, Position position) {
        List<Action> actions = new ArrayList<>();
        FigureColor figureColor = board.getFigureColor(position);

        add(actions, moveOrEat(board, figureColor, position, position.offsetOptional(+2, +1)));
        add(actions, moveOrEat(board, figureColor, position, position.offsetOptional(+1, +2)));
        add(actions, moveOrEat(board, figureColor, position, position.offsetOptional(-1, +2)));
        add(actions, moveOrEat(board, figureColor, position, position.offsetOptional(-2, +1)));
        add(actions, moveOrEat(board, figureColor, position, position.offsetOptional(-2, -1)));
        add(actions, moveOrEat(board, figureColor, position, position.offsetOptional(-1, -2)));
        add(actions, moveOrEat(board, figureColor, position, position.offsetOptional(+1, -2)));
        add(actions, moveOrEat(board, figureColor, position, position.offsetOptional(+2, -1)));

        return actions;
    }
}
