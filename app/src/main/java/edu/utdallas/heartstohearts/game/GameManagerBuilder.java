/**
 * Hearts to Hearts project
 * Senior design project, University of Texas at Dallas CS 4485.0W1
 * Fall 2023
 * <p>
 * File authors:
 * - Alex Kempen
 * - Jacob Baskins
 */
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
