package com.chess.server.chat;

import com.chess.server.gameplay.GamePlay;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class GameChat {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    public GameChat(GamePlay gamePlay) {
        this.gamePlay = gamePlay;
    }

    @OneToOne(optional = false)
    @JoinColumn(name = "game_play_id", nullable = false, unique = true)
    private GamePlay gamePlay;

    @Builder.Default
    @OneToMany(mappedBy = "gameChat", cascade = CascadeType.ALL)
    private List<GameChatMessage> gameChatMessages = new ArrayList<>();

}
