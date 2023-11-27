package edu.utdallas.heartstohearts.game;

import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

public class GameManager {
    private boolean heartsBroken;
    private boolean firstTrick;
    private List<Player> players;
    private PassDirection direction;

    /**
     * Maps player ids to the player who played it.
     * LinkedHashMap is used since it maintains insertion order.
     */
    private LinkedHashMap<Card, Integer> currentTrick;
    private GamePhase phase;
    private Integer currentPlayerId;

    /**
     * Creates a GameManager set up to start a new game.
     */
    public static GameManager startGame() {
        return new GameManagerBuilder().build();
    }

    public GameManager(List<Player> players, PassDirection direction, LinkedHashMap<Card, Integer> currentTrick, GamePhase phase, Integer currentPlayerId, boolean heartsBroken, boolean firstTrick) {
        this.players = players;
        this.direction = direction;
        this.currentTrick = currentTrick;
        this.phase = phase;
        this.currentPlayerId = currentPlayerId;
        this.heartsBroken = heartsBroken;
        this.firstTrick = firstTrick;
    }

    /**
     * Returns the current phase of the game.
     */
    public GamePhase getGamePhase() {
        return phase;
    }

    private Suit getTrumpSuit() {
        return currentTrick.keySet().stream().findFirst().get().getSuit();
    }

    /**
     * Changes the phase of the game to nextPhase.
     */
    private GamePhase changeGamePhase(GamePhase nextPhase) {
        this.phase = nextPhase;
        return this.phase;
    }

    /**
     * Starts a round of play.
     * Sets currentPlayerId to the player holding the two of clubs.
     */
    private GamePhase startPlay() {
        currentPlayerId = players.stream().filter(player -> player.getHand().contains(Card.TWO_OF_CLUBS)).map(p -> p.getId()).findFirst().orElse(null);
        return changeGamePhase(GamePhase.PLAY);
    }

    /**
     * Deals a random deck of 52 cards to each player.
     */
    public GamePhase deal() {
        return deal(Card.dealHands(Card.makeDeck()));
    }

    /**
     * Begins a round by assigning the given hands to each player.
     * Also initializes each player's actions.
     */
    public GamePhase deal(List<List<Card>> hands) {
        if (phase != GamePhase.DEAL) {
            throw new AssertionError("Expected phase to be GamePhase.DEAL.");
        }
        // assign cards
        for (int i = 0; i < 4; i++) {
            this.players.get(i).setHand(hands.get(i));
        }
        if (direction == PassDirection.NONE) {
            return startPlay();
        }
        return changeGamePhase(GamePhase.PASS);
    }


    /**
     * Passes the cards among the players according to the current PassDirection.
     */
    public GamePhase passCards(List<List<Card>> playerChoices) {
        if (phase != GamePhase.PASS) {
            throw new AssertionError("Expected phase to be GamePhase.PASS.");
        }
        for (int p = 0; p < 4; p++) {
            players.get(p).removeFromHand(playerChoices.get(p));
            players.get(p).addToHand(playerChoices.get(direction.getPassId(p)));
        }

        return startPlay();
    }

    /**
     * Returns the id of the player who should currently play a card.
     * Throws if the current phase is not GamePhase.PLAY.
     */
    public Integer getCurrentPlayerId() {
        if (phase != GamePhase.PLAY) {
            throw new AssertionError("Expected phase to be GamePhase.PLAY.");
        }
        return currentPlayerId;
    }

    /**
     * Plays a given card.
     * The card is assumed to be legal and played by the current active player.
     */
    public GamePhase playCard(Card card) {
        if (phase != GamePhase.PLAY) {
            throw new AssertionError("Expected phase to be GamePhase.PLAY.");
        }
        // Remove the card from the current player's hand
        players.get(currentPlayerId).removeFromHand(card);
        currentTrick.put(card, currentPlayerId);
        // TODO: Whether queens breaks hearts is an open question?
        heartsBroken = heartsBroken || card.getSuit() == Suit.HEARTS;

        // If the current trick is full, add it to the current player and reset it
        if (currentTrick.size() == 4) {
            // determine winner of trick and give them the trick
            currentPlayerId = determineTrickWinner();
            players.get(currentPlayerId).takeTrick(new ArrayList<>(currentTrick.keySet()));
            currentTrick.clear();
            firstTrick = false;
            boolean roundOver = players.stream().map(Player::getHand).allMatch(List::isEmpty);
            return changeGamePhase(roundOver ? GamePhase.ROUND_FINISHED : GamePhase.PLAY);
        }
        // Continue clockwise play
        currentPlayerId = (currentPlayerId + 1) % 4;
        return GamePhase.PLAY;
    }

