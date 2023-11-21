package edu.utdallas.heartstohearts.gameui;

import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import edu.utdallas.heartstohearts.R;
import edu.utdallas.heartstohearts.appui.BaseActivity;
import edu.utdallas.heartstohearts.game.Card;
import edu.utdallas.heartstohearts.game.PlayerState;
import edu.utdallas.heartstohearts.gamenetwork.GameClient;
import edu.utdallas.heartstohearts.gamenetwork.MockClient;
import edu.utdallas.heartstohearts.network.NetworkManager;

/**
 * An activity representing the main game screen.
 */
public class GameActivity extends BaseActivity {
    public static final String TAG = "Game";

    GameClient client;
    MockClient removeThisEventually;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_main);

        final HandView handView = findViewById(R.id.hand_view);
        final SubmitButton submitButton = findViewById(R.id.submit_button);

        final ViewModelProvider provider = new ViewModelProvider(this, ViewModelProvider.Factory.from(GameViewModel.initializer));
        final GameViewModel model = provider.get(GameViewModel.class);

        handView.registerModel(model);
        submitButton.registerModel(model);

        model.getPlayerStateData().observe(this, gameState -> {
            handView.displayHand(gameState.getHand());
            submitButton.update();
        });

        model.getSelectedCardsData().observe(this, selectedCards -> {
            List<Card> hand = model.getPlayerStateData().getValue().getHand();
            handView.displayHand(hand);
            submitButton.update();
        });

        // TODO fix this, assumes that the network has already been established and goes against the event-based structure
        WifiP2pInfo group_info = NetworkManager.getInstance(getApplicationContext()).getLastConnectionInfo();
        GameClient.createGameClientAsync(group_info.groupOwnerAddress, group_info.isGroupOwner, (client) -> {
            GameActivity.this.client = client;
            model.setOnPass(() -> {
                // TODO probably butchering the livedata philosophy
                client.passCards(model.getSelectedCardsData().getValue());
            });
            model.setOnPlay(() -> {
                client.playCard(model.getSelectedCardsData().getValue());
            });

            // listen for incoming game states
            client.addPlayerStateListener((state) -> {
                model.setPlayerState((PlayerState) state);
            });
        }, null);

        // a hack for two devices: if each device mocks an additional client we have a total of 4
        removeThisEventually = new MockClient(group_info.groupOwnerAddress);

        Log.d(TAG, "Init complete");
    }
}
