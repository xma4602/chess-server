package com.chess.server.gamehistory;

import com.chess.engine.FigureColor;
import com.chess.engine.GameEngine;
import com.chess.engine.GameState;
import com.chess.server.gameconditions.GameConditions;
import com.chess.server.gameconditions.MatchMode;
import com.chess.server.gameplay.GamePlay;
import com.chess.server.user.User;
import com.chess.server.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameHistoryService {

    // Коэффициент K, который определяет, насколько сильно изменится рейтинг
    private static final int K = 40;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");


    private final GameHistoryRepository gameHistoryRepository;
    private final UserRepository userRepository;

    public void createGameHistory(GamePlay gamePlay) {
        User creator = gamePlay.getCreator();
        User opponent = gamePlay.getOpponent();

        int[] newRating;
        if (gamePlay.getGameConditions().getMatchMode() == MatchMode.RATING) {
            newRating = calculateRating(gamePlay);
        } else {
            newRating = new int[]{creator.getRating(), opponent.getRating(), 0, 0};
        }

        GameHistory gameHistory = GameHistory.builder()
                .gameConditions(gamePlay.getGameConditions())
                .gameState(gamePlay.getGameEngine().getGameState())
                .timestamp(gamePlay.getStartDateTime())
                .creator(creator)
                .opponent(opponent)
                .creatorRating(newRating[0])
                .opponentRating(newRating[1])
                .creatorRatingDifference(newRating[2])
                .opponentRatingDifference(newRating[3])
                .build();

        creator.setRating(newRating[0]);
        opponent.setRating(newRating[1]);
        userRepository.saveAll(List.of(creator, opponent));

        gameHistoryRepository.save(gameHistory);
    }


    private int[] calculateRating(GamePlay gamePlay) {
        User creator = gamePlay.getCreator();
        User opponent = gamePlay.getOpponent();
        GameConditions gameConditions = gamePlay.getGameConditions();
        GameEngine gameEngine = gamePlay.getGameEngine();

        Integer oldCreatorRating = creator.getRating();
        Integer oldOpponentRating = opponent.getRating();

        boolean creatorWin = (gameConditions.getCreatorFigureColor() == FigureColor.WHITE && gameEngine.getGameState() == GameState.WHITE_WIN)
                || (gameConditions.getCreatorFigureColor() == FigureColor.BLACK && gameEngine.getGameState() == GameState.BLACK_WIN);

        boolean opponentWin = (gameConditions.getOpponentFigureColor() == FigureColor.WHITE && gameEngine.getGameState() == GameState.WHITE_WIN)
                || (gameConditions.getOpponentFigureColor() == FigureColor.BLACK && gameEngine.getGameState() == GameState.BLACK_WIN);


        // Вычисление ожидаемого результата для игрока Creator
        double expectedScoreCreator = 1 / (1 + Math.pow(10, (oldOpponentRating - oldCreatorRating) / 400.0));
        // Вычисление ожидаемого результата для игрока Opponent
        double expectedScoreOpponent = 1 / (1 + Math.pow(10, (oldCreatorRating - oldOpponentRating) / 400.0));

        // Определение фактического результата
        double actualScoreCreator;
        double actualScoreOpponent;

        if (creatorWin) {
            actualScoreCreator = 1; // Игрок Creator выиграл
            actualScoreOpponent = 0; // Игрок Opponent проиграл
        } else if (opponentWin) {
            actualScoreCreator = 0; // Игрок Creator проиграл
            actualScoreOpponent = 1; // Игрок Opponent выиграл
        } else {
            actualScoreCreator = 0.5; // Ничья
            actualScoreOpponent = 0.5; // Ничья
        }

        // Перерасчет рейтингов
        int newCreatorRating = (int) Math.ceil(oldCreatorRating + K * (actualScoreCreator - expectedScoreCreator));
        int newOpponentRating = (int) Math.ceil(oldOpponentRating + K * (actualScoreOpponent - expectedScoreOpponent));

        return new int[]{
                newCreatorRating,
                newOpponentRating,
                newCreatorRating - oldCreatorRating,
                newOpponentRating - oldOpponentRating
        };

    }

    public List<GameHistoryDto> getHistory(UUID userId) {
        List<GameHistory> histories = gameHistoryRepository.getGameHistoriesByCreatorIdOrOpponentId(userId, userId);
        return histories.stream()
                .map(this::toGameHistoryDto)
                .toList();

    }

    private GameHistoryDto toGameHistoryDto(GameHistory gameHistory) {
        return GameHistoryDto.builder()
                .id(gameHistory.getId())
                .creatorLogin(gameHistory.getCreator().getLogin())
                .opponentLogin(gameHistory.getOpponent().getLogin())
                .creatorRating(gameHistory.getCreatorRating())
                .opponentRating(gameHistory.getOpponentRating())
                .creatorRatingDifference(gameHistory.getCreatorRatingDifference())
                .opponentRatingDifference(gameHistory.getOpponentRatingDifference())
                .gameState(gameHistory.getGameState())
                .gameConditions(gameHistory.getGameConditions())
                .timestamp(formatter.format(gameHistory.getTimestamp()))
                .build();
    }
}
