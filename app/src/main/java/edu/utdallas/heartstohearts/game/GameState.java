package edu.utdallas.heartstohearts.game;

import java.util.List;

public class GameState {
    private List<Card> hand;
    private List<Card> trick;
    private Scores scores;

    public GameState(List<Card> hand, List<Card> trick, Scores scores) {
        this.hand = hand;
        this.trick = trick;
        this.scores = scores;
    }

//    public GameState() {
//        this(new ArrayList<>(), new ArrayList<>(), new Scores());
//    }

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

    public void setScores(Scores scores) {
        this.scores = scores;
    }

    public Scores getScores() {
        return scores;
    }
}
