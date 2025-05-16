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
@RequestMapping("/chess/rooms")
public class GameRoomController {

    private final GameRoomService gameRoomService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("")
    ResponseEntity<UUID> create(@RequestParam UUID creatorId, @RequestBody GameConditions conditions) {
        GameRoom gameRoom = gameRoomService.createGameRoom(creatorId, conditions);
        log.info("Create room: roomId={}, creatorId={}\n", gameRoom.getId(), creatorId);
        return ResponseEntity.ok(gameRoom.getId());
    }

    @PutMapping("/{roomId}/join")
    ResponseEntity<GameRoomDto> joinToRoom(@PathVariable UUID roomId, @RequestParam UUID userId) {
        GameRoomDto gameRoomDto = gameRoomService.connectToRoom(roomId, userId);
        log.info("User join to room: userId={}, roomId={}\n", userId, roomId);

        String destination = String.format("/topic/rooms/%s/join", roomId);
        messagingTemplate.convertAndSend(destination, gameRoomDto);

        return ResponseEntity.ok(gameRoomDto);
    }

    @DeleteMapping("/{roomId}/close")
    ResponseEntity<?> closeRoom(@PathVariable UUID roomId) {
        gameRoomService.closeRoom(roomId);

        String destination = String.format("/topic/rooms/%s/close", roomId);
        messagingTemplate.convertAndSend(destination, roomId);

        return ResponseEntity.ok().build();
    }
}
