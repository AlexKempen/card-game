package edu.utdallas.heartstohearts;

public enum Suit {
    HEARTS, DIAMONDS, CLUBS, SPADES;

    public int toInt(){
        // Don't reorder this! some of the other logic in this file relies on this ordering
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

    public static Suit fromInt(int suite_index){
        return new Suit[]{HEARTS, DIAMONDS, CLUBS, SPADES}[suite_index];
    }

    public String toString(){
        return new String[]{"Hearts", "Diamonds", "Clubs", "Spades"}[this.toInt()];
    }
}
