package com.chess.engine;

import com.chess.engine.actions.Action;
import com.chess.engine.actions.ActionEat;
import com.chess.engine.exceptions.ChessEngineIllegalArgumentException;
import com.chess.engine.exceptions.ChessEngineIllegalStateException;
import com.chess.engine.exceptions.ChessEngineRuntimeException;
import com.chess.engine.figures.Figure;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

@SuppressWarnings("unused")
public class GameEngine implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final Board board;
    private final List<Action> madeActions;
    private FigureColor activePlayerColor;
    private GameState gameState;
    private List<Action> whiteActions;
    private List<Action> blackActions;

    public GameEngine() {
        board = Board.newBoard();
        activePlayerColor = FigureColor.WHITE;
        madeActions = new ArrayList<>();
        whiteActions = CheckmateResolver.calcActions(board, FigureColor.WHITE);
        blackActions = CheckmateResolver.calcActions(board, FigureColor.BLACK);
        updateGameState();
    }

    // Game State -------------------------------------------------------------------------------

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public GameState getGameState() {
        return gameState;
    }

    public String getBoardString() {
        return board.toString();
    }

    public boolean isGameNotEnded() {
        return gameState == GameState.CONTINUES;
    }

    public FigureColor getActivePlayerColor() {
        return activePlayerColor;
    }

    public Map.Entry<FigureType, FigureColor> getCellState(String cell) throws ChessEngineIllegalArgumentException {
        try {
            Position position = Position.of(cell);
            Figure figure = board.getFigureByPosition(position);
            return Map.entry(figure.getFigureType(), figure.getFigureColor());
        } catch (ChessEngineRuntimeException e) {
            throw new ChessEngineIllegalArgumentException(e);
        }
    }

    public Map<Position, Figure> getBoardState() {
        return board.getCells();
    }

    public boolean getCellColor(String cellPosition) {
        return board.getCellColor(Position.of(cellPosition));
    }

    public List<Action> getActionsByPlayerColor(FigureColor figureColor) {
        return figureColor.isWhite() ? whiteActions : blackActions;
    }

    public List<Action> getActionsByPosition(String cellPosition) throws ChessEngineIllegalArgumentException {
        try {
            return CheckmateResolver.getActionsByPosition(board, Position.of(cellPosition));
        } catch (ChessEngineRuntimeException e) {
            throw new ChessEngineIllegalArgumentException(e);
        }
    }

    public List<Action> getMadeActions() {
        return List.copyOf(madeActions);
    }

    // Game Actions -------------------------------------------------------------------------------

    public Action getActionBy(FigureColor playerColor, String startPosition, String endPosition) throws ChessEngineIllegalStateException, ChessEngineIllegalArgumentException {
        verifyGameEnd();
        verifyPlayerMoveOrder(playerColor);

        List<Action> actions = activePlayerColor == FigureColor.WHITE ? whiteActions : blackActions;
        Optional<Action> actionOpt = findActionBy(actions, startPosition, endPosition);

        return actionOpt.orElseThrow(
                () -> new ChessEngineIllegalArgumentException("Не найдено доступного хода с начальной позицией %s и конечной %s".formatted(startPosition, endPosition))
        );
    }

    public void makeAction(FigureColor playerColor, Action action) throws ChessEngineIllegalArgumentException, ChessEngineIllegalStateException {
        verifyGameEnd();
        verifyPlayerMoveOrder(playerColor);
        verifyAction(action);

        board.executeAction(action);
        madeActions.add(action);
        activePlayerColor = activePlayerColor.reverseColor();

        whiteActions = CheckmateResolver.calcActions(board, FigureColor.WHITE);
        blackActions = CheckmateResolver.calcActions(board, FigureColor.BLACK);

        updateGameState();

    }

    private void verifyAction(Action verifingAction) throws ChessEngineIllegalArgumentException {
        List<Action> actions = activePlayerColor == FigureColor.WHITE ? whiteActions : blackActions;
        for (Action action : actions) {
            if (action.equals(verifingAction)) return;
        }
        throw new ChessEngineIllegalArgumentException("Игрок не может сделать такой ход");
    }

    public void makeDefeat(boolean playerColor) {
        gameState = playerColor ? GameState.BLACK_WIN : GameState.WHITE_WIN;
    }

    public void makeDraw() {
        gameState = GameState.DRAW;
    }

    // game inner operations ---------------------------------------------------------------------------------

    private Optional<Action> findActionBy(List<Action> actions, String startPosition, String endPosition) {
        return actions.stream()
                .filter(action -> action.equalsPositions(startPosition, endPosition))
                .findFirst();
    }

    private void verifyGameEnd() throws ChessEngineIllegalStateException {
        if (!isGameNotEnded()) {
            throw new ChessEngineIllegalStateException("Игра завершена и нельзя выполнять ходы");
        }
    }

    private void verifyPlayerMoveOrder(FigureColor playerColor) throws ChessEngineIllegalStateException {
        if (activePlayerColor != playerColor) {
            throw new ChessEngineIllegalStateException("Сейчас ходят " + (activePlayerColor.isWhite() ? "белые" : "черные"));
        }
    }

    private void updateGameState() {
        if (activePlayerColor == FigureColor.WHITE && whiteActions.isEmpty()) {
            gameState = GameState.BLACK_WIN;
        } else if (activePlayerColor == FigureColor.BLACK && blackActions.isEmpty()) {
            gameState = GameState.WHITE_WIN;
        } else gameState = GameState.CONTINUES;
    }

    private static class CheckmateResolver {

        public static List<Action> calcActions(Board board, FigureColor figureColor) {
            List<Action> actions = getActionsFor(board, figureColor);
            List<Action> results = new ArrayList<>(actions.size());

            for (Action action : actions) {
                Board boardClone = board.clone();
                boardClone.executeAction(action);
                List<ActionEat> opponentActions = getEatActionsFor(boardClone, figureColor.reverseColor());
                Position kingPosition = boardClone.findKing(figureColor);
                if (notContainsAttackOnKing(opponentActions, kingPosition)) {
                    results.add(action);
                }
            }

            return results;
        }

        private static List<Action> getActionsFor(Board board, FigureColor figureColor) {
            List<Action> actions = new ArrayList<>();
            List<Position> figurePositionsByColor = board.getFigurePositionsByColor(figureColor);
            for (Position position : figurePositionsByColor) {
                List<Action> actionsByPosition = getActionsByPosition(board, position);
                actions.addAll(actionsByPosition);
            }
            return actions;
        }

        private static List<ActionEat> getEatActionsFor(Board board, FigureColor figureColor) {
            List<ActionEat> actionEats = new ArrayList<>();
            List<Position> figurePositionsByColor = board.getFigurePositionsByColor(figureColor);
            for (Position position : figurePositionsByColor) {
                List<Action> actionsByPosition = getActionsByPosition(board, position);
                for (Action action : actionsByPosition) {
                    if (action.getActionType() == Action.ActionType.EAT) {
                        ActionEat actionEat = (ActionEat) action;
                        actionEats.add(actionEat);
                    }
                }
            }
            return actionEats;
        }

        private static List<Action> getActionsByPosition(Board board, Position position) {
            return board.getFigureByPosition(position).getActions(board, position);
        }

        private static boolean notContainsAttackOnKing(List<ActionEat> actions, Position kingPosition) {
            for (var action : actions) {
                if (action.getEatenPosition().equals(kingPosition)) {
                    return false;
                }
            }
            return true;
        }
    }

    @Override
    public String toString() {
        return board.toString();
    }

    public enum GameState implements Serializable {
        BLACK_WIN,
        WHITE_WIN,
        DRAW,
        CONTINUES
    }
}
