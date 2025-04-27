package com.chess.server.gameroom;

import com.chess.server.gameconditions.GameConditions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/chess") // Устанавливаем корневой путь для всех методов в этом контроллере
@CrossOrigin(origins="http://localhost:4200")
public class GameRoomController {

    private final GameRoomService guestRoomService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/rooms")
    ResponseEntity<UUID> create(@RequestParam UUID creatorId, @RequestBody GameConditions conditions) {
        GameRoom gameRoom = guestRoomService.createGameRoom(creatorId, conditions);
        log.info("Create room: roomId={}, creatorId={}", gameRoom.getId(), creatorId);
        return ResponseEntity.ok(gameRoom.getId());
    }

    @PutMapping("/rooms/{roomId}/join")
    ResponseEntity<GameRoom> joinToRoom(@PathVariable UUID roomId, @RequestParam UUID userId) {
        GameRoom gameRoom = guestRoomService.connectToRoom(roomId, userId);
        log.info("User join to room: userId={}, roomId={}", userId, roomId);

        String destination = String.format("/rooms/%s/join", roomId);
        messagingTemplate.convertAndSend(destination, gameRoom);
        log.info("Send to user={} at destination={} with payload={}", userId, destination, gameRoom);

        return ResponseEntity.ok(gameRoom);
    }

}
