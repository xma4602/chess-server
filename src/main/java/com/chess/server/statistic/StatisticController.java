package com.chess.server.statistic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Controller
@Slf4j
@RequiredArgsConstructor
public class StatisticController {
    private final StatisticService statisticService;

    @GetMapping("/user/stat")
    ResponseEntity<List<GameStatistic>> getStatistics(@RequestParam UUID userId) {
        List<GameStatistic> gameStatistic = statisticService.getStatistics(userId);
        log.info("Grub GameStatistic: %s".formatted(gameStatistic));
        return ResponseEntity.ok(gameStatistic);
    }

}
