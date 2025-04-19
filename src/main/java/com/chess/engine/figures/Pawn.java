package com.chess.engine.figures;

import com.chess.engine.Board;
import com.chess.engine.FigureColor;
import com.chess.engine.Position;
import com.chess.engine.actions.Action;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Pawn extends AbstractFigure {

    @Override
    public List<Action> getActions(Board board, Position position) {
        List<Action> actions = new ArrayList<>();
        FigureColor figureColor = board.getFigureColor(position);

        add(actions, moveForward(board, figureColor, position, position.top(figureColor)));
        add(actions, moveDoubleForward(board, position, position.topTop(figureColor)));
        add(actions, eat(board, figureColor, position, position.leftTop(figureColor)));
        add(actions, eat(board, figureColor, position, position.rightTop(figureColor)));
        add(actions, take(board, figureColor, position, position.left(figureColor)));
        add(actions, take(board, figureColor, position, position.right(figureColor)));

        return actions;
    }


    private Action eat(Board board, FigureColor figureColor, Position startPosition, Optional<Position> endPosition) {
        if (endPosition.isPresent()) {
            var endPos = endPosition.get();
            if (board.hasOpponent(endPos, figureColor)) {
                return Action.eat(startPosition, endPos);
            }
        }
        return null;
    }

    private Action take(Board board, FigureColor figureColor, Position startPosition, Optional<Position> opponentPosition) {
        if (opponentPosition.isPresent()) {
            var oppPos = opponentPosition.get();
            if (board.isPawn(oppPos) //на позиции пешка?
                    && board.hasOpponent(oppPos, figureColor) //на позиции противник?
                    && board.wasSpecialMove(oppPos) //противник делал двойной ход?
            ) {
                return Action.take(startPosition, oppPos.top(figureColor).get(), oppPos);
            }
        }

        return null;
    }

    private Action moveForward(Board board, FigureColor figureColor, Position startPosition, Optional<Position> endPosition) {
        if (endPosition.isPresent()) {
            var endPos = endPosition.get();
            if (board.isNone(endPos)) { //позиция корректна и на позиции пусто?
                return endPos.isLastLine() ?  //это последняя линия поля?
                        Action.swap(startPosition, endPos, figureColor) :
                        Action.move(startPosition, endPos);
            }
        }
        return null;
    }

    private Action moveDoubleForward(Board board, Position startPosition, Optional<Position> endPosition) {
        if (endPosition.isPresent()) {
            var endPos = endPosition.get();
            if (board.wasNotMoving(startPosition)  //пешка не двигалась?
                    && board.isNone(endPos)) { // на позиции пусто?
                return Action.move(startPosition, endPos);
            }
        }
        return null;
    }
}
