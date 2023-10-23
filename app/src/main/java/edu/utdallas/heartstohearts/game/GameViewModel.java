package edu.utdallas.heartstohearts.game;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class GameViewModel extends ViewModel {
    private final MutableLiveData<GameState> gameState;

    public GameViewModel(GameState gameState) {
        this.gameState = new MutableLiveData<>(gameState);
    }

    public LiveData<GameState> getGameState() {
        return gameState;
    }

    public void setTrick(List<Card> trick) {
        GameState currState = gameState.getValue();
        currState.setTrick(trick);
        gameState.setValue(currState);
    }

    public void setHand(List<Card> hand) {
        GameState currState = gameState.getValue();
        currState.setHand(hand);
        gameState.setValue(currState);
    }

    /**
     * Choose three cards to pass.
     */
    public void chooseCards(List<Card> cards) {
        GameState currState = gameState.getValue();
        currState.getHand().removeAll(cards);
        gameState.setValue(currState);
    }

    /**
     * Choose a card to play from your hand.
     */
    public void playCard(Card card) {
        GameState currState = gameState.getValue();
        currState.getHand().remove(card);
        currState.getTrick().add(card);
        gameState.setValue(currState);
    }
}
