/*
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
        for (int i = 0; i < 52; i++) {
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
		Card aceOfHearts = new Card(1, Suit.HEARTS);
		Card twoOfHearts = new Card(2, Suit.HEARTS);
		Card threeOfHearts = new Card(3, Suit.HEARTS);
		Card fourOfHearts = new Card(4, Suit.HEARTS);
		Card fiveOfHearts = new Card(5, Suit.HEARTS);
		Card sixOfHearts = new Card(6, Suit.HEARTS);
		Card sevenOfHearts = new Card(7, Suit.HEARTS);
		Card eightOfHearts = new Card(8, Suit.HEARTS);
		Card nineOfHearts = new Card(9, Suit.HEARTS);
		Card tenOfHearts = new Card(10, Suit.HEARTS);
		Card jackOfHearts = new Card(11, Suit.HEARTS);
		Card queenOfHearts = new Card(12, Suit.HEARTS);
		Card kingOfHearts = new Card(13, Suit.HEARTS);

		Card aceOfSpades = new Card(1, Suit.SPADES);
		Card twoOfSpades = new Card(2, Suit.SPADES);
		Card threeOfSpades = new Card(3, Suit.SPADES);
		Card fourOfSpades = new Card(4, Suit.SPADES);
		Card fiveOfSpades = new Card(5, Suit.SPADES);
		Card sixOfSpades = new Card(6, Suit.SPADES);
		Card sevenOfSpades = new Card(7, Suit.SPADES);
		Card eightOfSpades = new Card(8, Suit.SPADES);
		Card nineOfSpades = new Card(9, Suit.SPADES);
		Card tenOfSpades = new Card(10, Suit.SPADES);
		Card jackOfSpades = new Card(11, Suit.SPADES);
		Card queenOfSpades = new Card(12, Suit.SPADES);
		Card kingOfSpades = new Card(13, Suit.SPADES);

		Card aceOfDiamonds = new Card(1, Suit.DIAMONDS);
		Card twoOfDiamonds = new Card(2, Suit.DIAMONDS);
		Card threeOfDiamonds = new Card(3, Suit.DIAMONDS);
		Card fourOfDiamonds = new Card(4, Suit.DIAMONDS);
		Card fiveOfDiamonds = new Card(5, Suit.DIAMONDS);
		Card sixOfDiamonds = new Card(6, Suit.DIAMONDS);
		Card sevenOfDiamonds = new Card(7, Suit.DIAMONDS);
		Card eightOfDiamonds = new Card(8, Suit.DIAMONDS);
		Card nineOfDiamonds = new Card(9, Suit.DIAMONDS);
		Card tenOfDiamonds = new Card(10, Suit.DIAMONDS);
		Card jackOfDiamonds = new Card(11, Suit.DIAMONDS);
		Card queenOfDiamonds = new Card(12, Suit.DIAMONDS);
		Card kingOfDiamonds = new Card(13, Suit.DIAMONDS);

		Card aceOfClubs = new Card(1, Suit.CLUBS);
		Card twoOfClubs = new Card(2, Suit.CLUBS);
		Card threeOfClubs = new Card(3, Suit.CLUBS);
		Card fourOfClubs = new Card(4, Suit.CLUBS);
		Card fiveOfClubs = new Card(5, Suit.CLUBS);
		Card sixOfClubs = new Card(6, Suit.CLUBS);
		Card sevenOfClubs = new Card(7, Suit.CLUBS);
		Card eightOfClubs = new Card(8, Suit.CLUBS);
		Card nineOfClubs = new Card(9, Suit.CLUBS);
		Card tenOfClubs = new Card(10, Suit.CLUBS);
		Card jackOfClubs = new Card(11, Suit.CLUBS);
		Card queenOfClubs = new Card(12, Suit.CLUBS);
		Card kingOfClubs = new Card(13, Suit.CLUBS);
		
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
		
		*/
/* ---------------------------------
		 * START THE FIRST ROUND *
		 * ---------------------------------
		 *//*

		
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
		
		*/
/*
		// TESTING THAT CARDS ARE STORED PROPERLY; THEY ARE
		for (int i = 0; i < listOfHeldCards.size(); i ++) {
			for (int j = 0; j < listOfHeldCards.get(i).size(); j ++) {
				Card temp = listOfHeldCards.get(i).get(j);
				System.out.println(temp.getRank() + "," + temp.getSuit());
			}
		}
		*//*


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
*/
