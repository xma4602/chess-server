package com.chess.engine.actions;

import com.chess.engine.Position;
import lombok.Getter;

import java.io.Serial;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class ActionTaking extends Action {
    @Serial
    private static final long serialVersionUID = 1L;

    public static final Pattern takingPattern = Pattern.compile("^([a-h][1-8]):([a-h][1-8])$");
    private final Position startPosition;
    private final Position endPosition;
    private final Position eatenPosition;

    public ActionTaking(Position startPosition, Position endPosition, Position eatenPosition) {
        super(ActionType.TAKING);
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.eatenPosition = eatenPosition;
    }

    protected ActionTaking(Position startPosition, Position endPosition) {
        super(ActionType.TAKING);
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.eatenPosition = Position.of(startPosition.getRow(), endPosition.getColumn());
    }

    public static Optional<ActionTaking> parse(String action) {
        Matcher matcher = takingPattern.matcher(action);
        if (matcher.find()) {
            Position startPosition = Position.of(matcher.group(1));
            Position endPosition = Position.of(matcher.group(2));
            return Optional.of(new ActionTaking(startPosition, endPosition));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public String getCodeNotation() {
        return startPosition + ":" + endPosition;
    }

    @Override
    public String getAlgebraicNotation() {
        return startPosition.getColumnString() + eatenPosition.getColumnString();
    }


    @Override
    public boolean equals(Action action) {
        if (action.getActionType() != actionType) return false;
        ActionTaking actionTaking = (ActionTaking) action;
        if (actionTaking.startPosition != startPosition) return false;
        if (actionTaking.eatenPosition != eatenPosition) return false;
        return actionTaking.endPosition == endPosition;
    }

    @Override
    public boolean equalsPositions(String startPosition, String endPosition) {
        return this.startPosition.equalsString(startPosition)
                && this.endPosition.equalsString(endPosition);
    }
}
