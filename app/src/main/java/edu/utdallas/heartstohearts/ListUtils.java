package edu.utdallas.heartstohearts;

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
}
