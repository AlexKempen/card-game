package edu.utdallas.heartstohearts.gameui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import edu.utdallas.heartstohearts.game.Card;
import edu.utdallas.heartstohearts.game.Scores;

public class GameViewModel extends ViewModel {
    private final MutableLiveData<GameUiState> gameStateData = new MutableLiveData<>(new GameUiState());

    public LiveData<GameUiState> getGameStateData() {
        return gameStateData;
    }

    public void setTrick(List<Card> trick) {
        GameUiState currState = gameStateData.getValue();
        currState.setTrick(trick);
        gameStateData.setValue(currState);
    }

    public void setHand(List<Card> hand) {
        GameUiState currState = gameStateData.getValue();
        currState.setHand(hand);
        gameStateData.setValue(currState);
    }

    public void setScores(Scores scores) {
        GameUiState currState = gameStateData.getValue();
        currState.setScores(scores);
        gameStateData.setValue(currState);
    }

    /**
     * Choose three cards to pass.
     */
    public void passCards(List<Card> cards) {
        GameUiState currState = gameStateData.getValue();
        currState.getHand().removeAll(cards);
        gameStateData.setValue(currState);
    }

    /**
     * Choose a card to play from your hand.
     */
    public void playCard(Card card) {
        GameUiState currState = gameStateData.getValue();
        currState.getHand().remove(card);
        currState.getTrick().add(card);
        gameStateData.setValue(currState);
    }
}
