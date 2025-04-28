package com.chess.server.gameplay;

import com.chess.engine.exceptions.ChessEngineIllegalArgumentException;
import com.chess.engine.exceptions.ChessEngineIllegalStateException;
import com.chess.server.gameplay.dto.GamePlayDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/chess/games")
public class GamePlayController {
    private final GameplayService gameplayService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("")
    ResponseEntity<UUID> create(@RequestParam UUID roomId) {
        GamePlay gameplay = gameplayService.createGameplay(roomId);
        log.info("Create game, id={}", gameplay.getId());

        String destination = String.format("/topic/rooms/%s/startGame", roomId);
        messagingTemplate.convertAndSend(destination, gameplay.getId().toString());
        log.info("Send at destination={} with payload={}", destination, gameplay.getId());

        return ResponseEntity.ok(gameplay.getId());
    }

    @GetMapping("/{gameId}")
    ResponseEntity<GamePlayDto> getGamePlay(@PathVariable UUID gameId) {
        GamePlayDto gameplay = gameplayService.getGamePlayDto(gameId);
        return ResponseEntity.ok(gameplay);
    }

    @PostMapping("/{gameId}/action")
    ResponseEntity<?> action(@PathVariable UUID gameId,
                             @RequestParam UUID userId,
                             @RequestParam String action) {
        try {
            GamePlayDto gamePlayDto = gameplayService.makeAction(gameId, userId, action);
            log.info("User in made action: userId={}, gameId={}, action={}", userId, gameId, action);
            String destination = String.format("/topic/games/%s/action", gameId);
            messagingTemplate.convertAndSend(destination, gamePlayDto);
            return ResponseEntity.ok().build();
        } catch (ChessEngineIllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (ChessEngineIllegalStateException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PutMapping("/{gameId}/timeout")
    ResponseEntity<?> timeout(@PathVariable UUID gameId, @RequestParam UUID userId) {
        Optional<GamePlayDto> dtoOptional = gameplayService.timeout(gameId, userId);
        dtoOptional.ifPresent(gamePlayDto -> {
            log.info("Game over by timeout: userId={}, gameId={}", userId, gameId);
            String destination = String.format("/topic/games/%s/action", gameId);
            messagingTemplate.convertAndSend(destination, dtoOptional);
        });
   
        return ResponseEntity.ok().build();
    }

//    @PostMapping("/games/end/{gameId}")
//    ResponseEntity<String> endGame(@PathVariable UUID gameId,
//                                   @RequestParam UUID userId,
//                                   @RequestBody GameResult gameResult) {
//        GameEnd gameEnd = gameplayService.endGame(gameId, userId, gameResult);
//        log.info("Game ID(%s) ended by %s".formatted(gameId, gameResult));
//        sendToUser(gameEnd.getNotifiedUserId(), "/games/end", gameEnd.getGameResult());
//        return ResponseEntity.ok(NOTHING);
//    }

//    @GetMapping("/games/ask-draw/{gameId}")
//    ResponseEntity<Boolean> askDraw(@PathVariable UUID gameId, @RequestParam UUID userId)
//            throws InterruptedException {
//        UUID otherUserId = gameplayService.getOtherUserId(gameId, userId);
//        Object waiter = new Object();
//        waitMap.put(userId, Map.entry(waiter, false));
//        sendToUser(otherUserId, "/games/ask-draw", "ask-draw");
//        synchronized (waiter) {
//            waiter.wait();
//        }
//        boolean answer = waitMap.remove(userId).getValue();
//        return ResponseEntity.ok(answer);
//    }

//    @PostMapping("/games/answer-draw/{gameId}")
//    ResponseEntity<String> answerDraw(@PathVariable UUID gameId,
//                                      @RequestParam UUID userId,
//                                      @RequestBody boolean answer) {
//        UUID otherUserId = gameplayService.getOtherUserId(gameId, userId);
//        Object waiter = waitMap.remove(otherUserId).getKey();
//        waitMap.put(otherUserId, Map.entry(waiter, answer));
//        synchronized (waiter) {
//            waiter.notifyAll();
//        }
//        return ResponseEntity.ok(NOTHING);
//    }

}
