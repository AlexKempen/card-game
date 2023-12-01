package edu.utdallas.heartstohearts.gameui;

import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.color.utilities.Score;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

import edu.utdallas.heartstohearts.R;
import edu.utdallas.heartstohearts.appui.BaseActivity;
import edu.utdallas.heartstohearts.game.Card;
import edu.utdallas.heartstohearts.game.PlayerState;
import edu.utdallas.heartstohearts.gamenetwork.GameClient;
import edu.utdallas.heartstohearts.gamenetwork.GameServer;
import edu.utdallas.heartstohearts.network.NetworkManager;
import edu.utdallas.heartstohearts.network.Switchboard;

/**
 * An activity representing the main game screen.
 */
public class GameActivity extends BaseActivity {
    public static final String TAG = "Game";

    private GameViewModel model;

    private GameClient client;

    /**
     * A queue of state messages.
     */
    private PriorityBlockingQueue<PlayerState> stateBacklog;
    private int stateAge;
    private Thread stateConsumer;

    public GameClient getClient() {
        return client;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_main);

        final HandView handView = findViewById(R.id.hand_view);
        final SubmitButton submitButton = findViewById(R.id.submit_button);
        final TrickView trickView = findViewById(R.id.trick_view);
        final ScoreboardView scoreboardView = findViewById(R.id.scoreboard_view);

        client = GameClient.getActiveClient();

        final ViewModelProvider provider = new ViewModelProvider(this, ViewModelProvider.Factory.from(GameViewModel.initializer));

        model = provider.get(GameViewModel.class);

        handView.registerModel(model);
        submitButton.registerModel(model);

<<<<<<< Updated upstream
        model.getPlayerStateData().observe(this, state -> {
            if (state != null) {
                handView.displayHand(state.getHand());
                trickView.displayTrick(state.getTrick());
=======
        model.getPlayerStateData().observe(this, gameState -> {
            if (gameState != null) {
                handView.displayHand(gameState.getHand());
                trickView.displayTrick(gameState.getTrick());
                scoreboardView.updateNames(gameState.getNicknames(), gameState.getPlayerId());
                scoreboardView.updateScores(gameState.getPoints(), gameState.getPlayerId());
>>>>>>> Stashed changes
                submitButton.update();
            }
        });

        model.getSelectedCardsData().observe(this, selectedCards -> {
            PlayerState state = model.getPlayerStateData().getValue();
            Log.e(TAG, "Player state: " + state);
            if (state != null) {
                Log.e(TAG, "Hand: " + state.getHand());
                List<Card> hand = state.getHand();
                handView.displayHand(hand);
                submitButton.update();
            }
        });

        // Set up state consumer backlog
        stateBacklog = new PriorityBlockingQueue<>(3, Comparator.comparingInt(PlayerState::getAge));
        stateConsumer = new Thread(this::stateConsumerWorker);

        client.addPlayerStateListener(stateBacklog::put);

        Log.d(TAG, "Initialization complete");
    }

    @Override
    public void onResume() {
        super.onResume();
        stateConsumer.start();
        // Re-request a state from the server
        client.requestState();
    }

    @Override
    public void onPause() {
        stateConsumer.interrupt();
        super.onPause();
    }

    @WorkerThread
    public void stateConsumerWorker() {
        while (true) {
            // Block until one there, then wait a small time for another to arrive so we keep things in order
            try {
                // Peek doesn't actually wait, so take then put it back
                PlayerState state = stateBacklog.take();
                stateBacklog.add(state);

                Thread.sleep(100);
                state = stateBacklog.take();

                // Only consume later states
                if (state.getAge() >= stateAge) {
                    Log.d(TAG, "Set state");
                    model.setPlayerState(state);
                    stateAge = state.getAge();
                    Thread.sleep(1000);
                } else {
                    Log.d(TAG, "Stale state rejected. Rejected state age: " + state.getAge() + ", Current age: " + stateAge);
                }
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
