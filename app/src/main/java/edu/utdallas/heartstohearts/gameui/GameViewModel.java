package edu.utdallas.heartstohearts.gameui;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandleSupport;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.List;

import edu.utdallas.heartstohearts.game.Card;
import edu.utdallas.heartstohearts.game.GameState;
import edu.utdallas.heartstohearts.game.GameStateBuilder;

public class GameViewModel extends ViewModel {
    static final ViewModelInitializer<GameViewModel> initializer = new ViewModelInitializer<>(GameViewModel.class, creationExtras -> {
        // TODO: Use creationExtras to get initial state from server
        GameActivity gameActivity = (GameActivity) creationExtras.get(SavedStateHandleSupport.VIEW_MODEL_STORE_OWNER_KEY);
        String socketPort = (String) gameActivity.getIntent().getExtras().get("socket");
        Log.d(GameActivity.TAG, "View model init port: " + socketPort);
        return new GameViewModel(new GameStateBuilder().make().get(0));
    });
    private final MutableLiveData<GameState> gameStateData;

    public GameViewModel(GameState gameState) {
        gameStateData = new MutableLiveData<>(gameState);
    }

    public LiveData<GameState> getGameStateData() {
        return gameStateData;
    }

    /**
     * Choose three cards to pass.
     */
    public void passCards(List<Card> cards) {
//        GameState currState = gameStateData.getValue();
//        currState.getHand().removeAll(cards);
//        gameStateData.setValue(currState);
    }

    /**
     * Choose a card to play from your hand.
     */
    public void playCard(Card card) {
//        GameState currState = gameStateData.getValue();
//        currState.getHand().remove(card);
//        currState.getTrick().add(card);
//        gameStateData.setValue(currState);
    }
}
