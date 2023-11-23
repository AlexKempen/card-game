package edu.utdallas.heartstohearts.gamenetwork;

import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;

import edu.utdallas.heartstohearts.game.Card;
import edu.utdallas.heartstohearts.game.PlayerAction;
import edu.utdallas.heartstohearts.game.PlayerState;
import edu.utdallas.heartstohearts.network.Callback;
import edu.utdallas.heartstohearts.network.MessageFilter;
import edu.utdallas.heartstohearts.network.MessageListener;
import edu.utdallas.heartstohearts.network.PeerConnection;
import edu.utdallas.heartstohearts.network.Switchboard;

/**
 * Class for communicating with the game server.
 * <p>
 * Every device in the group will think itself a client. Upon instantiating a GameClient, if the
 * device is the group owner, it will additionally launch a GameServer in the background, and then
 * connect to that server. If it is not the group owner, it will attempt to connect to the server
 * launched  by the group owner
 */
public class GameClient implements MessageListener {

    private static final String TAG = "GameClient";
    private PlayerState lastPlayerState = null;
    private Switchboard switchboard;
    private InetAddress gameHost;
    private MessageFilter gameMessages;

    public GameClient(Switchboard switchboard, InetAddress gameHost){
        this.switchboard = switchboard;
        this.gameHost = gameHost;
        gameMessages = new MessageFilter(GameMessage.class).addChildren(this);
        switchboard.addListener(gameHost, gameMessages);
    }

    /**
     * Sends play selection to server
     *
     * @param card
     */
    public void playCard(Card card) {
        playCard(Arrays.asList(card));
    }

    /**
     * Sends play selection to server.
     *
     * @param card- should be a list of a single card. Use passCards for passing.
     */
    public void playCard(List<Card> card) {
        assert card.size() == 1;
        GameMessage message = new GameMessage(PlayerAction.PLAY_CARD, card);
        switchboard.sendMessageAsync(gameHost, message, null); // TODO error handling
    }

    /**
     * Sends pass selection to server
     *
     * @param cards
     */
    public void passCards(List<Card> cards) {
        GameMessage message = new GameMessage(PlayerAction.CHOOSE_CARDS, cards);
        switchboard.sendMessageAsync(gameHost, message, null); // TODO error handling
    }

    /**
     * Sends a message to request the game state, which will be handled through the appropriate listeners
     */
    public void requestState() {
        switchboard.sendMessageAsync(gameHost, new GameMessage(null, null), null);
    }

    /**
     * Registers a listener to game state updates. Messages will be passed in as objects which can
     * be cast to PlayerStates. When added, the last known game state (if not null) will be
     * send to the listener.
     *
     * @param l
     */
    public synchronized void addPlayerStateListener(MessageListener... l) {
        gameMessages.addChildren(l);
    }

    @Override
    public void messageReceived(Object o) {
        Log.d(TAG, "State Received");
        PlayerState msg = (PlayerState) o;
        lastPlayerState = msg;
    }
}
