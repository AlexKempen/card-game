package edu.utdallas.heartstohearts.gamenetwork;

import java.util.List;
import java.util.stream.Collectors;

import edu.utdallas.heartstohearts.game.Card;
import edu.utdallas.heartstohearts.game.PlayerAction;
import edu.utdallas.heartstohearts.game.PlayerState;

/**
 * Janky hook-in to allow bots in the game.
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
