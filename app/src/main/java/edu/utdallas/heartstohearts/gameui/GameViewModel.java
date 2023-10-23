package edu.utdallas.heartstohearts.gameui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import edu.utdallas.heartstohearts.game.Card;
import edu.utdallas.heartstohearts.game.Scores;

public class GameViewModel extends ViewModel {
    private final MutableLiveData<GameState> gameStateData = new MutableLiveData<>(new GameState());

    public LiveData<GameState> getGameStateData() {
        return gameStateData;
    }

    public void setTrick(List<Card> trick) {
        GameState currState = gameStateData.getValue();
        currState.setTrick(trick);
        gameStateData.setValue(currState);
    }

    public void setHand(List<Card> hand) {
        GameState currState = gameStateData.getValue();
        currState.setHand(hand);
        gameStateData.setValue(currState);
    }

    public void setScores(Scores scores) {
        GameState currState = gameStateData.getValue();
        currState.setScores(scores);
        gameStateData.setValue(currState);
    }

    /**
     * Choose three cards to pass.
     */
    public void passCards(List<Card> cards) {
        GameState currState = gameStateData.getValue();
        currState.getHand().removeAll(cards);
        gameStateData.setValue(currState);
    }

    /**
     * Choose a card to play from your hand.
     */
    public void playCard(Card card) {
        GameState currState = gameStateData.getValue();
        currState.getHand().remove(card);
        currState.getTrick().add(card);
        gameStateData.setValue(currState);
    }
}
