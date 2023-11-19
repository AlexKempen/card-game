package edu.utdallas.heartstohearts.game;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PlayerBuilder {
    public List<Integer> points = ListUtils.fourCopies(() -> 0);
    public List<String> names = ListUtils.fourCopies(() -> "");
    public List<List<Card>> hands = ListUtils.fourCopies(ArrayList::new);
    public List<List<Card>> tricks = ListUtils.fourCopies(ArrayList::new);
    public List<PlayerAction> actions = ListUtils.fourCopies(() -> PlayerAction.CHOOSE_CARDS);

    public List<Player> make() {
        return IntStream.range(0, 4).mapToObj(i -> new Player(i, names.get(i), hands.get(i), tricks.get(i), actions.get(i), points.get(i))).collect(Collectors.toList());
    }
}