    /**
     * Returns id of player who won the trick.
     */
    private int determineTrickWinner() {
        Suit trumpSuit = getTrumpSuit();
        Card nextCard = currentTrick.keySet().stream().filter(card -> card.getSuit() == trumpSuit).max(Comparator.naturalOrder()).get();
        return currentTrick.get(nextCard);
    }

    /**
     * Should be called once shouldPlayCard returns false.
     * Updates the PassDirection and scores and resets the player's tricks.
     *
     * @return the next phase of the game.
     */
    public GamePhase finishRound() {
        if (phase != GamePhase.ROUND_FINISHED) {
            throw new AssertionError("Expected phase to be GamePhase.ROUND_FINISHED.");
        }
        boolean shotTheMoon = players.stream().anyMatch(player -> player.getTrickPoints() == 26);
        if (shotTheMoon) {
            players.forEach(player -> player.addPoints(player.getTrickPoints() == 26 ? 0 : 26));
        } else {
            // update scores
            players.forEach(Player::addTrickPoints);
        }
        players.forEach(Player::clearTricks);
        firstTrick = false;
        heartsBroken = false;
        direction = direction.nextPassDirection();
        return changeGamePhase(players.stream().anyMatch(player -> player.getPoints() >= 100) ? GamePhase.COMPLETE : GamePhase.DEAL);
    }

    /**
     * Returns a list of the current game states, one for each player.
     */
    // Maybe use GameStateBuilder
    public List<PlayerState> getPlayerStates() {
        PlayerStateBuilder builder = new PlayerStateBuilder();
        for (int i = 0; i < 4; i++) {
            builder.hands.set(i, players.get(i).getHand());
            builder.points.set(i, players.get(i).getPoints());

            if (phase == GamePhase.PLAY) {
                builder.actions.set(i, i == currentPlayerId ? PlayerAction.PLAY_CARD : PlayerAction.WAIT);

                List<Card> hand = builder.hands.get(i);
                if (i != currentPlayerId) {
                    hand.forEach(card -> card.setSelectable(false));
                    continue;
                }

                // Leading a trick
                if (currentTrick.isEmpty()) {
                    if (firstTrick) {
                        // Can only lead first trick with two of clubs
                        hand.forEach(card -> card.setSelectable(card.equals(Card.TWO_OF_CLUBS)));
                    } else {
                        // Cannot lead with hearts or queen of spades unless hearts broken
                        hand.forEach(card -> card.setSelectable(heartsBroken || card.getPoints() == 0));
                    }
                } else {
                    // Trump suit cards must be played
                    Suit trumpSuit = getTrumpSuit();
                    boolean hasTrump = hand.stream().anyMatch(card -> card.getSuit() == trumpSuit);
                    // If has trump cards, must play it
                    // Else, cannot play hearts
                    // TODO: Some variants don't allow queen without breaking hearts first
                    hand.forEach(card -> {
                        boolean selectable = true;
                        selectable &= (!hasTrump || (card.getSuit() == trumpSuit)); // match trick
                        selectable &= (!firstTrick || card.getPoints() == 0); // no points on first round
                        card.setSelectable(selectable);
                    });


                }
            }
        }
        if (phase == GamePhase.DEAL || phase == GamePhase.PASS || phase == GamePhase.COMPLETE) {
            PlayerAction action = phase == GamePhase.PASS ? PlayerAction.CHOOSE_CARDS : PlayerAction.WAIT;
            builder.actions = ListUtils.fourCopies(() -> action);
            builder.hands.stream().flatMap(List::stream).forEach(card -> card.setSelectable(phase == GamePhase.PASS));
        }

        builder.trick = new ArrayList<>(currentTrick.keySet());
        return builder.build();
    }
}
