package com.chess.server.gamehistory;

import com.chess.engine.GameState;
import com.chess.server.gameconditions.GameConditions;
import com.chess.server.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "opponent_id", nullable = false)
    private User opponent;

    @Column(name = "creator_rating", nullable = false)
    private Integer creatorRating;

    @Column(name = "opponent_rating", nullable = false)
    private Integer opponentRating;

    @Column(name = "creator_rating_difference", nullable = false)
    private Integer creatorRatingDifference;

    @Column(name = "opponent_rating_difference", nullable = false)
    private Integer opponentRatingDifference;

    @OneToOne(cascade = CascadeType.ALL, optional = false, orphanRemoval = true)
    @JoinColumn(name = "game_conditions_id", nullable = false)
    private GameConditions gameConditions;

    @Enumerated
    @Column(name = "game_state", nullable = false)
    private GameState gameState;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

}
