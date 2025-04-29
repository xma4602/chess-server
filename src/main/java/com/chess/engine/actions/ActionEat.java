package com.chess.engine.actions;

import com.chess.engine.Position;
import lombok.Getter;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class ActionEat extends Action {
    public static final Pattern eatPattern = Pattern.compile("([a-h][1-8])x([a-h][1-8])");
    private final Position startPosition;
    private final Position eatenPosition;

    protected ActionEat(Position startPosition, Position eatenPosition) {
        super(ActionType.EAT);
        this.startPosition = startPosition;
        this.eatenPosition = eatenPosition;
    }

    public static Optional<ActionEat> parse(String action) {
        Matcher matcher = eatPattern.matcher(action);
        return Optional.ofNullable(
                matcher.find() ?
                        new ActionEat(Position.of(matcher.group(1)), Position.of(matcher.group(2))) :
                        null
        );
    }

    @Override
    public String toString() {
        return startPosition + "x" + eatenPosition;
    }

    @Override
    public boolean equals(Action action) {
        if (action.getActionType() != actionType) return false;
        ActionEat actionEat = (ActionEat) action;
        if (actionEat.startPosition != startPosition) return false;
        return actionEat.eatenPosition == eatenPosition;
    }

    @Override
    public boolean equalsPositions(String startPosition, String endPosition) {
        return this.startPosition.equalsString(startPosition)
                && this.eatenPosition.equalsString(endPosition);
    }
}
