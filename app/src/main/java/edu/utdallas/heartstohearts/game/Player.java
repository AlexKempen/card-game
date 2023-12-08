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

import java.io.Serializable;
import java.util.List;

public class Player implements Serializable {
    private final String nickname;
    private final int id;
    private int points;
    private List<Card> hand;
    private final List<Card> tricks;

    public Player(int id, String nickname, List<Card> hand, List<Card> tricks, int points) {
        this.id = id;
        this.nickname = nickname;
        this.tricks = tricks;
        this.hand = hand;
        this.points = points;
    }

    public String getNickname() {
        return nickname;
    }

    public void setHand(List<Card> hand) {
        this.hand = hand;
    }

    public List<Card> getHand() {
        return hand;
    }

    public int getPoints() {
        return points;
    }

    public void addToHand(List<Card> cards) {
        this.hand.addAll(cards);
    }

    public void removeFromHand(List<Card> cards) {
        this.hand.removeAll(cards);
    }

    public void removeFromHand(Card card) {
        this.hand.remove(card);
    }

    public int getId() {
        return id;
    }

    public void takeTrick(List<Card> trick) {
        this.tricks.addAll(trick);
    }

    /**
     * Adds a set amount of points to player.
     */
    public void addPoints(int points) {
        this.points += points;
    }

    /**
     * Adds the value of the trick to the player.
     */
    public void addTrickPoints() {
        this.points += this.getTrickPoints();
    }

    public void clearTricks() {
        tricks.clear();
    }

    /**
     * Returns the points the current set of tricks is worth.
     */
    public int getTrickPoints() {
        return this.tricks.stream().mapToInt(Card::getPoints).sum();
    }
}
