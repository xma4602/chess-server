package com.chess.engine.actions;

import com.chess.engine.FigureColor;
import com.chess.engine.Position;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@Getter
@EqualsAndHashCode
public abstract class Action {
    protected final ActionType actionType;
    private static final Predicate<String> actionPredicate = Pattern.compile("(0-0(-0)?)|([a-h][1-8]-[KQRBNP]?[a-h][1-8])|([a-h][1-8][x:][a-h][1-8])").asPredicate();

    protected Action(ActionType actionType) {
        this.actionType = actionType;
    }

    public abstract String toString();

    public abstract boolean equalsPositions(String startPosition, String endPosition);

    public static Action move(Position startPosition, Position endPosition) {
        return new ActionMove(startPosition, endPosition);
    }

    public static Action eat(Position startPosition, Position eatenPosition) {
        return new ActionEat(startPosition, eatenPosition);
    }

    public static Action swap(Position startPosition, Position endPosition, FigureColor figureColor) {
        return new ActionSwap(startPosition, endPosition, figureColor);
    }

    public static Action take(Position startPosition, Position endPosition, Position eatenPosition) {
        return new ActionTaking(startPosition, endPosition, eatenPosition);
    }

    public static Action castling(Position kingPosition, Position rookPosition) {
        return new ActionCastling(kingPosition, rookPosition);
    }

    public static Optional<? extends Action> parse(String actionString, boolean playerColor) {
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

    public static boolean validate(String action) {
        return actionPredicate.test(action);
    }

    public enum ActionType {
        MOVE,
        EAT,
        SWAP,
        CASTLING,
        TAKING,
    }
}
