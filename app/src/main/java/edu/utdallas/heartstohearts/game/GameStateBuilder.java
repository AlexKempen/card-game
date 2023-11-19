package edu.utdallas.heartstohearts.game;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class GameStateBuilder {
    public List<List<Card>> hands = ListUtils.fourCopies(ArrayList::new);
    // Trick is shared
    public List<Card> trick = new ArrayList<>();
    public List<PlayerAction> actions = ListUtils.fourCopies(() -> PlayerAction.CHOOSE_CARDS);
    public List<Integer> points = ListUtils.fourCopies(() -> 0);
    public Suit trumpSuit;

    public void setHandsAndActions(List<List<Card>> hands) {
        IntStream.range(0, 4).forEach(i -> setHandAndAction(i, hands.get(i)));
    }

    public void setHandAndAction(int playerId, List<Card> hand) {
        hands.set(playerId, hand);
        actions.set(playerId, hand.contains(Card.TWO_OF_CLUBS) ? PlayerAction.PLAY_CARD : PlayerAction.WAIT);
    }

    public List<GameState> make() {
        return IntStream.range(0, 4).mapToObj(i -> new GameState(hands.get(i), trick, actions.get(i), points.get(i), null)).collect(Collectors.toList());
    }
}
