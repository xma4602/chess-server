package com.chess.server.chat;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameChatMessageDto {
    private UUID id;
    private UUID chatId;
    private UUID userId;
    private String userLogin;
    private String message;
    private String timestamp;
}
