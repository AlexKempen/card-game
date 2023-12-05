/**
 * Hearts to Hearts project
 * Senior design project, University of Texas at Dallas CS 4485.0W1
 * Fall 2023
 * <p>
 * File authors:
 * - Alex Kempen
 * - Egan Johnson
 */

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
    private final ArrayList<Card> hand;
    private final ArrayList<Card> trick;
    private final PlayerAction action;
    private final int playerId;
    private final ArrayList<Integer> points;
    private final ArrayList<String> nicknames;

    // a counter that increases monotonically as the game progresses. Used for ordering states
    // that may arrive from the server out-of-order.
    private int age = 0;

    public PlayerState(List<Card> hand, List<Card> trick, PlayerAction action, int playerId, List<Integer> points, List<String> nicknames) {
        // Clone hand and trick into new arraylists
        this.hand = hand.stream().map(Card::clone).collect(Collectors.toCollection(ArrayList::new));
        this.trick = trick.stream().map(Card::clone).collect(Collectors.toCollection(ArrayList::new));
        this.action = action;
        this.playerId = playerId;
        this.points = new ArrayList<>(points);
        this.nicknames = new ArrayList<>(nicknames);
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
        return !otherIterator.hasNext() && !iterator.hasNext() && hand.equals(playerState.hand) && trick.equals(playerState.trick) && action == playerState.action && playerId == playerState.playerId && points.equals(playerState.points) && nicknames.equals(playerState.nicknames);
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

    public int getPlayerId() {
        return playerId;
    }

    public List<Integer> getPoints() {
        return points;
    }

    public List<String> getNicknames() {
        return nicknames;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}