package edu.utdallas.heartstohearts;
import java.util.Random;
import java.util.Scanner;
import java.util.ArrayList;

public class Game {

	private Player[] players = null; //array of 4 players
	private Card[] deck = null; //array  of 52 cards
	
	
	public Game(Player[] playerList, Card[] fullDeck) {
		this.players = playerList;
		this.deck = fullDeck;
	}
	
	
	// SIMPLE GETTERS
	public Player[] getPlayers() {
		return this.players;
	}
	
	public Card[] getDeck() {
		return this.deck;
	}
	
	// SIMPLE SETTERS
		public void setPlayers(Player[] p) {
			this.players = p;
		}
		
		public void setDeck(Card[] c) {
			this.deck = c;
		}
	
	
	public void dealCard(Card c, Player p) {
		p.addCardToHand(c);
	}
	public void shuffleDeck() {
		Card[] d = this.getDeck();
		Random rand = new Random();
		for (int i = 0; i < d.length; i++) {
			int randomIndexToSwap = rand.nextInt(d.length);
			Card temp = d[randomIndexToSwap];
			d[randomIndexToSwap] = d[i];
			d[i] = temp;
		}
	}
	
	public void dealDeck() {
		Card[] d = this.getDeck();
		Player[] p = this.getPlayers();
		int playerTrack = 0;
		for (int i = 0; i < 52; i ++) {
			dealCard(d[i], p[(playerTrack % 4)]); // deals the entire 52 card deck to the 4 players, round robin
			playerTrack++;
		}
		playerTrack = 0; // resets playerTrack to be safe, not really necessary
	}

