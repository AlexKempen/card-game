package edu.utdallas.heartstohearts.game;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.SortedMap;

public class GameManagerBuilder {
    public GameManagerBuilder(PlayerBuilder playerBuilder) {
        this.playerBuilder = playerBuilder;
    }

    public GameManagerBuilder() {
        this.playerBuilder = new PlayerBuilder();
    }

    public boolean heartsBroken = false;
    public boolean firstTrick = true;
    public Integer currentPlayerId = null;
    public PlayerBuilder playerBuilder;
    public PassDirection direction = PassDirection.LEFT;
    public LinkedHashMap<Card, Integer> currentTrick = new LinkedHashMap<>();

    public GamePhase phase = GamePhase.DEAL;

    public GameManager make() {
        return new GameManager(playerBuilder.make(), direction, currentTrick, phase, currentPlayerId, heartsBroken, firstTrick);
    }

}
