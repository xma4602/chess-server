package com.chess.engine.actions;

import com.chess.engine.FigureColor;
import com.chess.engine.FigureType;
import com.chess.engine.Position;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class ActionSwap extends Action {
    @Serial
    private static final long serialVersionUID = 1L;

    public static final Pattern swapPattern = Pattern.compile("^([a-h][1-8])-([a-h][1-8])([KQNBRP])$");

    private final Position startPosition;
    private final Position endPosition;
    private final FigureColor figureColor;
    @Setter
    private FigureType swapType;

    protected ActionSwap(Position startPosition, FigureType swapType, Position endPosition) {
        super(ActionType.SWAP);
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.swapType = swapType;
        figureColor = FigureColor.DEFAULT;
    }

    public ActionSwap(Position startPosition, Position endPosition, FigureColor figureColor) {
        super(ActionType.SWAP);
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.figureColor = figureColor;
        swapType = FigureType.NONE;
    }

    public static Optional<ActionSwap> parse(String action) {
        Matcher matcher = swapPattern.matcher(action);
        if (matcher.find()) {
            Position startPosition = Position.of(matcher.group(1));
            Position endPosition = Position.of(matcher.group(2));
            FigureType figureType = FigureType.getTypeByNotationChar(matcher.group(3));
            return Optional.of(new ActionSwap(startPosition, figureType, endPosition));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public String toString() {
        return startPosition + "-" + endPosition + swapType.getNotationChar();
    }

    @Override
    public boolean equals(Action action) {
        if (action.getActionType() != actionType) return false;
        ActionSwap actionSwap = (ActionSwap) action;
        if (actionSwap.startPosition != startPosition) return false;
        if (actionSwap.figureColor != figureColor) return false;
        if (actionSwap.endPosition != endPosition) return false;

        return actionSwap.swapType != FigureType.NONE;
    }

    @Override
    public boolean equalsPositions(String startPosition, String endPosition) {
        return this.startPosition.equalsString(startPosition)
                && this.endPosition.equalsString(endPosition);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ActionSwap that)) return false;
        return figureColor == that.figureColor
                && startPosition.equals(that.startPosition)
                && endPosition.equals(that.endPosition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getStartPosition(), getEndPosition(), getFigureColor(), getSwapType());
    }
}
