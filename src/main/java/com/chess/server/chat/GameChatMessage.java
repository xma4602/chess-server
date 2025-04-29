package com.chess.server.chat;

import com.chess.server.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class GameChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "game_chat_id", nullable = false)
    private GameChat gameChat;

    @Column(name = "message", nullable = false)
    private String message;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    public GameChatMessage(GameChat gameChat, User user, String message, LocalDateTime timestamp) {
        this.gameChat = gameChat;
        this.user = user;
        this.message = message;
        this.timestamp = timestamp;
    }
}
