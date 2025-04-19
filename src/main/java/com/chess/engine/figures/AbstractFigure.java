package com.chess.engine.figures;

import com.chess.engine.Board;
import com.chess.engine.FigureColor;
import com.chess.engine.Position;
import com.chess.engine.actions.Action;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public abstract class AbstractFigure {

    /**
     * Анализирует возможные ходы для заданной фигуры
     *
     * @param board    состояние игровой доски
     * @param position позиция фигуры, для которой производится анализ
     * @return возможные ходы для заданной фигуры
     */
    public abstract List<Action> getActions(Board board, Position position);

    protected void add(List<Action> actions, Action action) {
        if (action != null) actions.add(action);
    }

    protected void add(List<Action> actions, List<Action> action) {
        if (actions != null) actions.addAll(action);
    }

    protected Action moveOrEat(Board board, FigureColor figureColor, Position startPosition, Optional<Position> endPosition) {
        if (endPosition.isPresent()) { //следующая позиция существует?
            var endPos = endPosition.get();
            if (board.isNone(endPos)) { //следующая позиция пустая?
                return Action.move(startPosition, endPos); //можно двинуться
            } else if (board.hasOpponent(endPos, figureColor)) { //на позиции противник?
                return Action.eat(startPosition, endPos); //можно съесть
            }
        }
        return null;
    }

    protected List<Action> moveOrEatInDirection(Board board,
                                                      FigureColor figureColor,
                                                      Position startPosition,
                                                      Function<Position, Optional<Position>> direction) {
        return moveOrEatInDirection(board, figureColor, startPosition, direction, direction.apply(startPosition));
    }

    protected List<Action> moveOrEatInDirection(Board board,
                                                      FigureColor figureColor,
                                                      Position startPosition,
                                                      Function<Position, Optional<Position>> direction,
                                                      Optional<Position> nextPosition) {
        List<Action> actions = new ArrayList<>();

        if (nextPosition.isPresent()) { //следующая позиция существует?
            Position nextPos = nextPosition.get();
            if (board.isNone(nextPos)) { //следующая позиция пустая?
                actions.add(Action.move(startPosition, nextPos)); //можно двинуться
                actions.addAll(moveOrEatInDirection(board, figureColor, startPosition, direction, direction.apply(nextPos))); //проверить дальше
            } else if (board.hasOpponent(nextPos, figureColor)) { //на позиции противник?
                actions.add(Action.eat(startPosition, nextPos)); //можно съесть
            }
        }
        return actions;
    }

}
