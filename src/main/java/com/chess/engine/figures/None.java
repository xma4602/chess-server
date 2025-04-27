package com.chess.engine.figures;

import com.chess.engine.Board;
import com.chess.engine.FigureType;
import com.chess.engine.Position;
import com.chess.engine.actions.Action;

import java.util.Collections;
import java.util.List;

public class None extends Figure {

    public static None NONE = new None();

    protected None() {
        super(FigureType.NONE, null);
    }

    @Override
    public List<Action> getActions(Board board, Position position) {
        return Collections.emptyList();
    }

    @Override
    public Figure clone() {
        return this;
    }
}
