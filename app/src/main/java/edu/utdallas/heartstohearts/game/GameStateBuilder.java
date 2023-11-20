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

    /**
     * Sets the hand of the given playerId.
     * If the given hand contains the TWO_OF_CLUBS, that player's action is set to PLAY_CARD.
     * Else, it is set to WAIT.
     */
    public void setHandAndAction(int playerId, List<Card> hand) {
        hands.set(playerId, hand);
        actions.set(playerId, hand.contains(Card.TWO_OF_CLUBS) ? PlayerAction.PLAY_CARD : PlayerAction.WAIT);
    }

    public List<PlayerState> make() {
        return IntStream.range(0, 4).mapToObj(i -> new PlayerState(hands.get(i), trick, actions.get(i), points.get(i))).collect(Collectors.toList());
    }
}
