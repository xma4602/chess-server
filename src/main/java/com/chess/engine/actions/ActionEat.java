package com.chess.engine.actions;

import com.chess.engine.FigureType;
import com.chess.engine.Position;
import lombok.Getter;

import java.io.Serial;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class ActionEat extends Action {
    @Serial
    private static final long serialVersionUID = 1L;

    public static final Pattern eatPattern = Pattern.compile("^([KQBNR]|[ФСЛК]|Кр)?([a-h][1-8])x([a-h][1-8])$");

    private final Position startPosition;
    private final Position eatenPosition;
    private final FigureType figureType;

    public ActionEat(Position startPosition, Position eatenPosition, FigureType figureType) {
        super(ActionType.EAT);
        this.startPosition = startPosition;
        this.eatenPosition = eatenPosition;
        this.figureType = figureType;
    }

    public static Optional<ActionEat> parse(String action) {
        Matcher matcher = eatPattern.matcher(action);
        if (matcher.find()) {
            FigureType figureType = FigureType.fromNotationChar(matcher.group(1) != null ? matcher.group(1) : "");
            Position startPosition = Position.of(matcher.group(2));
            Position endPosition = Position.of(matcher.group(3));
            return Optional.of(new ActionEat(startPosition, endPosition, figureType));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public String getCodeNotation() {
        return figureType.getNotationCharRu() + startPosition + "x" + eatenPosition;
    }

    @Override
    public String getAlgebraicNotation() {
        return figureType.getNotationCharRu() + "x" + eatenPosition;
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
