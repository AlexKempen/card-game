package edu.utdallas.hearts2hearts;

import java.io.Serializable;
import java.util.ArrayList;

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

    public int getSuit(){
        return this.suit;
    }

    public int getRank(){
        return this.rank;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Card){
            Card card = (Card) obj;
            if ((this.rank == card.rank) && (this.suit == card.suit))
                return true;
        }
        return false;
    }

    public static void main(String[] args) {
        ArrayList<Card> hand = new ArrayList<Card>();
        hand.add(new Card(0, 0));
        Card card = new Card(0, 0);

        System.out.println(hand.remove(card));
    }

}