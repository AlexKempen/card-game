package edu.utdallas.heartstohearts.game;

import java.util.ArrayList;
import java.util.List;

public class GameManager {

    private boolean heartsBroken = false;
    private List<Player> players;
    private PassDirection direction;
    private List<Card> currentTrick;

    public GameManager(List<Player> players, PassDirection direction, List<Card> currentTrick) {
        this.players = players;
        this.direction = direction;
        this.currentTrick = currentTrick;
    }

    public GameManager(List<Player> players, PassDirection direction) {
        this(players, direction, new ArrayList<>());
    }

    public static GameManager make() {
        return new GameManager(new PlayerBuilder().make(), PassDirection.NONE);
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
        // update PlayerActions

    }

    /**
     * Returns true if the players should pass cards to each other.
     */
    public boolean shouldPass() {
        return direction == PassDirection.NONE;
    }

    /**
     * Passes the cards among the players according to the current PassDirection.
     * Throws if shouldPass() currently returns False.
     */
    public void passCards(List<List<Card>> playerChoices) {
    }

    /**
     * Returns true if the given player should play a card.
     */
    public boolean shouldPlayCard(int playerId) {
        return players.get(playerId).getAction() == PlayerAction.PLAY_CARD;
    }

    /**
     * Plays a given card.
     * The card is assumed to have been played by the current active player.
     */
    public void playCard(Card card) {
        // Remove the card from the current player's hand
        // If the current trick is full, add it to the current player and reset it
        // Update PlayerActions to match the current state

        // If all cards have been played (all hands are empty), shouldPlayCard should become false,
        // and calling this method should throw an error
    }

    /**
     * Should be called once shouldPlayCard returns false.
     * Updates the PassDirection and scores and resets the player's tricks.
     *
     * @return true if the game is over, and false otherwise.
     */
    public boolean finishRound() {

        return false;
    }

    /**
     * Returns a list of the current game states, one for each player.
     */
    public List<GameState> getGameStates() {
        return new ArrayList<>();
    }
}
