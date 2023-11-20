package edu.utdallas.heartstohearts.game;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ListUtils {
    /**
     * Creates a mutable list of size four using the given supplier.
     * Unlike Collections.nCopies, the result is mutable.
     */
    static public <T> List<T> fourCopies(Supplier<T> supplier) {
        return Stream.generate(supplier).limit(4).collect(Collectors.toList());
    }

    /**
     * Removes the elements [start, end) from list.
     * Returns a list of the removed items.
     */
    static public <T> List<T> slice(List<T> list, int start, int end) {
        List<T> slice = new ArrayList<>(list.subList(start, end));
        list.removeAll(slice);
        return slice;
    }
}
