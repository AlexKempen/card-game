package edu.utdallas.heartstohearts.gameui;

import java.util.List;

import edu.utdallas.heartstohearts.game.Card;
import edu.utdallas.heartstohearts.game.Scores;

public class GameState {
    private List<Card> hand;
    private List<Card> trick;
    /**
     * True if the client should choose a card to play.
     */
    private boolean chooseCardToPlay = false;

    /**
     * True if the client should choose three cards to pass.
     */
    private boolean chooseCardsToPass = false;

    private Scores scores;

    public List<Card> getHand() {
        return hand;
    }

    public void setHand(List<Card> hand) {
        this.hand = hand;
    }

    public List<Card> getTrick() {
        return trick;
    }

    public void setTrick(List<Card> trick) {
        this.trick = trick;
    }

    public boolean chooseCardToPlay() {
        return chooseCardToPlay;
    }

    public void setChooseCardToPlay(boolean chooseCardToPlay) {
        this.chooseCardToPlay = chooseCardToPlay;
    }

    public boolean chooseCardsToPass() {
        return chooseCardsToPass;
    }

    public void setChooseCardsToPass(boolean chooseCardsToPass) {
        this.chooseCardsToPass = chooseCardsToPass;
    }

    public Scores getScores() {
        return scores;
    }

    public void setScores(Scores scores) {
        this.scores = scores;
    }
}
