package com.chess.server.gameroom;

import com.chess.server.gameconditions.GameConditions;
import com.chess.server.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GameRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @ManyToOne
    @JoinColumn(name = "opponent_id")
    private User opponent;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn
    private GameConditions gameConditions;

    public GameRoom(User creator, GameConditions gameConditions) {
        this.creator = creator;
        this.gameConditions = gameConditions;
    }

}
