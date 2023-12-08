/**
 * Hearts to Hearts project
 * Senior design project, University of Texas at Dallas CS 4485.0W1
 * Fall 2023
 * <p>
 * File authors:
 * - Alex Kempen
 * - Jacob Baskins
 */
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
     * The current trick is finished.
     */
    TRICK_FINISHED,
    /**
     * The game is complete.
     */
    COMPLETE
}
