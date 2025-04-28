package com.chess.server.chat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GameChatRepository extends JpaRepository<GameChat, UUID> {
}
