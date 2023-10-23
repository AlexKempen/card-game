package edu.utdallas.heartstohearts.gameui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import edu.utdallas.heartstohearts.game.Card;
import edu.utdallas.heartstohearts.game.Scores;

public class GameViewModel extends ViewModel {
    private final MutableLiveData<List<Card>> handData;
    private final MutableLiveData<List<Card>> trickData;
    private final MutableLiveData<Scores> scoreData;

    public GameViewModel(List<Card> hand, List<Card> trick, Scores scores) {
        handData = new MutableLiveData<>(hand);
        trickData = new MutableLiveData<>(trick);
        scoreData = new MutableLiveData<>(scores);
    }

    public LiveData<List<Card>> getHandData() {
        return handData;
    }

    public LiveData<List<Card>> getTrickData() {
        return trickData;
    }

    public LiveData<Scores> getScoreData() {
        return scoreData;
    }

    public void setTrick(List<Card> trick) {
        trickData.setValue(trick);
    }

    public void setHand(List<Card> hand) {
        handData.setValue(hand);
    }

    public void setScores(Scores scores) {
        scoreData.setValue(scores);
    }

    /**
     * Choose three cards to pass.
     */
    public void chooseCards(List<Card> cards) {
        List<Card> currHand = handData.getValue();
        currHand.removeAll(cards);
        handData.setValue(currHand);
    }

    /**
     * Choose a card to play from your hand.
     */
    public void playCard(Card card) {
        List<Card> currHand = handData.getValue();
        List<Card> currTrick = trickData.getValue();
        currHand.remove(card);
        currTrick.add(card);
        handData.setValue(currHand);
        trickData.setValue(currTrick);
    }
}
