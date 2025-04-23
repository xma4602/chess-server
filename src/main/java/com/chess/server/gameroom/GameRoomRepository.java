package com.chess.server.gameroom;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GameRoomRepository extends JpaRepository<GameRoom, UUID> {

    List<GameRoom> findGameRoomsByOpponentIdIsNull();

}
