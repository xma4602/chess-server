package com.chess.engine.figures;

import com.chess.engine.Board;
import com.chess.engine.FigureColor;
import com.chess.engine.FigureType;
import com.chess.engine.Position;
import com.chess.engine.actions.Action;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Pawn extends Figure {
    @Serial
    private static final long serialVersionUID = 1L;

    public Pawn(FigureColor figureColor) {
        super(FigureType.PAWN, figureColor);
    }

    public Pawn(FigureType figureType, FigureColor figureColor, boolean moved, boolean actioned) {
        super(figureType, figureColor, moved, actioned);
    }

    @Override
    public List<Action> getActions(Board board, Position position) {
        List<Action> actions = getEatActions(board, position);

        add(actions, moveForward(board, position, position.top(figureColor)));
        add(actions, moveDoubleForward(board, position, position.top(figureColor), position.topTop(figureColor)));

        return actions;
    }

    @Override
    public List<Action> getEatActions(Board board, Position position) {
        List<Action> actions = new ArrayList<>();

        add(actions, moveForward(board, position, position.top(figureColor)));
        add(actions, moveDoubleForward(board, position, position.top(figureColor), position.topTop(figureColor)));
        add(actions, eat(board, position, position.leftTop(figureColor)));
        add(actions, eat(board, position, position.rightTop(figureColor)));
        add(actions, take(board, position, position.left(figureColor)));
        add(actions, take(board, position, position.right(figureColor)));

        return actions;
    }

    @Override
    public Figure clone() {
        return new Pawn(figureType, figureColor, moved, actioned);
    }


    private Action eat(Board board, Position startPosition, Optional<Position> endPosition) {
        if (endPosition.isPresent()) {
            Position endPos = endPosition.get();
            if (board.hasOpponent(endPos, figureColor)) {
                if (endPos.isLastLine()) {
                    return Action.swap(startPosition, endPos, figureColor);
                } else {
                    return Action.eat(startPosition, endPos);
                }
            }
        }
        return null;
    }

    private Action take(Board board, Position startPosition, Optional<Position> positionOptional) {
        if (positionOptional.isPresent()) {
            var opponentPosition = positionOptional.get();
            if (board.isPawn(opponentPosition) //на позиции пешка?
                    && board.hasOpponent(opponentPosition, figureColor) //на позиции противник?
                    && board.wasSpecialMove(opponentPosition) //противник делал двойной ход?
            ) {
                return Action.take(startPosition, opponentPosition.top(figureColor).get(), opponentPosition);
            }
        }

        return null;
    }

    private Action moveForward(Board board, Position startPosition, Optional<Position> endPosition) {
        if (endPosition.isPresent()) {
            var endPos = endPosition.get();
            if (board.isNone(endPos)) { //позиция корректна и на позиции пусто?
                return endPos.isLastLine() ?  //это последняя линия поля?
                        Action.swap(startPosition, endPos, figureColor) :
                        Action.move(startPosition, endPos, figureType);
            }
        }
        return null;
    }

    private Action moveDoubleForward(Board board,
                                     Position startPosition,
                                     Optional<Position> midPosition,
                                     Optional<Position> endPosition) {
        if (endPosition.isPresent()) {
            Position endPos = endPosition.get();
            Position midPos = midPosition.get();
            if (board.wasNotMoving(startPosition)  //пешка не двигалась?
                    && board.hasAllNone(midPos, endPos)) // на позиции пусто?)
            {
                return Action.move(startPosition, endPos, figureType);
            }
        }
        return null;
    }
}
