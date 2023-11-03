package edu.utdallas.heartstohearts.game;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private List<Card> hand;
    private List<Card> trick;
    private PlayerAction action;
    private int points;

    public GameState(List<Card> hand, List<Card> trick, PlayerAction action, int points) {
        this.hand = hand;
        this.trick = trick;
        this.action = action;
        this.points = points;
    }

    public GameState(List<Card> hand, PlayerAction action) {
        this(hand, new ArrayList<>(), action, 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameState gameState = (GameState) o;
        return hand.equals(gameState.hand) && trick.equals(gameState.trick) && action == gameState.action;
    }

    public List<Card> getHand() {
        return hand;
    }

    public List<Card> getTrick() {
        return trick;
    }

    public PlayerAction getAction() {
        return action;
    }

    public int getPoints() {
        return points;
    }
}