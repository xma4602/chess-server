package com.chess.server.gameconditions;

import com.chess.engine.FigureColor;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class GameConditions implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private FigureColor figureColor;
    private MatchMode matchMode;
    private TimeControl timeControl;
    private int moveTime;
    private int partyTime;
}
