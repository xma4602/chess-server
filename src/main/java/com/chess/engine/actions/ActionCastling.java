package com.chess.engine.actions;

import com.chess.engine.FigureColor;
import com.chess.engine.Position;
import lombok.Getter;

import java.io.Serial;
import java.util.Optional;

@Getter
public class ActionCastling extends Action {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Position kingStartPosition;
    private final Position rookStartPosition;

    protected ActionCastling(Position kingStartPosition, Position rookStartPosition) {
        super(ActionType.CASTLING);
        this.kingStartPosition = kingStartPosition;
        this.rookStartPosition = rookStartPosition;
    }

    public Position getKingEndPosition() {
        return isLongCastling() ? //если король правее
                kingStartPosition.offset(0, -2) : //движется вправо
                kingStartPosition.offset(0, +2); //движется влево

    }

    public Position getRookEndPosition() {
        return isLongCastling() ? //если король правее
                rookStartPosition.offset(0, +3) : //движется право
                rookStartPosition.offset(0, -2); //движется влево
    }

    public static Optional<ActionCastling> parse(String action, FigureColor figureColor) {
        return switch (action) {
            case "0-0-0" -> Optional.of(ActionCastling.left(figureColor));
            case "0-0" -> Optional.of(ActionCastling.right(figureColor));
            default -> Optional.empty();
        };
    }

    private static ActionCastling right(FigureColor figureColor) {
        return figureColor.isWhite() ?
                new ActionCastling(Position.E1, Position.H1) :
                new ActionCastling(Position.E8, Position.H8);
    }

    private static ActionCastling left(FigureColor figureColor) {
        return figureColor.isWhite() ?
                new ActionCastling(Position.E1, Position.A1) :
                new ActionCastling(Position.E8, Position.A8);
    }


    public boolean isLongCastling() {
        return rookStartPosition.getColumn() < kingStartPosition.getColumn();
    }

    @Override
    public String toString() {
        return isLongCastling() ? "0-0-0" : "0-0";
    }

    @Override
    public boolean equals(Action action) {
        if (action.getActionType() != actionType) return false;
        ActionCastling actionCastling = (ActionCastling) action;
        if (actionCastling.kingStartPosition != kingStartPosition) return false;
        return actionCastling.rookStartPosition == rookStartPosition;
    }

    @Override
    public boolean equalsPositions(String startPosition, String endPosition) {
        return this.kingStartPosition.equalsString(startPosition)
                && this.rookStartPosition.equalsString(endPosition);
    }

}
