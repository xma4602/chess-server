package com.chess.engine.actions;

import com.chess.engine.FigureColor;
import com.chess.engine.Position;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@EqualsAndHashCode(callSuper = true)
public class ActionCastling extends Action {
    public static final Pattern castlingPattern = Pattern.compile("(0-0-0)|(0-0)");
    private final Position kingStartPosition;
    private final Position rookStartPosition;

    protected ActionCastling(Position kingStartPosition, Position rookStartPosition) {
        super(ActionType.CASTLING);
        this.kingStartPosition = kingStartPosition;
        this.rookStartPosition = rookStartPosition;
    }

    public Position getKingEndPosition() {
        return isLongCastling() ? //если король левее
                kingStartPosition.offset(0, -2) : //движется вправо
                kingStartPosition.offset(0, +2); //движется влево

    }

    public Position getRookEndPosition() {
        return isLongCastling() ? //если король левее
                rookStartPosition.offset(0, +3) : //движется влево
                rookStartPosition.offset(0, -2); //движется вправо
    }

    public static Optional<ActionCastling> parse(String action, FigureColor figureColor) {
        Matcher matcher = castlingPattern.matcher(action);
        return Optional.ofNullable(
                !matcher.find() ?
                        null :
                        (matcher.group(1) == null ?
                                ActionCastling.left(figureColor) :
                                ActionCastling.right(figureColor))
        );
    }

    private static ActionCastling right(FigureColor figureColor) {
        return figureColor.isWhite() ?
                new ActionCastling(Position.of("e1"), Position.of("h1")) :
                new ActionCastling(Position.of("e8"), Position.of("h8"));
    }

    private static ActionCastling left(FigureColor figureColor) {
        return figureColor.isWhite()  ?
                new ActionCastling(Position.of("e1"), Position.of("a1")) :
                new ActionCastling(Position.of("e8"), Position.of("a8"));
    }


    public boolean isLongCastling() {
        return kingStartPosition.getColumn() < rookStartPosition.getColumn();
    }

    @Override
    public String toString() {
        return isLongCastling() ? "0-0-0" : "0-0";
    }

    @Override
    public boolean equalsPositions(String startPosition, String endPosition) {
        return this.kingStartPosition.equalsString(startPosition)
                && this.rookStartPosition.equalsString(endPosition);
    }

}
