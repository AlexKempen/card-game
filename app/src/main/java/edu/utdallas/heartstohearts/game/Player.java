package edu.utdallas.heartstohearts.game;

import java.io.Serializable;
import java.util.List;

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

    public void setHand(List<Card> hand) {
        this.hand = hand;
    }

    //sets action and determines legality of all cards
    public void setAction(PlayerAction action, Suit trumpSuit, boolean heartsBroken) {
        this.action = action;

        switch (action) {
            case WAIT: // if not player's turn to play or pass cards, player can't select any card
                hand.forEach(c -> c.setSelectable(false));
                break;
            case CHOOSE_CARDS: // all cards are selectable to be passed
                hand.forEach(c -> c.setSelectable(true));
                break;
            case PLAY_CARD:
                // determine playable cards based on trump suit
                // if leading a card, all cards are playable except maybe Hearts
                if (trumpSuit == null) {
                    // if hand contains 2 of Clubs, that is the only playable card
                    if (hand.contains(Card.TWO_OF_CLUBS)) {
                        hand.forEach(c -> c.setSelectable(c.equals(Card.TWO_OF_CLUBS)));
                    }
                    // all cards are playable except maybe Hearts
                    else if (heartsBroken) {
                        hand.forEach(c -> c.setSelectable(true));
                    } else {
                        hand.forEach(c -> c.setSelectable(!c.getSuit().equals(Suit.HEARTS)));
                    }
                }
                // if not leading a card, only trump suit cards are playable unless player has no trump cards
                else {
                    if (hand.stream().anyMatch(c -> c.getSuit() == trumpSuit)) {
                        hand.forEach(c -> c.setSelectable(c.getSuit() == trumpSuit));
                    }
                    // player has no trump, all cards are playable
                    else {
                        hand.forEach(c -> c.setSelectable(false));
                    }
                }
                break;
        }
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

    public PlayerAction getAction() {
        return action;
    }

    public int getId() {
        return id;
    }

    public void takeTrick(List<Card> trick) {
        this.tricks.addAll(trick);
    }

    public void addPoints(int points) {
        this.points += points;
    }

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
