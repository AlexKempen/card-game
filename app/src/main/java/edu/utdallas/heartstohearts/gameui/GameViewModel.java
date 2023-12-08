/**
 * Hearts to Hearts project
 * Senior design project, University of Texas at Dallas CS 4485.0W1
 * Fall 2023
 * <p>
 * File authors:
 * - Alex Kempen
 */
package edu.utdallas.heartstohearts.gameui;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandleSupport;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.ArrayList;
import java.util.List;

import edu.utdallas.heartstohearts.game.Card;
import edu.utdallas.heartstohearts.game.PlayerState;
import edu.utdallas.heartstohearts.gamenetwork.GameClient;

public class GameViewModel extends ViewModel {

    static final ViewModelInitializer<GameViewModel> initializer = new ViewModelInitializer<>(GameViewModel.class, creationExtras -> {
        GameActivity gameActivity = (GameActivity) creationExtras.get(SavedStateHandleSupport.VIEW_MODEL_STORE_OWNER_KEY);
        return new GameViewModel(gameActivity.getClient());
    });
    private final MutableLiveData<PlayerState> playerStateData = new MutableLiveData<>(null);
    private final MutableLiveData<List<Card>> selectedCardsData = new MutableLiveData<>(new ArrayList<>());

    private final GameClient client;

    public GameViewModel(GameClient client) {
        this.client = client;
    }

    public void setPlayerState(PlayerState playerState) {
        playerStateData.postValue(playerState);
    }

    public LiveData<PlayerState> getPlayerStateData() {
        return playerStateData;
    }

    public LiveData<List<Card>> getSelectedCardsData() {
        return selectedCardsData;
    }

    public void selectCard(Card card) {
        List<Card> selectedCards = selectedCardsData.getValue();
        selectedCards.add(card);
        selectedCardsData.setValue(selectedCards);
    }

    public void deselectCard(Card card) {
        List<Card> selectedCards = selectedCardsData.getValue();
        selectedCards.remove(card);
        selectedCardsData.setValue(selectedCards);
    }

    /**
     * Choose three cards to pass.
     */
    public void passCards() {
        List<Card> cards = selectedCardsData.getValue();
        client.passCards(cards);
        selectedCardsData.setValue(new ArrayList<>());
    }

    /**
     * Choose a card to play from your hand.
     */
    public void playCard() {
        Card card = selectedCardsData.getValue().get(0);
        client.playCard(card);
        selectedCardsData.setValue(new ArrayList<>());
    }
}
