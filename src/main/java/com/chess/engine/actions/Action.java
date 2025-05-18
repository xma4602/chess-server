package com.chess.engine.actions;

import com.chess.engine.FigureColor;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@Getter
public abstract class Action implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    protected final ActionType actionType;
    private static final Predicate<String> actionPredicate = Pattern.compile("(0-0(-0)?)|([a-h][1-8]-[KQRBNP]?[a-h][1-8])|([a-h][1-8][x:][a-h][1-8])").asPredicate();

    protected Action(ActionType actionType) {
        this.actionType = actionType;
    }

    @Override
    public String toString() {
        return getCodeNotation();
    }

    public abstract String getCodeNotation();

    public abstract String getAlgebraicNotation();

    public abstract boolean equals(Action action);

    public abstract boolean equalsPositions(String startPosition, String endPosition);

    public static Optional<? extends Action> parse(String actionString, FigureColor playerColor) {
        Optional<? extends Action> action;

        action = ActionMove.parse(actionString);
        if (action.isPresent()) return action;

        action = ActionEat.parse(actionString);
        if (action.isPresent()) return action;

        action = ActionSwap.parse(actionString);
        if (action.isPresent()) return action;

        action = ActionTaking.parse(actionString);
        if (action.isPresent()) return action;

        action = ActionCastling.parse(actionString, playerColor);
        return action;

    }

    public enum ActionType {
        MOVE,
        EAT,
        SWAP,
        CASTLING,
        TAKING,
    }
}
