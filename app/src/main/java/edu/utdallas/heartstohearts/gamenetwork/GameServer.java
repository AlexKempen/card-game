package edu.utdallas.heartstohearts.gamenetwork;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.Nullable;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

import edu.utdallas.heartstohearts.game.Card;
import edu.utdallas.heartstohearts.game.GameManager;
import edu.utdallas.heartstohearts.game.GamePhase;
import edu.utdallas.heartstohearts.game.PlayerAction;
import edu.utdallas.heartstohearts.game.PlayerState;
import edu.utdallas.heartstohearts.network.MessageFilter;
import edu.utdallas.heartstohearts.network.Switchboard;

/**
 * Listens for messages from either the network or bots and processes them sequentially in another thread.
 */
public class GameServer extends Service {

    private Switchboard switchboard;
    private static final String TAG = "GameServer";
    private static final int MAX_PLAYERS = 4;

    private GameManager game = null;
    private List<InetAddress> players;
    private List<ServerBot> bots;

    // Queue messages to process them one by one. Integer represents playerID
    private BlockingQueue<Pair<Integer, GameMessage>> messages;

    private List<List<Card>> passSelections;

    private Thread messageProcessor;

    @Override
    public void onCreate() {
        super.onCreate();
        messages = new ArrayBlockingQueue<>(100);
        bots = new ArrayList<>();
        switchboard = Switchboard.getDefault();
    }

    // Android lifecycle stuff

    /**
     * @param intent  - Must contain as an extra a list of InetAddress by the name of "players". If less
     *                than 4, a list of bots must also be provided.
     * @param flags
     * @param startID
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        if (game == null) {
            int playerId = 0;

            players = new ArrayList<>();
            String[] addressStrings = intent.getStringArrayExtra("players");
            for (String playerAddr : addressStrings) {
                try {
                    players.add(InetAddress.getByName(playerAddr));
                } catch (UnknownHostException e) {
                    Log.e(TAG, "Unable to build player list: unknown host for " + playerAddr);
                }
            }

            if (players.size() > 4) {
                Log.e(TAG, "Received too many players! Limiting to 4");
                players = players.stream().limit(4).collect(Collectors.toList());
            }

            for (InetAddress player : players) {
                int id = playerId;
                playerId++;
                switchboard.addListener(player, new MessageFilter(GameMessage.class).addChildren((msg, author) -> {
                    messageReceived(id, (GameMessage) msg);
                }));
            }
            while (playerId < 4) {
                int id = playerId;
                playerId++;
                this.bots.add(new ServerBot(this, id));
            }
            startGame();
        }

        messageProcessor = new Thread(() -> {
            while (true) {
                // Blocking queue, can always poll
                try {
                    Pair<Integer, GameMessage> p = messages.take();
                    processMessage(p.first, p.second);
                } catch (InterruptedException e){
                    Log.d(TAG, "Processing thread interrupted");
                    break;
                }
            }
        });

        messageProcessor.start();

        return START_NOT_STICKY;
    }

    // No binding
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }


    public void startGame() {
        Log.d(TAG, "Starting new game");
        game = GameManager.startGame();
        resetPassSelections();
        stateChangedClosure();
    }

    private void processMessage(int playerId, GameMessage msg) {
        try {
            assertGameState(game != null, "Game has not begun");

            // null action is a request for game state
            if (msg.action == null) {
                sendGameState(playerId);
                return;
            }

            PlayerState playerState = game.getPlayerStates().get(playerId);
            // check received correct number of cards
            assertGameState(msg.actionItems.size() == msg.action.getSelectionLimit(), "Unexpected number of cards");
            // check that the player is playing cards that are in fact in their hand
            assertGameState(playerState.getHand().containsAll(msg.actionItems), "Player using cards not in hand");

            if (game.getGamePhase() == GamePhase.PASS) {
                assertGameState(msg.action == PlayerAction.CHOOSE_CARDS, "Player taking invalid action");
                assertGameState(!hasPassed(playerId), "Player has already passed");
                Log.d(TAG, "Processing pass from player " + playerId);
                partialPass(playerId, msg.actionItems);
            } else if (game.getGamePhase() == GamePhase.PLAY) {
                assertGameState(game.getCurrentPlayerId().equals(playerId), "Player playing out of turn");
                assertGameState(msg.action == PlayerAction.PLAY_CARD, "Player taking invalid action");
                Log.d(TAG, "Processing play from player" + playerId);
                game.playCard(msg.actionItems.get(0));
            }

            stateChangedClosure();

        } catch (GameStateException e) {
            Log.d(TAG, "Game State error when handling message: " + e);
        }
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

    private boolean hasPassed(int player) {
        return passSelections.get(player) != null;
    }

    /**
     * Called when a message is received.
     *
     * @param playerId - index of the connection where the message originated
     * @param msg      - the message object. This should be filtered to only include game messages.
     */
    protected void messageReceived(int playerId, GameMessage msg) {
        Log.d(TAG, "Message received from player " + playerId);
        // blocking queue already synchronized
        messages.add(new Pair<>(playerId, msg));
    }

    /**
     * Called whenever the state can reasonable be assumed to have changed.
     * <p>
     * Checks for necessary state transitions for the game manager and broadcasts the new states
     * to each player.
     * <p>
     * TODO Warning: 99% sure this will not let anyone see the last card played per trick
     */
    private void stateChangedClosure() {
        if (game.getGamePhase() == GamePhase.DEAL) {
            game.deal();
            // Check if passes from the bots are needed
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
     * @param playerTo - index of player to send to.
     */
    private void sendGameState(int playerTo) {
        // Done with transitions. Dispatch messages
        if (game == null) return;
        PlayerState state = game.getPlayerStates().get(playerTo);

        if (playerTo < players.size()) {
            Log.d(TAG, "Sending state to player" + playerTo);
            switchboard.sendMessageAsync(players.get(playerTo), state,
                    (e) -> Log.d(TAG, "Unable to send state to player " + playerTo + ": " + e));
        } else {
            int botId = playerTo - players.size();
            bots.get(botId).notifyState(state);
        }
    }

    private void assertGameState(boolean condition) throws GameStateException {
        assertGameState(condition, "Illegal game action");
    }

    private void assertGameState(boolean condition, String errorMessage) throws GameStateException {
        if (!condition) {
            throw new GameStateException(errorMessage);
        }
    }
}

class GameStateException extends Exception {
    public GameStateException(String msg) {
        super(msg);
    }
}

