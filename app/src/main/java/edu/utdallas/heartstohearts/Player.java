package edu.utdallas.heartstohearts;
import java.util.ArrayList;

public class Player {
	
	private String username = null;
	private ArrayList<Card> currHand = new ArrayList<Card>(); //array of cards in a player's hand, length begins at 13 and decreases every trick
	
	public Player(String n) {
		this.username = n;
	}
	
	// SIMPLE GETTERS
	public String getUsername() {
		return this.username;
	}
	public ArrayList<Card> getCurrHand() {
		return this.currHand;
	}
	
	// SIMPLE SETTERS
	public void setUsername(String n) {
		this.username = n;
	}
	
	/*
	public void setCurrHand(ArrayList<Card> h) {
		this.currHand = h;
	}
	*/
	
	public void addCardToHand(Card c) {
		// add Card c to currHand array
		this.getCurrHand().add(c);
	}
	
	public void passCards(ArrayList<Card> cardsToPass, Player p) {
		// remove cardsToPass from my hand and add them to p's hand
		for (int i = 0; i < cardsToPass.size(); i ++) {
			this.getCurrHand().remove(cardsToPass.get(i));
		}
		
		for (int i = 0; i < cardsToPass.size(); i ++) {
			p.addCardToHand(cardsToPass.get(i));
		}
		
	}
	
	public void playCard(Card cardToPlay, ArrayList<Card> stack) {
		// remove cardToPlay from my hand, put it on trick stack
		this.getCurrHand().remove(cardToPlay);
		stack.add(cardToPlay);
		
	}
}
