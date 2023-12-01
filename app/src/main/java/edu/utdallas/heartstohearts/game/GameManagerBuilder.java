package edu.utdallas.heartstohearts.game;

import java.util.LinkedHashMap;

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

    public GameManager build() {
        return new GameManager(playerBuilder.build(), direction, currentTrick, phase, currentPlayerId, heartsBroken, firstTrick);
    }

}