	public static void main(String[] args) {
		// Initiate the 4 players, with user-input usernames
		Scanner reader = new Scanner(System.in);  // Create a Scanner object
	    
		System.out.println("Enter Player 1's Username");
	    String user1 = reader.nextLine();
	    System.out.println("Enter Player 2's Username");
	    String user2 = reader.nextLine();
	    System.out.println("Enter Player 3's Username");
	    String user3 = reader.nextLine();
	    System.out.println("Enter Player 4's Username");
	    String user4 = reader.nextLine();
	    
		Player p0 = new Player(user1);
		Player p1 = new Player(user2);
		Player p2 = new Player(user3);
		Player p3 = new Player(user4);
		Player[] fourPlayers = {p0, p1, p2, p3};
		
		// Initiate the 52-card deck
		Card aceOfHearts = new Card(1, "hearts");
		Card twoOfHearts = new Card(2, "hearts");
		Card threeOfHearts = new Card(3, "hearts");
		Card fourOfHearts = new Card(4, "hearts");
		Card fiveOfHearts = new Card(5, "hearts");
		Card sixOfHearts = new Card(6, "hearts");
		Card sevenOfHearts = new Card(7, "hearts");
		Card eightOfHearts = new Card(8, "hearts");
		Card nineOfHearts = new Card(9, "hearts");
		Card tenOfHearts = new Card(10, "hearts");
		Card jackOfHearts = new Card(11, "hearts");
		Card queenOfHearts = new Card(12, "hearts");
		Card kingOfHearts = new Card(13, "hearts");
		
		Card aceOfSpades = new Card(1, "spades");
		Card twoOfSpades = new Card(2, "spades");
		Card threeOfSpades = new Card(3, "spades");
		Card fourOfSpades = new Card(4, "spades");
		Card fiveOfSpades = new Card(5, "spades");
		Card sixOfSpades = new Card(6, "spades");
		Card sevenOfSpades = new Card(7, "spades");
		Card eightOfSpades = new Card(8, "spades");
		Card nineOfSpades = new Card(9, "spades");
		Card tenOfSpades = new Card(10, "spades");
		Card jackOfSpades = new Card(11, "spades");
		Card queenOfSpades = new Card(12, "spades");
		Card kingOfSpades = new Card(13, "spades");
		
		Card aceOfDiamonds = new Card(1, "diamonds");
		Card twoOfDiamonds = new Card(2, "diamonds");
		Card threeOfDiamonds = new Card(3, "diamonds");
		Card fourOfDiamonds = new Card(4, "diamonds");
		Card fiveOfDiamonds = new Card(5, "diamonds");
		Card sixOfDiamonds = new Card(6, "diamonds");
		Card sevenOfDiamonds = new Card(7, "diamonds");
		Card eightOfDiamonds = new Card(8, "diamonds");
		Card nineOfDiamonds = new Card(9, "diamonds");
		Card tenOfDiamonds = new Card(10, "diamonds");
		Card jackOfDiamonds = new Card(11, "diamonds");
		Card queenOfDiamonds = new Card(12, "diamonds");
		Card kingOfDiamonds = new Card(13, "diamonds");
		
		Card aceOfClubs = new Card(1, "clubs");
		Card twoOfClubs = new Card(2, "clubs");
		Card threeOfClubs = new Card(3, "clubs");
		Card fourOfClubs = new Card(4, "clubs");
		Card fiveOfClubs = new Card(5, "clubs");
		Card sixOfClubs = new Card(6, "clubs");
		Card sevenOfClubs = new Card(7, "clubs");
		Card eightOfClubs = new Card(8, "clubs");
		Card nineOfClubs = new Card(9, "clubs");
		Card tenOfClubs = new Card(10, "clubs");
		Card jackOfClubs = new Card(11, "clubs");
		Card queenOfClubs = new Card(12, "clubs");
		Card kingOfClubs = new Card(13, "clubs");
		
		Card[] full52Deck = {aceOfHearts, twoOfHearts, threeOfHearts, fourOfHearts, fiveOfHearts, sixOfHearts, sevenOfHearts, eightOfHearts, nineOfHearts, tenOfHearts, jackOfHearts, queenOfHearts, kingOfHearts, 
							 aceOfSpades, twoOfSpades, threeOfSpades, fourOfSpades, fiveOfSpades, sixOfSpades, sevenOfSpades, eightOfSpades, nineOfSpades, tenOfSpades, jackOfSpades, queenOfSpades, kingOfSpades, 
							 aceOfDiamonds, twoOfDiamonds, threeOfDiamonds, fourOfDiamonds, fiveOfDiamonds, sixOfDiamonds, sevenOfDiamonds, eightOfDiamonds, nineOfDiamonds, tenOfDiamonds, jackOfDiamonds, queenOfDiamonds, kingOfDiamonds, 
							 aceOfClubs, twoOfClubs, threeOfClubs, fourOfClubs, fiveOfClubs, sixOfClubs, sevenOfClubs, eightOfClubs, nineOfClubs, tenOfClubs, jackOfClubs, queenOfClubs, kingOfClubs};
		
		
		// Create the game with 4 players and 52-card deck
		Game game = new Game(fourPlayers, full52Deck);
		
		System.out.println("Players:");
		for (Player p: game.getPlayers()) {
			System.out.println(p.getUsername());
		}
		
		System.out.println("\nAll 52 Cards:");
		for (Card c : game.getDeck()) {
			System.out.println(c.getRank() + " of " + c.getSuit());
		}
		
		// Shuffle the deck and deal the cards
		game.shuffleDeck();	
		game.dealDeck();
		
		System.out.println("Players' initial hands:");
		for (Player p : game.getPlayers()) {
			ArrayList<Card> hand = p.getCurrHand();
			System.out.print(p.getUsername() + "'s hand: ");
			for (Card c : hand) {
				System.out.print(c.getRank() + " of " + c.getSuit() + ", ");
			}
			System.out.println();
		}
		System.out.println();
		
		/* ---------------------------------
		 * START THE FIRST ROUND *
		 * ---------------------------------
		 */
		
		int round = 0;

		passPhase(game, p0, p1, p2, p3, round);
		playRound(game);
		
	}
	
