package edu.utdallas.heartstohearts.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An immutable state holder containing a snapshot of game state for a single player.
 * Includes an action the player should take (if any) and all the information pertinent to said action.
 */
public class PlayerState implements Serializable {
    public static final long serialVersionUID = 6774190515566769539L;
    private ArrayList<Card> hand;
    private ArrayList<Card> trick;
    private PlayerAction action;
    private int points;

    public PlayerState(List<Card> hand, List<Card> trick, PlayerAction action, int points) {
        this.hand = new ArrayList<>(hand.stream().map(card -> card.clone()).collect(Collectors.toList()));
        this.trick = new ArrayList<>(trick.stream().map(card->card.clone()).collect(Collectors.toList()));
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
        return !otherIterator.hasNext() && !iterator.hasNext() && hand.equals(playerState.hand) && trick.equals(playerState.trick) &&
                // TODO: Convert to .equals() once points has been modified to list
                points == playerState.points && action == playerState.action;
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