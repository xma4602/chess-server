package com.chess.server.gameroom;

import com.chess.server.gameconditions.GameConditions;
import com.chess.server.user.User;
import com.chess.server.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameRoomService {

    private final GameRoomRepository guestRoomRepository;
    private final UserRepository userRepository;

    public GameRoom createGameRoom(UUID creatorId, GameConditions gameConditions) {
        User user = userRepository.findById(creatorId)
                .orElseThrow(() -> new NoSuchElementException("No such User with id=" + creatorId));
        GameRoom gameRoom = new GameRoom(user, gameConditions);
        return guestRoomRepository.save(gameRoom);
    }

    public GameRoomDto connectToRoom(UUID roomId, UUID userId) {
        GameRoom room = getGameRoom(roomId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("No such User with id=" + userId));

        if (room.getCreator().getId().equals(userId)) {
            room.setCreator(user);
        } else {
            room.setOpponent(user);

        }
        room = guestRoomRepository.save(room);
        return toGameRoomDto(room);
    }

    public GameRoomDto toGameRoomDto(GameRoom gameRoom) {
        return GameRoomDto.builder()
                .id(gameRoom.getId())
                .creatorId(gameRoom.getCreator().getId())
                .creatorLogin(gameRoom.getCreator().getLogin())
                .creatorRating(gameRoom.getCreator().getRating())
                .opponentRating(gameRoom.getOpponent() != null ? gameRoom.getOpponent().getRating() : null)
                .opponentId(gameRoom.getOpponent() != null ? gameRoom.getOpponent().getId() : null)
                .opponentLogin(gameRoom.getOpponent() != null ? gameRoom.getOpponent().getLogin() : null)
                .gameConditions(gameRoom.getGameConditions())
                .build();
    }

    public GameRoom getGameRoom(UUID guestRoomId) {
        Optional<GameRoom> roomOpt = guestRoomRepository.findById(guestRoomId);
        return roomOpt.orElseThrow(
                () -> new NoSuchElementException("Гостевой комнаты с ID(%s) не существует".formatted(guestRoomId))
        );
    }

    public void closeRoom(UUID roomId) {
        GameRoom gameRoom = getGameRoom(roomId);
        guestRoomRepository.deleteById(gameRoom.getId());
    }
}
