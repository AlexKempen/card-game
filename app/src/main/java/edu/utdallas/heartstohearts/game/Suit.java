package edu.utdallas.heartstohearts.game;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public enum Suit implements Serializable {

    HEARTS, DIAMONDS, CLUBS, SPADES;
    private static final long serialVersionUID = 7865183916468303480L;

    public int toInt() {
        switch (this) {
            case HEARTS:
                return 0;
            case DIAMONDS:
                return 1;
            case CLUBS:
                return 2;
            case SPADES:
                return 3;
            default:
                throw new AssertionError("Unhandled suit");
        }
    }

    public static Suit fromInt(int suiteIndex) {
        return new Suit[]{HEARTS, DIAMONDS, CLUBS, SPADES}[suiteIndex];
    }

    public String toString() {
        return new String[]{"Hearts", "Diamonds", "Clubs", "Spades"}[this.toInt()];
    }

    public List<Card> filterBySuit(List<Card> cards) {
        return cards.stream().filter(c -> c.getSuit() == this).collect(Collectors.toList());
    }
}