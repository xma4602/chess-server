package com.chess.engine.actions;

import com.chess.engine.Position;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@EqualsAndHashCode(callSuper = true)
public class ActionMove extends Action {
    private static final Pattern movePattern = Pattern.compile("([a-h][1-8])-([a-h][1-8])");

    private final Position startPosition;
    private final Position endPosition;

    protected ActionMove(Position startPosition, Position endPosition) {
        super(ActionType.MOVE);
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    public static Optional<ActionMove> parse(String action) {
        Matcher matcher = movePattern.matcher(action);
        return Optional.ofNullable(
                matcher.find() ?
                        new ActionMove(Position.of(matcher.group(1)), Position.of(matcher.group(2))) :
                        null
        );
    }

    public boolean isDoubleMove() {
        return Math.abs(endPosition.getRow() - startPosition.getRow()) == 2;
    }

    @Override
    public String toString() {
        return startPosition + "-" + endPosition;
    }

    @Override
    public boolean equalsPositions(String startPosition, String endPosition) {
        return this.startPosition.equalsString(startPosition)
                && this.endPosition.equalsString(endPosition);
    }
}
