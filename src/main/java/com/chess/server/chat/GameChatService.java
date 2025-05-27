package com.chess.server.chat;

import com.chess.server.gameplay.GamePlay;
import com.chess.server.user.User;
import com.chess.server.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class GameChatService {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private final GameChatRepository gameChatRepository;
    private final UserRepository userRepository;
    private final GameChatMessageRepository gameChatMessageRepository;

    public GameChatMessageDto pushMessage(UUID chatId, UUID userId, String message) {
        GameChat gameChat = gameChatRepository.findById(chatId)
                .orElseThrow(() -> new NoSuchElementException("No such GameChat with id=" + chatId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("No such User with id=" + userId));

        GameChatMessage gameChatMessage = new GameChatMessage(gameChat, user, message, LocalDateTime.now());
        gameChatMessage = gameChatMessageRepository.save(gameChatMessage);
        log.info("created new game chat message: id={}, chatId={}, message={}", gameChatMessage.getId(), gameChat.getId(), message);
        return toGameChatMessageDto(gameChatMessage);
    }

    public void createChat(GamePlay gamePlay) {
        GameChat newGameChat = gameChatRepository.save(new GameChat(gamePlay));
        log.info("created new game chat: id={}, gameId={}", newGameChat.getId(), gamePlay.getId());
    }

    public List<GameChatMessageDto> getChatMessages(UUID chatId) {
        GameChat gameChat = gameChatRepository.findById(chatId)
                .orElseThrow(() -> new NoSuchElementException("No such GameChat with id=" + chatId));
        return gameChat.getGameChatMessages().stream()
                .map(this::toGameChatMessageDto)
                .toList();
    }

    private GameChatMessageDto toGameChatMessageDto(GameChatMessage gameChatMessage){
        return GameChatMessageDto.builder()
                .id(gameChatMessage.getId())
                .chatId(gameChatMessage.getGameChat().getId())
                .userId(gameChatMessage.getUser().getId())
                .userLogin(gameChatMessage.getUser().getLogin())
                .message(gameChatMessage.getMessage())
                .timestamp(gameChatMessage.getTimestamp().format(dateTimeFormatter))
                .build();
    }
}
