package com.chess.server.gameplay;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GameplayRepository extends JpaRepository<GamePlay, UUID> {
    Optional<GamePlay> getGameplayByCreatorId(UUID userId);
}
