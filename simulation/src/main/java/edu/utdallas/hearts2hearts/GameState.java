package edu.utdallas.hearts2hearts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class GameState implements Serializable{
    
    public Player[] players;
    public boolean areHeartsBroken;

    public GameState() {
        players = new Player[4];
        for (int playerID = 0; playerID < 4; playerID++)
            players[playerID] = new Player(playerID);
        dealCards();
    }

    private void dealCards() {
        ArrayList<Card> deck = new ArrayList<Card>();
        for(int suit = 0; suit < 4; suit++)
            for(int rank = 0; rank < 13; rank++)
                deck.add(new Card(suit, rank));
        
        Collections.shuffle(deck);

        for(int playerID = 0; playerID < 4; playerID++) {
            ArrayList<Card> newHand = new ArrayList<Card>();
            for (int i = 0; i < 13; i++)
                newHand.add(deck.remove(0));
            players[playerID].hand = newHand;   
        }
        
    }


}
