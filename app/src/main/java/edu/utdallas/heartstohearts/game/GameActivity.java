package edu.utdallas.heartstohearts.game;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

/**
 * An activity representing the main game screen.
 */
public class GameActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GameViewModel model = new ViewModelProvider(this).get(GameViewModel.class);
        model.getGameStateData().observe(this, gameState -> {
            // gameState is immutable - modify via model methods
            List<Card> hand = gameState.getHand();
            List<Card> trick = gameState.getTrick();

//            List<Card> chosenCards = Arrays.asList(0, 1, 5).stream().map(hand::get).collect(Collectors.toList());
//            model.chooseCards(chosenCards);
        });
    }
}
