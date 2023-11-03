package edu.utdallas.heartstohearts.game;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private List<GameState> gameStates;
    private boolean heartsBroken;
    private List<Player> players;
    private PassDirection direction;
    private List<Card> currentTrick;
    private Suit trumpSuit;

    public GameManager(List<Player> players, PassDirection direction, List<Card> currentTrick, boolean heartsBroken) {
        this.players = players;
        this.direction = direction;
        this.currentTrick = currentTrick;
        this.heartsBroken = heartsBroken;
    }

    /**
     * Deals a random deck of 52 cards to each player.
     */
    public void deal() {
        deal(Card.dealHands(Card.makeDeck()));
    }

    /**
     * Begins a round by assigning the given hands to each player.
     */
    public void deal(List<List<Card>> hands) {
        // assign cards
        for (int p = 0; p < 4; p++) {
            gameStates.add(new GameState(hands.get(p), PlayerAction.WAIT));
            direction = PassDirection.LEFT;
            //this.direction = direction.nextPassDirection();
            //this.players.get(p).addToHand(hands.get(p));
        }

        // update PlayerActions - TO DO


    }

    /**
     * Returns true if the players should pass cards to each other.
     */
    public boolean shouldPass() {
        return direction != PassDirection.NONE;
    }

    /**
     * Passes the cards among the players according to the current PassDirection.
     * This method may throw if shouldPass() currently returns false.
     */
    public void passCards(List<List<Card>> playerChoices) {
        if (shouldPass()) {
            for (int p = 0; p < 4; p++) {
                //player p passes playerChoices.get(p) to player this.direction.mapPassIndex(p) and those cards are removed from p's hand
                this.players.get(this.direction.mapPassIndex(p)).addToHand(playerChoices.get(p));
                this.players.get(p).removeFromHand(playerChoices.get(p));
            }
        }
    }

    /**
     * Returns the id of the player who should currently play a card, or null if no player should.
     */
    public Integer shouldPlayCard() {
        for (Player player : players) {
            if (player.getAction() == PlayerAction.PLAY_CARD) {
                return player.getId();
            }
        }
        return null;
    }

    /**
     * Plays a given card.
     * The card is assumed to have been played by the current active player.
     */
    public void playCard(Card card) {
        // Remove the card from the current player's hand
        int currPlayer = shouldPlayCard();
        this.players.get(currPlayer).removeFromHand(card);
        // If the current trick is full, add it to the current player and reset it
        if (this.currentTrick.size() == 4) {
            // determine winner of trick and give them the trick
            int winnerOfTrick = determineTrickWinner(this.currentTrick, currPlayer);
            this.players.get(winnerOfTrick).takeTrick(this.currentTrick);

            this.currentTrick.clear();
        }

        // Update PlayerActions to match the current state

        // If all cards have been played (all hands are empty), shouldPlayCard should become false,
        // and calling this method should throw an error
    }

    /**
     * Returns id of player who won the trick
     * Requires knowledge of player who played most recent card (recentPlayer) to determine who played the winning card
     */
    public int determineTrickWinner(List<Card> trick, int recentPlayer) {
        /*
        //find highest card of trump suit in currentPlay list; this card is the winning card
        Card currWinningCard = null;
        int currWinningIndex = -1;
        for (int cardIndex = 0; cardIndex < trick.size(); cardIndex++) {
            if (trick.get(cardIndex).getSuit() == this.trumpSuit && trick.get(cardIndex).getRank().fromInt > currWinningCard.getRank()) {
                currWinningCard = trick.get(cardIndex);
                currWinningIndex = cardIndex;
            }
        }

        int winner = recentPlayer - 3 + currWinningIndex;
        if (winner < 0) {
            winner += 4;
        }
        return winner;
        */
        return 0;
    }

    /**
     * Should be called once shouldPlayCard returns false.
     * Updates the PassDirection and scores and resets the player's tricks.
     *
     * @return true if the game is over, and false otherwise.
     */
    public boolean finishRound() {
        this.direction = this.direction.nextPassDirection();
        // update scores and reset tricks - TO DO
        return false;
    }

    /**
     * Returns a list of the current game states, one for each player.
     */
    // Maybe use GameStateBuilder
    public List<GameState> getGameStates() {
        return new ArrayList<>();
    }
}
