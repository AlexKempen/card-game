package edu.utdallas.heartstohearts.gameui;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandleSupport;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

import edu.utdallas.heartstohearts.game.Card;
import edu.utdallas.heartstohearts.game.PlayerAction;
import edu.utdallas.heartstohearts.game.PlayerState;
import edu.utdallas.heartstohearts.game.Rank;
import edu.utdallas.heartstohearts.game.Suit;
import edu.utdallas.heartstohearts.gamenetwork.GameClient;

public class GameViewModel extends ViewModel {

    static final ViewModelInitializer<GameViewModel> initializer = new ViewModelInitializer<>(GameViewModel.class, creationExtras -> {
        // TODO: Use creationExtras to get initial state from server
        GameActivity gameActivity = (GameActivity) creationExtras.get(SavedStateHandleSupport.VIEW_MODEL_STORE_OWNER_KEY);

        List<Card> hand = new ArrayList<>();
        List<Card> trick = new ArrayList<>();

        PlayerState playerState = new PlayerState(hand, trick, PlayerAction.WAIT, 0);
        return new GameViewModel(playerState, gameActivity.client);
    });
    private final MutableLiveData<PlayerState> playerStateData;
    private final MutableLiveData<List<Card>> selectedCardsData = new MutableLiveData<>(new ArrayList<>());

    private GameClient client;


    public GameViewModel(PlayerState playerState, GameClient client) {
        playerStateData = new MutableLiveData<>(playerState);
        this.client = client;
    }

    public void setPlayerState(PlayerState playerState) {

        // TODO remove
        String msg = "Received hand:\n";
        for(Card card : playerState.getHand()){
            msg += "\n\t" + card + "\t:\t"+  card.toString() + "\t:\t" + card.isSelectable();
        }
        Log.d("DebugSelection", msg);

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
