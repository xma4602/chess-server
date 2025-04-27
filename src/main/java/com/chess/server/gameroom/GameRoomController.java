package com.chess.server.gameroom;

import com.chess.server.gameconditions.GameConditions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/chess/rooms") // Устанавливаем корневой путь для всех методов в этом контроллере
public class GameRoomController {

    private final GameRoomService guestRoomService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("")
    ResponseEntity<UUID> create(@RequestParam UUID creatorId, @RequestBody GameConditions conditions) {
        GameRoom gameRoom = guestRoomService.createGameRoom(creatorId, conditions);
        log.info("Create room: roomId={}, creatorId={}\n", gameRoom.getId(), creatorId);
        return ResponseEntity.ok(gameRoom.getId());
    }

    @SubscribeMapping("/topic/rooms/{roomId}/join")
    public void subscribeJoin(@DestinationVariable UUID roomId){
        log.info("subscribe join roomId={}\n", roomId);
    }

    @SubscribeMapping("/topic/rooms/{roomId}/startGame")
    public void subscribeStartGame(@DestinationVariable UUID roomId){
        log.info("subscribe join roomId={}\n", roomId);
    }

    @PutMapping("/{roomId}/join")
    ResponseEntity<GameRoom> joinToRoom(@PathVariable UUID roomId, @RequestParam UUID userId) {
        GameRoom gameRoom = guestRoomService.connectToRoom(roomId, userId);
        log.info("User join to room: userId={}, roomId={}\n", userId, roomId);

        String destination = String.format("/topic/rooms/%s/join", roomId);
        messagingTemplate.convertAndSend(destination, gameRoom);

        return ResponseEntity.ok(gameRoom);
    }

}
