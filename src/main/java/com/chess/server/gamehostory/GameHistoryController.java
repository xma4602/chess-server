package com.chess.server.gamehostory;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/chess/history")
@RequiredArgsConstructor
public class GameHistoryController {

    private final GameHistoryService gameHistoryService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<GameHistoryDto>> getHistory(@PathVariable UUID userId) {
        List<GameHistoryDto> histories = gameHistoryService.getHistory(userId);
        return ResponseEntity.ok(histories);
    }
}
