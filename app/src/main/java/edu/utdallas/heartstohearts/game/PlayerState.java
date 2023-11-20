package edu.utdallas.heartstohearts.game;

import java.util.Iterator;
import java.util.List;

/**
 * An immutable state holder containing a snapshot of game state for a single player.
 * Includes an action the player should take (if any) and all the information pertinent to said action.
 */
public class PlayerState {
    private List<Card> hand;
    private List<Card> trick;
    private PlayerAction action;
    private int points;

    public PlayerState(List<Card> hand, List<Card> trick, PlayerAction action, int points) {
        this.hand = hand;
        this.trick = trick;
        this.action = action;
        this.points = points;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerState playerState = (PlayerState) o;

        Iterator<Card> otherIterator = playerState.hand.iterator();
        Iterator<Card> iterator = hand.iterator();
        while (otherIterator.hasNext() && iterator.hasNext()) {
            if (otherIterator.next().isSelectable() != iterator.next().isSelectable()) {
                return false;
            }
        }
        return !otherIterator.hasNext() && !iterator.hasNext() &&
                hand.equals(playerState.hand) &&
                trick.equals(playerState.trick) &&
                // TODO: Convert to .equals() once points has been modified to list
                points == playerState.points &&
                action == playerState.action;
    }

    public List<Card> getHand() {
        return hand;
    }

    public List<Card> getTrick() {
        return trick;
    }

    public PlayerAction getAction() {
        return action;
    }

    public int getPoints() {
        return points;
    }
}