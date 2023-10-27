package edu.utdallas.heartstohearts.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PlayerBuilder {
    public List<Integer> points = Collections.nCopies(4, 0);
    public List<String> names = Collections.nCopies(4, "");

    public List<List<Card>> hands = Collections.nCopies(4, new ArrayList<>());
    public List<List<Card>> tricks = Collections.nCopies(4, new ArrayList<>());
    public List<PlayerAction> actions = Collections.nCopies(4, PlayerAction.CHOOSE_CARDS);

    public List<Player> make() {
        return IntStream.range(0, 4).mapToObj(i -> new Player(i, names.get(i), hands.get(i), tricks.get(i), actions.get(i), points.get(i))).collect(Collectors.toList());
    }
}
