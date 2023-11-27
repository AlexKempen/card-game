package edu.utdallas.heartstohearts.game;

import java.io.Serializable;

public enum Rank implements Serializable {
    TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE;

    private static final long serialVersionUID = 4630732092238719595L;

    public int toInt() {
        switch (this) {
            case TWO:
                return 0;
            case THREE:
                return 1;
            case FOUR:
                return 2;
            case FIVE:
                return 3;
            case SIX:
                return 4;
            case SEVEN:
                return 5;
            case EIGHT:
                return 6;
            case NINE:
                return 7;
            case TEN:
                return 8;
            case JACK:
                return 9;
            case QUEEN:
                return 10;
            case KING:
                return 11;
            case ACE:
                return 12;
            default:
                throw new AssertionError("Unhandled rank");
        }
    }

    public static Rank fromInt(int rankIndex) {
        return new Rank[]{TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE}[rankIndex];
    }

    public String toString() {
        return new String[]{"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"}[this.toInt()];
    }
}