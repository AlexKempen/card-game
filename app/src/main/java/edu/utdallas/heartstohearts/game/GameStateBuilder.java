package edu.utdallas.heartstohearts.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GameStateBuilder {
    public List<List<Card>> hands = Collections.nCopies(4, new ArrayList<>());
    // Trick is shared
    public List<Card> trick = new ArrayList<>();
    public List<PlayerAction> actions = Collections.nCopies(4, PlayerAction.CHOOSE_CARDS);
    public List<Integer> points = Collections.nCopies(4, 0);

    public void setHandsAndActions(List<List<Card>> hands) {
        IntStream.range(0, 4).forEach(i -> setHandAndAction(i, hands.get(i)));
    }

    public void setHandAndAction(int playerId, List<Card> hand) {
        hands.set(playerId, hand);
        actions.set(playerId, hand.contains(Card.TWO_OF_CLUBS) ? PlayerAction.PLAY_CARD : PlayerAction.WAIT);
    }

    public List<GameState> make() {
        return IntStream.range(0, 4).mapToObj(i -> new GameState(hands.get(i), trick, actions.get(i), points.get(i))).collect(Collectors.toList());
    }
}
