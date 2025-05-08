package com.chess.engine.actions;

import com.chess.engine.FigureType;
import com.chess.engine.Position;
import lombok.Getter;

import java.io.Serial;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class ActionMove extends Action {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final Pattern movePattern = Pattern.compile("^([a-h][1-8])-([KQBNR])?([a-h][1-8])$");

    private final Position startPosition;
    private final Position endPosition;
    private final FigureType figureType;

    protected ActionMove(Position startPosition, Position endPosition, FigureType figureType) {
        super(ActionType.MOVE);
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.figureType = figureType;
    }

    public static Optional<ActionMove> parse(String action) {
        Matcher matcher = movePattern.matcher(action);
        if (matcher.find()) {
            Position startPosition = Position.of(matcher.group(1));
            Position endPosition = Position.of(matcher.group(3));
            String group = matcher.group(2) != null ? matcher.group(2) : "";
            FigureType figureType = FigureType.getTypeByNotationChar(group);
            return Optional.of(new ActionMove(startPosition, endPosition, figureType));
        } else {
            return Optional.empty();
        }
    }

    public boolean isDoubleMove() {
        return Math.abs(endPosition.getRow() - startPosition.getRow()) == 2;
    }

    @Override
    public String toString() {
        return startPosition + "-" + figureType.getNotationChar() + endPosition;
    }

    @Override
    public boolean equals(Action action) {
        if (action.getActionType() != actionType) return false;
        ActionMove actionMove = (ActionMove) action;
        if (actionMove.startPosition != startPosition) return false;
        return actionMove.endPosition == endPosition;
    }

    @Override
    public boolean equalsPositions(String startPosition, String endPosition) {
        return this.startPosition.equalsString(startPosition)
                && this.endPosition.equalsString(endPosition);
    }
}
