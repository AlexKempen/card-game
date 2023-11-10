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
import edu.utdallas.heartstohearts.game.GameState;
import edu.utdallas.heartstohearts.game.PlayerAction;
import edu.utdallas.heartstohearts.game.Rank;
import edu.utdallas.heartstohearts.game.Suit;

public class GameViewModel extends ViewModel {
    static final ViewModelInitializer<GameViewModel> initializer = new ViewModelInitializer<>(GameViewModel.class, creationExtras -> {
        // TODO: Use creationExtras to get initial state from server
        GameActivity gameActivity = (GameActivity) creationExtras.get(SavedStateHandleSupport.VIEW_MODEL_STORE_OWNER_KEY);
//        String socketPort = (String) gameActivity.getIntent().getExtras().get("socket");
//        Log.d(GameActivity.TAG, "View model init port: " + socketPort);

        List<Card> hand = new ArrayList<>(Arrays.asList(Card.QUEEN_OF_SPADES, Card.TWO_OF_CLUBS, new Card(Suit.CLUBS, Rank.ACE), new Card(Suit.CLUBS, Rank.FIVE)));
        List<Card> trick = new ArrayList<>(Arrays.asList(new Card(Suit.HEARTS, Rank.KING), new Card(Suit.DIAMONDS, Rank.ACE), new Card(Suit.SPADES, Rank.JACK)));
        for (Rank rank : Rank.values()) {
            hand.add(new Card(Suit.DIAMONDS, rank));
        }
        GameState gameState = new GameState(hand, trick, PlayerAction.CHOOSE_CARDS, 0);
        return new GameViewModel(gameState);
    });
    private final MutableLiveData<GameState> gameStateData;
    private final MutableLiveData<List<Card>> selectedCardsData = new MutableLiveData<>(new ArrayList<>());

    public GameViewModel(GameState gameState) {
        gameStateData = new MutableLiveData<>(gameState);
    }

    public void setGameState(GameState gameState) {
        gameStateData.setValue(gameState);
    }

    public LiveData<GameState> getGameStateData() {
        return gameStateData;
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
        // Send cards to socket
        selectedCardsData.setValue(new ArrayList<>());
    }

    /**
     * Choose a card to play from your hand.
     */
    public void playCard() {
        // send card to socket
        selectedCardsData.setValue(new ArrayList<>());
    }
}