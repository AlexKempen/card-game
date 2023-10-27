package edu.utdallas.heartstohearts.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Player implements Serializable {
    private String name;
    private int id;
    private int points;
    private List<Card> hand;
    private List<Card> tricks;
    private PlayerAction action;

    public Player(int id, String name, List<Card> hand, List<Card> tricks, PlayerAction action, int points) {
        this.id = id;
        this.name = name;
        this.tricks = tricks;
        this.hand = hand;
        this.points = points;
        this.action = action;
    }

    public void setAction(PlayerAction action) {
        this.action = action;
    }

    public PlayerAction getAction() {
        return action;
    }

    public int getId() {
        return id;
    }

    public void takeTrick(List<Card> trick) {
        this.tricks.addAll(trick);
    }

    public void clearTricks() {
        points += getTrickPoints();
        tricks.clear();
    }

    /**
     * Returns the points the current set of tricks is worth.
     */
    private int getTrickPoints() {
        return this.tricks.stream().mapToInt(Card::getPoints).sum();
    }

    public GameState getGameState() {
        return null;
    }
}
