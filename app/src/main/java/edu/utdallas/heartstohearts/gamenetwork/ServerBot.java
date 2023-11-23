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

    public ServerBot(GameServer parent, int playerID) {
        this.parent = parent;
        this.playerID = playerID;

    }

    public void notifyState(PlayerState state) {
        if (state.getAction() != PlayerAction.WAIT) {
            GameMessage message = new GameMessage(state.getAction(), getSelection(state));
            parent.messageReceived(playerID, message);
        }
    }

    private List<Card> getSelection(PlayerState state) {
        // for now pick first available cards
        return state.getHand().stream().filter(c -> c.isSelectable()).limit(state.getAction().getSelectionLimit()).collect(Collectors.toList());
    }
}
