package edu.utdallas.heartstohearts.game;

/**
 * An enum representing the current phase of the game.
 */
public enum GamePhase {
    /**
     * Deal a new set of cards.
     */
    DEAL,
    /**
     * Pass cards.
     */
    PASS,
    /**
     * A player should play a card.
     */
    PLAY,

    /**
     * The current round is finished.
     */
    ROUND_FINISHED,

    /**
     * The game is complete.
     */
    COMPLETE
}
