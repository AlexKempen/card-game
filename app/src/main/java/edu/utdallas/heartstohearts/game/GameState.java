package edu.utdallas.heartstohearts.game;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GameState {
    private List<Card> hand;
    private List<Card> trick;
    private PlayerAction action;
    private int points;
    private Suit trumpSuit;

    // To-do : Add a list of scores and a list of names so every player can know all players' scores

    public GameState(List<Card> hand, List<Card> trick, PlayerAction action, int points, Suit trumpSuit) {
        this.hand = hand;
        this.trick = trick;
        this.action = action;
        this.points = points;
        this.trumpSuit = trumpSuit;
    }

    public GameState(List<Card> hand, PlayerAction action) {
        this(hand, new ArrayList<>(), action, 0, null);
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
    public Suit getTrumpSuit() { return trumpSuit; }
}