package edu.utdallas.heartstohearts.game;

import java.util.List;

public class GameManager {
    private boolean heartsBroken;
    private List<Player> players;
    private PassDirection direction;
    private List<Card> currentTrick;
    private Suit trumpSuit;

    /**
     * Creates a GameManager suitable for starting a new game.
     */
    public static GameManager startGame() {
        return new GameManagerBuilder().make();
    }

    public GameManager(List<Player> players, PassDirection direction, List<Card> currentTrick, boolean heartsBroken, Suit trumpSuit) {
        this.players = players;
        this.direction = direction;
        this.currentTrick = currentTrick;
        this.heartsBroken = heartsBroken;
        this.trumpSuit = trumpSuit;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public PassDirection getDirection() {
        return direction;
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
            this.players.get(p).setHand(hands.get(p));
        }

        // update PlayerActions - TO DO
        for (int i = 0; i < 4; i++) {
            this.players.get(i).setAction(PlayerAction.CHOOSE_CARDS, trumpSuit, heartsBroken);
        }
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
                // player p passes playerChoices.get(p) to player this.direction.mapPassIndex(p)
                // and those cards are removed from p's hand
                players.get(p).removeFromHand(playerChoices.get(p));
                players.get(p).addToHand(playerChoices.get(direction.getPassId(p)));
            }
        }


        // update player actions - player with 2 of Clubs set to play a card, all others set to wait
        int hasTwoOfClubs = -1;
        for (int playerId = 0; playerId < 4; playerId++) {
            for (Card c : players.get(playerId).getHand()) {
                if (c.equals(Card.TWO_OF_CLUBS)) {
                    hasTwoOfClubs = playerId;
                }
            }
            if (hasTwoOfClubs == playerId) {
                players.get(playerId).setAction(PlayerAction.PLAY_CARD, trumpSuit, heartsBroken);
            } else {
                players.get(playerId).setAction(PlayerAction.WAIT, trumpSuit, heartsBroken);
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
     * The card is also assumed to be a legal play
     */
    public void playCard(Card card) {
        // Remove the card from the current player's hand
        int currPlayer = shouldPlayCard();
        players.get(currPlayer).removeFromHand(card);

        // Add the card to the trick
        currentTrick.add(card);

        // If card is first card in the trick, set the trump suit
        if (currentTrick.size() == 1) {
            trumpSuit = card.getSuit();
        }

        int winnerOfTrick;

        // If the current trick is full, add it to the current player and reset it
        if (currentTrick.size() == 4) {
            // determine winner of trick and give them the trick
            winnerOfTrick = determineTrickWinner(currentTrick, currPlayer);
            players.get(winnerOfTrick).takeTrick(currentTrick);
            trumpSuit = null;
            currentTrick.clear();

            // if round is not completely over (hands aren't empty)
            // set the winner of the trick to play (lead) a card next
            if (players.get(0).getHand().size() != 0) {
                for (int i = 0; i < 4; i++) {
                    if (i == winnerOfTrick) {
                        players.get(i).setAction(PlayerAction.PLAY_CARD, trumpSuit, heartsBroken);
                    } else players.get(i).setAction(PlayerAction.WAIT, trumpSuit, heartsBroken);
                }
            }


        }

        // Update PlayerActions to match the current state if current trick is not full
        // Clockwise play continues
        else {
            players.get(currPlayer).setAction(PlayerAction.WAIT, trumpSuit, heartsBroken);
            if (currPlayer < 3) {
                players.get(currPlayer + 1).setAction(PlayerAction.PLAY_CARD, trumpSuit, heartsBroken);
            } else players.get(0).setAction(PlayerAction.PLAY_CARD, trumpSuit, heartsBroken);
        }


        // If all cards have been played (all hands are empty), shouldPlayCard should become false,
        // and calling this method should throw an error
    }

    /**
     * Returns id of player who won the trick
     * Requires knowledge of player who played most recent card (recentPlayer) to determine who played the winning card
     */
    public int determineTrickWinner(List<Card> trick, int recentPlayer) {
        //find highest card of trump suit in currentPlay list; this card is the winning card
        int currWinningIndex = 0;
        for (int cardIndex = 0; cardIndex < trick.size(); cardIndex++) {
            if (trick.get(cardIndex).getSuit().equals(trumpSuit) && trick.get(cardIndex).getRank().toInt() >= trick.get(currWinningIndex).getRank().toInt()) {
                currWinningIndex = cardIndex;
            }
        }

        int winner = recentPlayer - 3 + currWinningIndex;
        if (winner < 0) {
            winner += 4;
        }
        return winner;
    }

    /**
     * Should be called once shouldPlayCard returns false.
     * Updates the PassDirection and scores and resets the player's tricks.
     *
     * @return true if the game is over, and false otherwise.
     */
    public boolean finishRound() {
        direction = direction.nextPassDirection();
        boolean shotTheMoon = false;
        //check for if someone shot the moon
        for (int playerId = 0; playerId < 4; playerId++) {
            if (players.get(playerId).getTrickPoints() == 26) {
                shotTheMoon = true;

                // all other players gain 26 points
                for (int j = 0; j < 4; j++) {
                    if (j != playerId) {
                        players.get(j).addSpecificPoints(26);
                    }
                }
            }
        }

        // update scores and reset tricks
        if (!shotTheMoon) {
            for (int playerId = 0; playerId < 4; playerId++) {
                players.get(playerId).addTrickPoints(); //sums points and clears tricks
                players.get(playerId).clearTricks();
            }
        }

        // if game is completely over
        for (int playerId = 0; playerId < 4; playerId++) {
            if (players.get(playerId).getPoints() >= 100) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a list of the current game states, one for each player.
     */
    // Maybe use GameStateBuilder
    public List<PlayerState> getGameStates() {
        GameStateBuilder builder = new GameStateBuilder();
        for (int i = 0; i < 4; i++) {
            builder.actions.set(i, players.get(i).getAction());
            builder.hands.set(i, players.get(i).getHand());
            builder.points.set(i, players.get(i).getPoints());
        }
        builder.trick = currentTrick;
        return builder.make();
    }
}
