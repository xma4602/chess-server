package com.chess.server.chat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GameChatMessageRepository extends JpaRepository<GameChatMessage, UUID> {
}
