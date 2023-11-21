package edu.utdallas.heartstohearts.gamenetwork;

import android.app.GameState;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

import edu.utdallas.heartstohearts.game.Card;
import edu.utdallas.heartstohearts.game.GameManager;
import edu.utdallas.heartstohearts.game.GamePhase;
import edu.utdallas.heartstohearts.game.PlayerAction;
import edu.utdallas.heartstohearts.game.PlayerState;
import edu.utdallas.heartstohearts.network.MessageListener;
import edu.utdallas.heartstohearts.network.PeerConnection;
import edu.utdallas.heartstohearts.network.PeerConnectionListener;
import edu.utdallas.heartstohearts.network.PeerServer;

public class GameServer extends PeerServer implements PeerConnectionListener {

    private static GameServer singleton;

    private static final String TAG = "GameServer";
    private static final int MAX_PLAYERS = 4;

    private GameManager game;
    private List<PeerConnection> playerConnections;

    private List<List<Card>> passSelections;

    /**
     * Creates (synchronously!!) a GameServer instance or returns a previously created instance.
     *
     * @param host
     * @param port
     * @return
     */
    public static GameServer getSingleton(InetAddress host, int port) throws IOException {
        if (singleton == null) {
            singleton = new GameServer(host, port);
        }
        return singleton;
    }

    /**
     * Synchronously creates a server but does NOT start listening for connections.
     * Do not use on main thread, as it performs networking operations.
     */
    private GameServer(InetAddress host, int port) throws IOException {
        super(host, port);
        playerConnections = Arrays.asList(new PeerConnection[MAX_PLAYERS]);
        resetPassSelections();
        addPeerConnectionListener(this);

        Log.d(TAG, "Game Server Launched");
    }

    public void startGame() {
        Log.d(TAG, "Starting new game");
        game = GameManager.startGame();
        stateChangedClosure();
    }

    /**
     * Find the lowest open index or -1 if not found.
     */
    private int getLowestOpenPlayerSlot() {
        for (int i = 0; i < playerConnections.size(); i++) {
            if (!isPlayerConnected(i)) {
                return i;
            }
        }
        // no slot found
        return -1;
    }

    private boolean isPlayerConnected(int i) {
        PeerConnection c = playerConnections.get(i);
        return (c != null && c.isOpen());
    }

    private void resetPassSelections() {
        passSelections = Arrays.asList(new List[MAX_PLAYERS]);
    }

    private void partialPass(int player, List<Card> selection) {
        passSelections.set(player, selection);
        if (!passSelections.contains(null)) {
            game.passCards(passSelections);
            resetPassSelections();
        }
    }

    @Override
    public void peerConnected(PeerConnection connection) {
        final int index = getLowestOpenPlayerSlot();
        if (index == -1) {
            // Too many players, refuse connection
            try {
                connection.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Log.d(TAG, "Too many connections, rejecting.");
        } else {
            // Add connection to player list
            playerConnections.set(index, connection);
            connection.addMessageListener((obj) -> {
                messageReceived(index, obj);
            });
            connection.listenForMessages(null);
            Log.d(TAG, "Player " + index + " Connected");
            // broadcast game state in case this is a re-connection and they need the updated version
//            sendGameState(index);
        }
    }

    /**
     * Called when a message is received.
     *
     * @param author - index of the connection where the message originated
     * @param o      - the message object
     */
    private synchronized void messageReceived(int author, Object o) {
        Log.d(TAG, "Message received from player " + author);
        try {
            assert game != null;

            GameMessage msg = (GameMessage) o;

            // null action is a request for game state
            if (msg.action == null) {
                sendGameState(author);
                return;
            }

            PlayerState playerState = game.getPlayerStates().get(author);
            // check received correct number of cards
            assert msg.actionItems.size() == msg.action.getSelectionLimit();
            // check that the player is playing cards that are in fact in their hand
            assert playerState.getHand().containsAll(msg.actionItems);

            if (game.getGamePhase() == GamePhase.PASS) {
                assert msg.action == PlayerAction.CHOOSE_CARDS;
                Log.d(TAG, "Processing pass from player " + author);
                partialPass(author, msg.actionItems);
            } else if (game.getGamePhase() == GamePhase.PLAY) {
                assert game.getCurrentPlayerId().equals(author);
                assert msg.action == PlayerAction.PLAY_CARD;
                Log.d(TAG, "Processing play from player" + author);
                game.playCard(msg.actionItems.get(0));
            }

            stateChangedClosure();

        } catch (Exception e) {
            Log.e(TAG, "Error when handling message: " + e);
        }
    }

    /**
     * Called whenever the state can reasonable be assumed to have changed.
     * <p>
     * Checks for necessary state transitions for the game manager and broadcasts the new states
     * to each player.
     * <p>
     * // TODO Warning: 99% sure this will not let anyone see the last card played per trick
     */
    private void stateChangedClosure() {
        if (game.getGamePhase() == GamePhase.DEAL) {
            game.deal();
            // state changed, so naturally need to call closure again
            stateChangedClosure();
        } else if (game.getGamePhase() == GamePhase.ROUND_FINISHED) {
            game.finishRound();
            stateChangedClosure();
        } else if (game.getGamePhase() == GamePhase.COMPLETE) {
            // TODO
        } else {
            for (int i = 0; i < MAX_PLAYERS; i++) {
                sendGameState(i);
            }
        }
    }

    /**
     * Sends the current game state to the corresponding connection. Handles if the player is not
     * connected.
     *
     * @param playerTo - The id of the player to send to.
     */
    private void sendGameState(int playerTo) {
        // Done with transitions. Dispatch messages
        if (isPlayerConnected(playerTo) && game != null) {
            Log.d(TAG, "Sending state to player" + playerTo);
            PlayerState state = game.getPlayerStates().get(playerTo);
            playerConnections.get(playerTo).sendMessageAsync(state, null);
        }
    }
}