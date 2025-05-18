package com.chess.engine.figures;

import com.chess.engine.Board;
import com.chess.engine.FigureColor;
import com.chess.engine.FigureType;
import com.chess.engine.Position;
import com.chess.engine.actions.Action;
import com.chess.engine.actions.ActionEat;
import com.chess.engine.actions.ActionMove;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Getter
public abstract class Figure implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    protected FigureType figureType;
    protected FigureColor figureColor;
    @Setter
    protected boolean moved;
    @Setter
    protected boolean actioned;


    protected Figure(FigureType figureType, FigureColor figureColor) {
        this(figureType, figureColor, false, false);
    }

    protected Figure(FigureType figureType, FigureColor figureColor, boolean moved, boolean actioned) {
        this.figureType = figureType;
        this.figureColor = figureColor;
        this.moved = moved;
        this.actioned = actioned;
    }

    /**
     * Анализирует возможные ходы для заданной фигуры
     *
     * @param board    состояние игровой доски
     * @param position позиция фигуры, для которой производится анализ
     * @return возможные ходы для заданной фигуры
     */
    public abstract List<Action> getActions(Board board, Position position);

    public List<Action> getEatActions(Board board, Position position){
        return getActions(board, position);
    }

    public abstract Figure clone();

    public String getId() {
        return figureColor.getId() + "_" + figureType.getId();
    }
    public static Figure fromFigureType(FigureType figureType, FigureColor figureColor) {
        return switch (figureType) {
            case NONE -> None.NONE;
            case PAWN -> new Pawn(figureColor);
            case KNIGHT -> new Knight(figureColor);
            case BISHOP -> new Bishop(figureColor);
            case ROOK -> new Rook(figureColor);
            case QUEEN -> new Queen(figureColor);
            case KING -> new King(figureColor);
        };
    }

    public boolean isNotMoved(){
        return !moved;
    }

    protected void add(List<Action> actions, Action action) {
        if (action != null) actions.add(action);
    }

    protected void add(List<Action> actions, List<Action> action) {
        if (actions != null) actions.addAll(action);
    }

    protected Action moveOrEat(Board board,
                               Position startPosition,
                               Optional<Position> endPosition) {
        if (endPosition.isPresent()) { //следующая позиция существует?
            var endPos = endPosition.get();
            if (board.isNone(endPos)) { //следующая позиция пустая?
                return new ActionMove(startPosition, endPos, figureType); //можно двинуться
            } else if (board.hasOpponent(endPos, figureColor)) { //на позиции противник?
                return new ActionEat(startPosition, endPos, figureType); //можно съесть
            }
        }
        return null;
    }

    protected List<Action> moveOrEatInDirection(Board board,
                                                Position startPosition,
                                                Function<Position, Optional<Position>> direction) {
        return moveOrEatInDirection(board, startPosition, direction, direction.apply(startPosition));
    }

    protected List<Action> moveOrEatInDirection(Board board,
                                                Position startPosition,
                                                Function<Position, Optional<Position>> direction,
                                                Optional<Position> nextPosition) {
        List<Action> actions = new ArrayList<>();

        if (nextPosition.isPresent()) { //следующая позиция существует?
            Position nextPos = nextPosition.get();
            if (board.isNone(nextPos)) { //следующая позиция пустая?
                actions.add(new ActionMove(startPosition, nextPos, figureType)); //можно двинуться
                actions.addAll(moveOrEatInDirection(board, startPosition, direction, direction.apply(nextPos))); //проверить дальше
            } else if (board.hasOpponent(nextPos, figureColor)) { //на позиции противник?
                actions.add(new ActionEat(startPosition, nextPos, figureType)); //можно съесть
            }
        }
        return actions;
    }

}
