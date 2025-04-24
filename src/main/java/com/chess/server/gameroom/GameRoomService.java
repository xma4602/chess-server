package com.chess.server.gameroom;

import com.chess.server.gameconditions.GameConditions;
import com.chess.server.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameRoomService {

    private final GameRoomRepository guestRoomRepository;
    private final UserService userService;

    public GameRoom createGameRoom(UUID creatorId, GameConditions gameConditions) {
        String userLogin = userService.getUserLogin(creatorId);
        GameRoom gameRoom = new GameRoom(creatorId, userLogin, gameConditions);
        return guestRoomRepository.save(gameRoom);
    }

    public GameRoom connectToRoom(UUID roomId, UUID userId) {
        GameRoom room = getGameRoom(roomId);
        String userLogin = userService.getUserLogin(userId);

        if (room.getCreatorId().equals(userId)) {
            room.setCreatorId(userId);
            room.setCreatorLogin(userLogin);
        } else {
            room.setOpponentId(userId);
            room.setOpponentLogin(userLogin);
        }
        return guestRoomRepository.save(room);
    }

    public GameRoom getGameRoom(UUID guestRoomId) {
        Optional<GameRoom> roomOpt = guestRoomRepository.findById(guestRoomId);
        return roomOpt.orElseThrow(
                () -> new NoSuchElementException("Гостевой комнаты с ID(%s) не существует".formatted(guestRoomId))
        );
    }

    public void delete(UUID roomId) {
        guestRoomRepository.deleteById(roomId);
    }
}
