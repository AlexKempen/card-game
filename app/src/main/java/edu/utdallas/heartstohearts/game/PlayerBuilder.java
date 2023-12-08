/**
 * Hearts to Hearts project
 * Senior design project, University of Texas at Dallas CS 4485.0W1
 * Fall 2023
 * <p>
 * File authors:
 * - Alex Kempen
 * - Jacob Baskins
 */
package edu.utdallas.heartstohearts.game;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PlayerBuilder {
    public List<Integer> points = ListUtils.fourCopies(() -> 0);
    public List<String> nicknames = ListUtils.fourCopies(() -> "");
    public List<List<Card>> hands = ListUtils.fourCopies(ArrayList::new);
    public List<List<Card>> tricks = ListUtils.fourCopies(ArrayList::new);

    public List<Player> build() {
        return IntStream.range(0, 4).mapToObj(i -> new Player(i, nicknames.get(i), hands.get(i), tricks.get(i), points.get(i))).collect(Collectors.toList());
    }
}
