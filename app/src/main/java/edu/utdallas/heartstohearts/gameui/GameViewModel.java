package edu.utdallas.heartstohearts.gameui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import edu.utdallas.heartstohearts.game.Card;
import edu.utdallas.heartstohearts.game.Scores;
import kotlin.collections.ArrayDeque;

public class GameViewModel extends ViewModel {
    private final MutableLiveData<List<Card>> handData = new MutableLiveData<>(null);
    private final MutableLiveData<List<Card>> trickData = new MutableLiveData<>(null);
    private final MutableLiveData<Scores> scoreData = new MutableLiveData<>(null);

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
