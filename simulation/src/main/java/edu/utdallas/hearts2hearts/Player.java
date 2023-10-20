package edu.utdallas.hearts2hearts;

import java.io.Serializable;
import java.util.ArrayList;

public class Player implements Serializable{
    private int id;
    public int points;
    public ArrayList<Card> hand;
    public ArrayList<Card> cardsToPlay;
    public ArrayList<Card> trickTaken;

    public Player(int id){
        this.id = id;
        this.points = 0;
        this.hand = new ArrayList<Card>();
        this.cardsToPlay = new ArrayList<Card>();
        this.trickTaken = new ArrayList<Card>();
    }

}
