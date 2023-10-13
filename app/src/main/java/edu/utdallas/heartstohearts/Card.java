package edu.utdallas.heartstohearts;

public class Card {
	private int rank = 0; // A, 2, 3, 4, ..., 9, 10, J, Q, K
	private static final int QUEEN = 11;
	private Suit suit;
	// Do we want one unique ID for each card?

	/**
	 * Infers a single card from a number 0-51. Cards 0-12 are suite 1, in rank order 0-12.
	 * @param card_number
	 */
	public Card(int card_number){
		this(card_number / 13, Suit.fromInt(card_number %13));
	}

	public Card(int rank, Suit s) {
		this.rank = rank;
		this.suit = s;
	}
	
	public int getRank() {
		return this.rank;
	}
	
	public Suit getSuit() {
		return this.suit;
	}

	public int pointValue(){
		if (suit == Suit.HEARTS) {
			return 1;
		}
		else if (suit == Suit.SPADES && rank == QUEEN){
			return 13;
		} else{
			return 0;
		}
	}

}
