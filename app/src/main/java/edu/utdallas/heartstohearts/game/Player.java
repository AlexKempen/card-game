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
                for (Card c : hand) {
                    c.setSelectable(false);
                }
                break;
            case CHOOSE_CARDS: // all cards are selectable to be passed
                for (Card c : hand) {
                    c.setSelectable(true);
                }
                break;
            case PLAY_CARD:
                // determine playable cards based on trump suit
                // if leading a card, all cards are playable except maybe Hearts
                if (trumpSuit == null) {
                    // if hand contains 2 of Clubs, that is the only playable card
                    boolean hasTwoOfClubs = false;
                    for (Card c : hand) {
                        if (c.equals(Card.TWO_OF_CLUBS)) {
                            hasTwoOfClubs = true;
                        }
                    }
                    if (hasTwoOfClubs) {
                        for (Card c : hand) {
                            if (c.equals(Card.TWO_OF_CLUBS)) {
                                c.setSelectable(true);
                            } else c.setSelectable(false);
                        }
                    }

                    // if leading a card and hand doesn't contain 2 of Clubs (it's not the 1st trick)
                    // all cards are playable except maybe Hearts
                    else if (heartsBroken) {
                        for (Card c : hand) {
                            c.setSelectable(true);
                        }
                    } else {
                        for (Card c : hand) {
                            if (!c.getSuit().equals(Suit.HEARTS)) {
                                c.setSelectable(true);
                            } else c.setSelectable(false);
                        }
                    }
                }

                // if not leading a card, only trump suit cards are playable unless player has no trump cards
                else {
                    boolean hasTrump = false;
                    for (Card c : hand) {
                        if (c.getSuit() == trumpSuit) {
                            hasTrump = true;
                            break;
                        }
                    }
                    if (hasTrump) {
                        for (Card c : hand) {
                            if (c.getSuit() == trumpSuit) {
                                c.setSelectable(true);
                            } else c.setSelectable(false);
                        }
                    }
                    // player has no trump, all cards are playable
                    else {
                        for (Card c : hand) {
                            c.setSelectable(true);
                        }
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

    public void addTrickPoints() {
        if (getTrickPoints() != 26) {
            points += getTrickPoints();
        }
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

    /**
     * Currently only used for shooting the moon and adding 26 points
     *
     * @param toAdd could be removed and method could just be add26Points()
     */
    public void addSpecificPoints(int toAdd) {
        points += toAdd;
    }

}
