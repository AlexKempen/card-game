package edu.utdallas.heartstohearts.gamenetwork;

import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;

import edu.utdallas.heartstohearts.game.Card;
import edu.utdallas.heartstohearts.game.PlayerAction;
import edu.utdallas.heartstohearts.game.PlayerState;
import edu.utdallas.heartstohearts.network.Callback;
import edu.utdallas.heartstohearts.network.MessageListener;
import edu.utdallas.heartstohearts.network.PeerConnection;

/**
 * Class for communicating with the game server.
 * <p>
 * Every device in the group will think itself a client. Upon instanciating a GameClient, if the
 * device is the group owner, it will additionally launch a GameServer in the background, and then
 * connect to that server. If it is not the group owner, it will attempt to connect to the server
 * launched  by the group owner
 */
public class GameClient implements MessageListener {

    private static final String TAG = "GameClient";
    private static final int PORT = 8888;
    private GameServer server;
    private PeerConnection connection;
    private PlayerState lastPlayerState = null;

    public static void createGameClientAsync(InetAddress hostAddress, boolean isGroupOwner, Callback<GameClient> onSuccess, Callback<IOException> onError) {
        new Thread(() -> {
            try {
                GameClient client = new GameClient(hostAddress, isGroupOwner);
                onSuccess.call(client);
            } catch (IOException e) {
                // TODO remove this hack. Retry after a second or so
                try {
                    Thread.sleep(1000);
                    GameClient client = new GameClient(hostAddress, isGroupOwner);
                    onSuccess.call(client);
                } catch (IOException e2) {
                    Callback.callOrThrow(onError, e2);
                } catch (Exception e2) {
                    // pass
                }
            }
        }).start();
    }

    private GameClient(InetAddress hostAddress, boolean isGroupOwner) throws IOException {
        if (isGroupOwner) {
            server = GameServer.getSingleton(hostAddress, PORT);
            server.startAcceptingConnections(null);
            server.startGame(); // TODO figure out if there's a better time to start this.
        }
        connection = PeerConnection.fromAddress(hostAddress, PORT);
        addPlayerStateListener(this);
        connection.listenForMessages(null);
        requestState();
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
        connection.sendMessageAsync(message, null); // TODO error handling
    }

    /**
     * Sends pass selection to server
     *
     * @param cards
     */
    public void passCards(List<Card> cards) {
        GameMessage message = new GameMessage(PlayerAction.CHOOSE_CARDS, cards);
        connection.sendMessageAsync(message, null); // TODO error handling
    }

    /**
     * Sends a message to request the game state, which will be handled through the appropriate listeners
     */
    public void requestState() {
        connection.sendMessageAsync(new GameMessage(null, null), null);
    }

    /**
     * Registers a listener to game state updates. Messages will be passed in as objects which can
     * be cast to PlayerStates. When added, the last known game state (if not null) will be
     * send to the listener.
     *
     * @param l
     */
    public synchronized void addPlayerStateListener(MessageListener l) {
        connection.addMessageListener(l);
        if (lastPlayerState != null) {
            l.messageReceived(lastPlayerState);
        }
    }

    @Override
    public void messageReceived(Object o) {
        Log.d(TAG, "State Received");
        PlayerState msg = (PlayerState) o;
        lastPlayerState = msg;
    }
}
