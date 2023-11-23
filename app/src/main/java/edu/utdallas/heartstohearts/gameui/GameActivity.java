package edu.utdallas.heartstohearts.gameui;

import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

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

    GameClient client;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_main);

        final HandView handView = findViewById(R.id.hand_view);
        final SubmitButton submitButton = findViewById(R.id.submit_button);
        final TrickView trickView = findViewById(R.id.trick_view);

        NetworkManager manager = NetworkManager.getInstance(getApplicationContext());
        InetAddress gameHost = manager.getGroupLeaderAddress();
        client = new GameClient(Switchboard.getDefault(), gameHost);

        final ViewModelProvider provider = new ViewModelProvider(this, ViewModelProvider.Factory.from(GameViewModel.initializer));

        final GameViewModel model = provider.get(GameViewModel.class);

        handView.registerModel(model);
        submitButton.registerModel(model);

        model.getPlayerStateData().observe(this, gameState -> {
            handView.displayHand(gameState.getHand());
            trickView.displayTrick(gameState.getTrick());
            submitButton.update();
        });

        model.getSelectedCardsData().observe(this, selectedCards -> {
            List<Card> hand = model.getPlayerStateData().getValue().getHand();
            handView.displayHand(hand);
            submitButton.update();
        });


        Log.d(TAG, "Init complete");
    }
}
