package com.chess.server.gameconditions;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GameConditionsRepository extends JpaRepository<GameConditions, UUID> {
}
