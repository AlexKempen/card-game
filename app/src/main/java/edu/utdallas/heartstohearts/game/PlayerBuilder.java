package edu.utdallas.heartstohearts.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerBuilder {
    public List<Integer> points = Collections.nCopies(4, 0);
    public List<String> names = Collections.nCopies(4, "");

    public List<List<Card>> hands = Collections.nCopies(4, new ArrayList<>());
    public List<List<Card>> tricks = Collections.nCopies(4, new ArrayList<>());
    public List<PlayerAction> actions = Collections.nCopies(4, PlayerAction.CHOOSE_CARDS);

    public List<Player> make() {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 4; ++i) {
            players.add(new Player(i, names.get(i), hands.get(i), tricks.get(i), points.get(i)));
        }
        return players;
    }
}
