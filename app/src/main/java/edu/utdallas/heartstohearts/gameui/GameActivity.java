package edu.utdallas.heartstohearts.gameui;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import edu.utdallas.heartstohearts.R;
import edu.utdallas.heartstohearts.appui.BaseActivity;
import edu.utdallas.heartstohearts.game.Card;

/**
 * An activity representing the main game screen.
 */
public class GameActivity extends BaseActivity {
    public static final String TAG = "Game";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_main);

//        Intent intent = getIntent();
//        String socketPort = (String) intent.getExtras().get("socket");
//        Log.d(TAG, socketPort);

        final HandView handView = findViewById(R.id.hand_view);
        final SubmitButton submitButton = findViewById(R.id.submit_button);

        final ViewModelProvider provider = new ViewModelProvider(this, ViewModelProvider.Factory.from(GameViewModel.initializer));
        final GameViewModel model = provider.get(GameViewModel.class);

        handView.registerModel(model);
        submitButton.registerModel(model);

        model.getGameStateData().observe(this, gameState -> {
            handView.displayHand(gameState.getHand());
            submitButton.update();
        });

        model.getSelectedCardsData().observe(this, selectedCards -> {
            List<Card> hand = model.getGameStateData().getValue().getHand();
            handView.displayHand(hand);
            submitButton.update();
        });
        Log.d(TAG, "Init complete");
    }
}
