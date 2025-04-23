package com.chess.server.gameplay;

import com.chess.server.gameplay.dto.GameEnd;
import com.chess.server.statistic.GameResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@Slf4j
@RequiredArgsConstructor
public class GamePlayController {
    private final GameplayService gameplayService;
    private final SimpMessagingTemplate messagingTemplate;
    private final static String NOTHING = "ok";
    private final static Map<UUID, Map.Entry<Object, Boolean>> waitMap = new HashMap<>();

    @PostMapping("/games")
    ResponseEntity<UUID> create(@RequestBody UUID roomId) {
        GamePlay gameplay = gameplayService.createGameplay(roomId);
        log.info("Create game, id={}", gameplay.getId());

        String destination = String.format("/rooms/%s/join", roomId);
        messagingTemplate.convertAndSend(destination, gameplay.getId());
        log.info("Send at destination={} with payload={}", destination, gameplay.getId());

        return ResponseEntity.ok(gameplay.getId());
    }

    @GetMapping("/games/{gameId}")
    ResponseEntity<GamePlay> getGamePlay(@PathVariable UUID gameId) {
        GamePlay gameplay = gameplayService.getGameplay(gameId);
        return ResponseEntity.ok(gameplay);
    }

    @PostMapping("/games/action/{gameId}")
    ResponseEntity<Boolean> action(@PathVariable UUID gameId,
                                   @RequestParam UUID userId,
                                   @RequestBody String action) {
        UUID notifiedUserId = gameplayService.makeAction(gameId, userId, action);
        log.info("User ID(%s) in game ID(%s) made action %s".formatted(userId, gameId, action));
        sendToUser(notifiedUserId, "/games/action", action);
        return ResponseEntity.ok(true);
    }

    @PostMapping("/games/end/{gameId}")
    ResponseEntity<String> endGame(@PathVariable UUID gameId,
                                   @RequestParam UUID userId,
                                   @RequestBody GameResult gameResult) {
        GameEnd gameEnd = gameplayService.endGame(gameId, userId, gameResult);
        log.info("Game ID(%s) ended by %s".formatted(gameId, gameResult));
        sendToUser(gameEnd.getNotifiedUserId(), "/games/end", gameEnd.getGameResult());
        return ResponseEntity.ok(NOTHING);
    }

    @GetMapping("/games/ask-draw/{gameId}")
    ResponseEntity<Boolean> askDraw(@PathVariable UUID gameId, @RequestParam UUID userId)
            throws InterruptedException {
        UUID otherUserId = gameplayService.getOtherUserId(gameId, userId);
        Object waiter = new Object();
        waitMap.put(userId, Map.entry(waiter, false));
        sendToUser(otherUserId, "/games/ask-draw", "ask-draw");
        synchronized (waiter) {
            waiter.wait();
        }
        boolean answer = waitMap.remove(userId).getValue();
        return ResponseEntity.ok(answer);
    }

    @PostMapping("/games/answer-draw/{gameId}")
    ResponseEntity<String> answerDraw(@PathVariable UUID gameId,
                                      @RequestParam UUID userId,
                                      @RequestBody boolean answer) {
        UUID otherUserId = gameplayService.getOtherUserId(gameId, userId);
        Object waiter = waitMap.remove(otherUserId).getKey();
        waitMap.put(otherUserId, Map.entry(waiter, answer));
        synchronized (waiter) {
            waiter.notifyAll();
        }
        return ResponseEntity.ok(NOTHING);
    }


    private void sendToUser(UUID userId, String destination, Object payload) {
        sendToUser(userId.toString(), destination, payload);
    }

    private void sendToUser(String user, String destination, Object payload) {
        messagingTemplate.convertAndSendToUser(user, destination, payload);
        log.info("Send to user(%s) at destination(%s) with payload(%s)".formatted(user, destination, payload));
    }
}
