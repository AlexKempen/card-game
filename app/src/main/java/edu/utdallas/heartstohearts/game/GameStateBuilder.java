package edu.utdallas.heartstohearts.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GameStateBuilder {
    public List<Card> hand = new ArrayList<>();
    public List<Card> trick = new ArrayList<>();
    public PlayerAction action = PlayerAction.CHOOSE_CARDS;
    public int score = 0;

    public void setHandAndAction(List<Card> hand) {
        this.hand = hand;
        this.action = hand.contains(Card.TWO_OF_CLUBS) ? PlayerAction.PLAY_CARD : PlayerAction.WAIT;
    }

    public void copy(GameState state) {
        hand = state.getHand();
        trick = state.getTrick();
        action = state.getAction();
        score = state.getScore();
    }

    public GameState make() {
        return new GameState(hand, trick, action, score);
    }

    public static List<GameState> makeAll(List<GameStateBuilder> builders) {
        return builders.stream().map(GameStateBuilder::make).collect(Collectors.toList());
    }
}
