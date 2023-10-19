package hearts2hearts;

import java.io.Serializable;

public class Card implements Serializable{
    private int suit, rank;
    public static final String[] suits = {"Clubs", "Spades", "Diamonds", "Hearts"};
    public static final String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};                               

    public Card(int suit, int rank){
        this.suit = suit;
        this.rank = rank;
    }

    public String toString() {
        String cardString = ranks[rank] + " of " + suits[suit];
        return cardString;
    }

}