package edu.utdallas.heartstohearts.gameui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

/**
 * An activity representing the main game screen.
 */
public class GameActivity extends AppCompatActivity {
    public static final String TAG = "Game";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String socketPort = (String) intent.getExtras().get("socket");
        Log.d(TAG, socketPort);

        final ViewModelProvider provider = new ViewModelProvider(this, ViewModelProvider.Factory.from(GameViewModel.initializer));
        final GameViewModel model = provider.get(GameViewModel.class);

        model.getGameStateData().observe(this, gameState -> {
            // Gets called every time gameUiState changes

            // gameState is immutable - modify via model methods
            // List<Card> chosenCards = Arrays.asList(0, 1, 5).stream().map(hand::get).collect(Collectors.toList());
            // model.chooseCards(chosenCards);
        });
    }
}
