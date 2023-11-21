package edu.utdallas.heartstohearts.gamenetwork;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.utdallas.heartstohearts.game.Card;
import edu.utdallas.heartstohearts.game.PlayerAction;

public class GameMessage implements Serializable {
    private static final long serialVersionUID = 7860268870820163182L;

    public PlayerAction action;
    public ArrayList<Card> actionItems;

    /**
     * Sends a player action to the server. A null action indicates the client wants a copy of the
     * game state re-broadcast.
     * @param action
     * @param actionItems
     */
    public GameMessage(PlayerAction action, List<Card> actionItems){
        this.action = action;
        if (actionItems!= null) {
            this.actionItems = new ArrayList<>(actionItems);
        } else {
            this.actionItems = null;
        }
    }
}
