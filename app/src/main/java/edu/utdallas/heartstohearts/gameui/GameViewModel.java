package edu.utdallas.heartstohearts.gameui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandleSupport;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.utdallas.heartstohearts.game.Card;
import edu.utdallas.heartstohearts.game.PlayerAction;
import edu.utdallas.heartstohearts.game.PlayerState;
import edu.utdallas.heartstohearts.game.Rank;
import edu.utdallas.heartstohearts.game.Suit;

public class GameViewModel extends ViewModel {
    static final ViewModelInitializer<GameViewModel> initializer = new ViewModelInitializer<>(GameViewModel.class, creationExtras -> {
        // TODO: Use creationExtras to get initial state from server
        GameActivity gameActivity = (GameActivity) creationExtras.get(SavedStateHandleSupport.VIEW_MODEL_STORE_OWNER_KEY);

        List<Card> cards = new ArrayList<>(Arrays.asList(Card.QUEEN_OF_SPADES, Card.TWO_OF_CLUBS, new Card(Suit.CLUBS, Rank.ACE), new Card(Suit.CLUBS, Rank.FIVE)));
        for (Rank rank : Rank.values()) {
            cards.add(new Card(Suit.DIAMONDS, rank));
        }

        PlayerState playerState = new PlayerState(cards, new ArrayList<>(), PlayerAction.CHOOSE_CARDS, 0);
        return new GameViewModel(playerState);
    });
    private final MutableLiveData<PlayerState> playerStateData;
    private final MutableLiveData<List<Card>> selectedCardsData = new MutableLiveData<>(new ArrayList<>());

    private Runnable onPass;
    private Runnable onPlay;

    public GameViewModel(PlayerState playerState) {
        playerStateData = new MutableLiveData<>(playerState);
        onPass = () -> {
        }; // do nothing by default
        onPlay = () -> {
        };
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
        onPass.run();
        selectedCardsData.setValue(new ArrayList<>());
    }

    /**
     * Choose a card to play from your hand.
     */
    public void playCard() {
        Card card = selectedCardsData.getValue().get(0);
        onPlay.run();
        selectedCardsData.setValue(new ArrayList<>());
    }

    /**
     * Set the action to take when passing
     *
     * @param r
     */
    public void setOnPass(Runnable r) {
        this.onPass = r;
    }

    /**
     * Set the action to take when playing a card
     *
     * @param r
     */
    public void setOnPlay(Runnable r) {
        this.onPlay = r;
    }
}
