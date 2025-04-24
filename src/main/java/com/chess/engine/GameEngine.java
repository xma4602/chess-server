package com.chess.engine;

import com.chess.engine.actions.Action;
import com.chess.engine.actions.ActionEat;
import com.chess.engine.exceptions.ChessEngineIllegalArgumentException;
import com.chess.engine.exceptions.ChessEngineIllegalStateException;
import com.chess.engine.exceptions.ChessEngineRuntimeException;
import com.chess.engine.figures.*;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class GameEngine {
    private final Board board;
    private final List<String> movedActions;
    private boolean activePlayerColor;
    private GameState gameState;
    private List<Action> whiteActions;
    private List<Action> blackActions;

    private static final Map<FigureType, AbstractFigure> figures = Map.of(
            FigureType.PAWN, new Pawn(),
            FigureType.KNIGHT, new Knight(),
            FigureType.BISHOP, new Bishop(),
            FigureType.ROOK, new Rook(),
            FigureType.QUEEN, new Queen(),
            FigureType.KING, new King()
    );

    public GameEngine() {
        board = Board.newBoard();
        activePlayerColor = true;
        movedActions = new ArrayList<>();
        whiteActions = CheckmateResolver.calcActions(board, FigureColor.WHITE);
        blackActions = CheckmateResolver.calcActions(board, FigureColor.BLACK);
        updateGameState();
    }

    public GameEngine(Board board, boolean activePlayerColor) {
        this.board = board;
        this.activePlayerColor = activePlayerColor;
        movedActions = new ArrayList<>();
        whiteActions = CheckmateResolver.calcActions(board, FigureColor.WHITE);
        blackActions = CheckmateResolver.calcActions(board, FigureColor.BLACK);
        updateGameState();
    }

    // Game State -------------------------------------------------------------------------------

    public GameState getGameState() {
        return gameState;
    }

    public String getBoardString() {
        return board.toString();
    }

    public boolean isGameNotEnded() {
        return gameState == GameState.CONTINUES;
    }

    public boolean getActivePlayerColor() {
        return activePlayerColor;
    }

    public Map.Entry<FigureType, FigureColor> getCellState(String cell) throws ChessEngineIllegalArgumentException {
        try {
            Position position = Position.of(cell);
            return Map.entry(board.typeBy(position), board.getFigureColor(position));
        } catch (ChessEngineRuntimeException e) {
            throw new ChessEngineIllegalArgumentException(e);
        }
    }

    public Map<String, Map.Entry<FigureType, FigureColor>> getBoardState() {
        Map<String, Map.Entry<FigureType, FigureColor>> states = new HashMap<>(64);
        for (var position : Position.values()) {
            states.put(position.toString(), Map.entry(board.typeBy(position), board.getFigureColor(position)));
        }
        return states;
    }

    public boolean getCellColor(String cellPosition) {
        return board.getCellColor(Position.of(cellPosition));
    }

    public List<Action> getActionsByPlayerColor(boolean isWhite) {
        return isWhite ? whiteActions : blackActions;
    }

    public List<Action> getActionsByPosition(String cellPosition) throws ChessEngineIllegalArgumentException {
        try {
            return CheckmateResolver.getActionsByPosition(board, Position.of(cellPosition));
        } catch (ChessEngineRuntimeException e) {
            throw new ChessEngineIllegalArgumentException(e);
        }
    }

    public List<String> getMadeActions() {
        return List.copyOf(movedActions);
    }

    // Game Actions -------------------------------------------------------------------------------

    public Action getActionBy(boolean playerColor, String startPosition, String endPosition) throws ChessEngineIllegalStateException, ChessEngineIllegalArgumentException {
        verifyGameEnd();
        verifyPlayerMoveOrder(playerColor);

        List<Action> actions = activePlayerColor ? whiteActions : blackActions;
        Optional<Action> actionOpt = findActionBy(actions, startPosition, endPosition);

        return actionOpt.orElseThrow(
                () -> new ChessEngineIllegalArgumentException("Не найдено доступного хода с начальной позицией %s и конечной %s".formatted(startPosition, endPosition))
        );
    }

    public Optional<Action> parseActionString(boolean playerColor, String actionString) {
        return Action.parse(actionString, playerColor).map(action -> (Action) action);
    }

    public void makeAction(boolean playerColor, Action action) throws ChessEngineIllegalArgumentException, ChessEngineIllegalStateException {
        verifyGameEnd();
        verifyPlayerMoveOrder(playerColor);

        List<Action> actions = activePlayerColor ? whiteActions : blackActions;

        if (!actions.contains(action)) {
            throw new ChessEngineIllegalArgumentException("Игрок не может сделать такой ход");
        } else {
            board.executeAction(action);
            activePlayerColor = !activePlayerColor;

            whiteActions = CheckmateResolver.calcActions(board, FigureColor.WHITE);
            blackActions = CheckmateResolver.calcActions(board, FigureColor.BLACK);

            updateGameState();
        }
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

    private void verifyPlayerMoveOrder(boolean playerColor) throws ChessEngineIllegalStateException {
        if (activePlayerColor != playerColor) {
            throw new ChessEngineIllegalStateException("Сейчас ходят " + (activePlayerColor ? "белые" : "черные"));
        }
    }

    private void updateGameState() {
        if (activePlayerColor && whiteActions.isEmpty()) {
            gameState = GameState.BLACK_WIN;
        } else if (!activePlayerColor && blackActions.isEmpty()) {
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
            return board.getFigurePositionsByColor(figureColor).stream()
                    .map(position -> getActionsByPosition(board, position))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
        }

        private static List<ActionEat> getEatActionsFor(Board board, FigureColor figureColor) {
            return board
                    .getFigurePositionsByColor(figureColor)
                    .stream()
                    .map(position -> getActionsByPosition(board, position))
                    .flatMap(Collection::stream)
                    .filter(action -> action.getActionType() == Action.ActionType.EAT)
                    .map(action -> (ActionEat) action)
                    .collect(Collectors.toList());
        }

        private static List<Action> getActionsByPosition(Board board, Position position) {
            return board.isNone(position) ?
                    Collections.emptyList() :
                    figures.get(board.typeBy(position)).getActions(board, position);
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

    public enum GameState {
        BLACK_WIN,
        WHITE_WIN,
        DRAW,
        CONTINUES
    }
}
