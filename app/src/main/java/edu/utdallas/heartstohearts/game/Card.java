package edu.utdallas.heartstohearts.game;

import java.io.Serializable;

public class Card implements Serializable {
    private Suit suit;
    private Rank rank;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    /**
     * @param id : A card id, ranging from 0 to 51.
     */
    public Card(int id) {
        this(Suit.fromInt(id / 13), Rank.fromInt(id % 13));
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    public String toString() {
        return rank.toString() + " of " + suit.toString();
    }
}
