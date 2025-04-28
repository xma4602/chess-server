package com.chess.server.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/chess/chats")
@RequiredArgsConstructor
public class GameChatController {

    private final GameChatService gameChatService;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/{chatId}")
    public ResponseEntity<?> getChatMessages(@PathVariable UUID chatId) {
        try {
            List<GameChatMessageDto> gameChatMessages = gameChatService.getChatMessages(chatId);
            return ResponseEntity.ok(gameChatMessages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{chatId}")
    public ResponseEntity<?> takeMassage(@PathVariable UUID chatId, @RequestParam UUID userId, @RequestBody String message) {
        try {
            GameChatMessageDto gameChatMessage = gameChatService.pushMessage(chatId, userId, message);
            String destination = String.format("/topic/chats/%s/new", chatId);
            messagingTemplate.convertAndSend(destination, gameChatMessage);
            return ResponseEntity.ok(userId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
