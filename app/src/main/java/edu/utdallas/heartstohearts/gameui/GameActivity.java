package edu.utdallas.heartstohearts.gameui;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import edu.utdallas.heartstohearts.R;
import edu.utdallas.heartstohearts.game.Card;
import edu.utdallas.heartstohearts.game.GameState;

/**
 * An activity representing the main game screen.
 */
public class GameActivity extends AppCompatActivity {
    public static final String TAG = "Game";

    private HandView handView;
    private GameViewModel model;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_main);

        handView = (HandView) findViewById(R.id.hand_view);

//        Intent intent = getIntent();
//        String socketPort = (String) intent.getExtras().get("socket");
//        Log.d(TAG, socketPort);

        final ViewModelProvider provider = new ViewModelProvider(this, ViewModelProvider.Factory.from(GameViewModel.initializer));
        model = provider.get(GameViewModel.class);

        model.getGameStateData().observe(this, gameState -> {
            displayHand(gameState, model.getSelectedCardsData().getValue());
        });

        model.getSelectedCardsData().observe(this, selectedCards -> {
            displayHand(model.getGameStateData().getValue(), selectedCards);
        });
        Log.d(TAG, "Init complete");
    }

    public void displayHand(GameState gameState, List<Card> selectedCards) {
        Log.d(TAG, "Update hand");
        List<Card> selectableCards = gameState.selectableCards(selectedCards);
        handView.displayHand(model, gameState.getHand(), selectableCards);
    }
}
