/**
 * Hearts to Hearts project
 * Senior design project, University of Texas at Dallas CS 4485.0W1
 * Fall 2023
 *
 * File authors:
 *  - Egan Johnson
 */

package edu.utdallas.heartstohearts.gamenetwork;

import java.util.List;
import java.util.stream.Collectors;

import edu.utdallas.heartstohearts.game.Card;
import edu.utdallas.heartstohearts.game.PlayerAction;
import edu.utdallas.heartstohearts.game.PlayerState;

/**
 * Janky hook-in to allow bots in the game. Picks the first available legal play.
 *
 * Needs special logic for handling passing as the game manager does not record partial passes, so
 * the bot would make infinite pass selections.
 */
public class ServerBot {
    GameServer parent;
    int playerID;

    PlayerAction previousAction; // disallow pass --> pass since the game state doesn't handle this for us

    public ServerBot(GameServer parent, int playerID) {
        this.parent = parent;
        this.playerID = playerID;
        previousAction = PlayerAction.WAIT;
    }

    /**
     * Bot has been notified of a new state by the server. Guaranteed in-order.
     * @param state
     */
    public synchronized void notifyState(PlayerState state) {
        if (state.getAction() == PlayerAction.PLAY_CARD || (state.getAction() == PlayerAction.CHOOSE_CARDS && previousAction != PlayerAction.CHOOSE_CARDS)) {
            GameMessage message = new GameMessage(state.getAction(), getSelection(state));
            parent.messageReceived(playerID, message);
        }
        previousAction = state.getAction();
    }

    private List<Card> getSelection(PlayerState state) {
        // for now pick first available cards
        return state.getHand().stream().filter(c -> c.isSelectable()).limit(state.getAction().getSelectionLimit()).collect(Collectors.toList());
    }
}
