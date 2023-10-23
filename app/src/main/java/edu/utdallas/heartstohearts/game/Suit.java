package edu.utdallas.heartstohearts.game;

public enum Suit {
    HEARTS, DIAMONDS, CLUBS, SPADES;

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
                return -1;
        }
    }

    public static Suit fromInt(int suiteIndex) {
        return new Suit[]{HEARTS, DIAMONDS, CLUBS, SPADES}[suiteIndex];
    }

    public String toString() {
        return new String[]{"Hearts", "Diamonds", "Clubs", "Spades"}[this.toInt()];
    }
}