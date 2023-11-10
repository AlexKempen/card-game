package edu.utdallas.heartstohearts.gameui;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewStub;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import edu.utdallas.heartstohearts.R;
import edu.utdallas.heartstohearts.game.Card;
import edu.utdallas.heartstohearts.game.GameState;

/**
 * An activity representing the main game screen.
 */
public class GameActivity extends AppCompatActivity {
    public static final String TAG = "Game";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_main);

//        Intent intent = getIntent();
//        String socketPort = (String) intent.getExtras().get("socket");
//        Log.d(TAG, socketPort);

        HandView handView = findViewById(R.id.hand_view);

        final ViewModelProvider provider = new ViewModelProvider(this, ViewModelProvider.Factory.from(GameViewModel.initializer));
        final GameViewModel model = provider.get(GameViewModel.class);

        handView.registerModel(model);

        model.getGameStateData().observe(this, gameState -> {
            handView.displayHand(gameState.getHand());
        });

        model.getSelectedCardsData().observe(this, selectedCards -> {
            List<Card> hand = model.getGameStateData().getValue().getHand();
            handView.displayHand(hand);
        });
        Log.d(TAG, "Init complete");
    }
}