	public static void passPhase(Game game, Player p0, Player p1, Player p2, Player p3, int rnd) {
		//first trick - start by passing 3 cards to the right
		
		// One ArrayList to hold each player's three cards they're going to pass
		ArrayList<Card> cardsToPassFrom0 = new ArrayList<Card>();
		ArrayList<Card> cardsToPassFrom1 = new ArrayList<Card>();
		ArrayList<Card> cardsToPassFrom2 = new ArrayList<Card>();
		ArrayList<Card> cardsToPassFrom3 = new ArrayList<Card>();
				
		ArrayList<ArrayList<Card>> listOfHeldCards = new ArrayList<ArrayList<Card>>();
		listOfHeldCards.add(cardsToPassFrom0);
		listOfHeldCards.add(cardsToPassFrom1);
		listOfHeldCards.add(cardsToPassFrom2);
		listOfHeldCards.add(cardsToPassFrom3);
		
		Scanner reader = new Scanner(System.in);  // Create a Scanner object
		
		for (int i = 0; i < game.getPlayers().length; i++) {
			Player p = game.getPlayers()[i];
			System.out.println(p.getUsername() + ", it's your turn to pass 3 cards to the right.\nEnter the index of the first card you'd like to pass (0 is the first card in your hand; 12 is the last card in your hand.");
			int index = reader.nextInt();
			while (index < 0 || index > 12) {
				System.out.println("Index must be between 0 and 12 inclusive, try again:");
				index = reader.nextInt();
			}
			// RESERVE THIS CARD TO PASS
			(listOfHeldCards.get(i)).add(p.getCurrHand().get(index));
			
			System.out.println("Now enter the index of the second card you'd like to pass to the right");
			index = reader.nextInt();
			while (index < 0 || index > 12) {
				System.out.println("Index must be between 0 and 12 inclusive, try again:");
				index = reader.nextInt();
			}
			// RESERVE THIS CARD TO PASS
			(listOfHeldCards.get(i)).add(p.getCurrHand().get(index));
			
			System.out.println("Now enter the index of the third card you'd like to pass to the right");
			index = reader.nextInt();
			while (index < 0 || index > 12) {
				System.out.println("Index must be between 0 and 12 inclusive, try again:");
				index = reader.nextInt();
			}
			// RESERVE THIS CARD TO PASS
			(listOfHeldCards.get(i)).add(p.getCurrHand().get(index));
		}
		
		/*
		// TESTING THAT CARDS ARE STORED PROPERLY; THEY ARE
		for (int i = 0; i < listOfHeldCards.size(); i ++) {
			for (int j = 0; j < listOfHeldCards.get(i).size(); j ++) {
				Card temp = listOfHeldCards.get(i).get(j);
				System.out.println(temp.getRank() + "," + temp.getSuit());
			}
		}
		*/
		
		// pass the stored cards to the correct players
		// pass right
		if (rnd == 0) {
			p0.passCards(cardsToPassFrom0, p3);
			p1.passCards(cardsToPassFrom1, p0);
			p2.passCards(cardsToPassFrom2, p1);
			p3.passCards(cardsToPassFrom3, p2);
		}
		
		// pass left
		if (rnd == 1) {
			p0.passCards(cardsToPassFrom0, p1);
			p1.passCards(cardsToPassFrom1, p2);
			p2.passCards(cardsToPassFrom2, p3);
			p3.passCards(cardsToPassFrom3, p0);
		}
		
		// pass across
		if (rnd == 2) {
			p0.passCards(cardsToPassFrom0, p2);
			p1.passCards(cardsToPassFrom1, p3);
			p2.passCards(cardsToPassFrom2, p0);
			p3.passCards(cardsToPassFrom3, p1);
		}
		
		System.out.println("Players' hands after passing:");
		for (Player p : game.getPlayers()) {
			ArrayList<Card> hand = p.getCurrHand();
			System.out.print(p.getUsername() + "'s hand: ");
			for (Card c : hand) {
				System.out.print(c.getRank() + " of " + c.getSuit() + ", ");
			}
			System.out.println();
		}
		System.out.println();
	}	
	
	public static void playRound(Game g) {
		//find who has the two of clubs, make that player the leader of the first trick
		Player lead = null;
		for (Player p : g.getPlayers()) {
			for (int i = 0; i < p.getCurrHand().size(); i++) {
				if (p.getCurrHand().get(i).getRank() == 2 && p.getCurrHand().get(i).getSuit() == "clubs") {
					System.out.println(p.getUsername() + " is the leader");
					lead = p;
				}
			}
			
		}
		playTrick(lead);
		
	}
	
	public static void playTrick(Player leader) {
		
		ArrayList<Card> trickStack = new ArrayList<Card>();
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter index of card you'd like to play from your hand");
		int index = scan.nextInt();
		leader.playCard(leader.getCurrHand().get(index), trickStack);
		System.out.println("Trick stack contents:");
		for (int i = 0; i < trickStack.size(); i++) {
			System.out.println(trickStack.get(i).getRank() + " of " + trickStack.get(i).getSuit());
		}
		
	}
}
