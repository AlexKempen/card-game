package edu.utdallas.heartstohearts;

public class Card {
    private int rank = 0;
    private String suit = "";
    // Do we want one unique ID for each card?

    public Card(int r, String s) {
        this.rank = r;
        this.suit = s;
    }

    public int getRank() {
        return this.rank;
    }

    public String getSuit() {
        return this.suit;
    }

}
