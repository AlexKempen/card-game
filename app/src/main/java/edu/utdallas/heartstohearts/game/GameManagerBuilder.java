package edu.utdallas.heartstohearts.game;

import java.util.ArrayList;
import java.util.List;

public class GameManagerBuilder {
    public GameManagerBuilder(PlayerBuilder playerBuilder) {
        this.playerBuilder = playerBuilder;
    }

    public GameManagerBuilder() {
        this.playerBuilder = new PlayerBuilder();
    }

    public boolean heartsBroken = false;
    public PlayerBuilder playerBuilder;
    public PassDirection direction = PassDirection.LEFT;
    public List<Card> currentTrick = new ArrayList<>();
    public Suit trumpSuit;

    public GameManager make() {
        return new GameManager(playerBuilder.make(), direction, currentTrick, heartsBroken, trumpSuit);
    }

}
