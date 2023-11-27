package edu.utdallas.heartstohearts;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import edu.utdallas.heartstohearts.game.PassDirection;

public class PassDirectionTest {
    @Test
    public void testPassDirection() {
        for (PassDirection direction : PassDirection.values()) {
            List<Integer> actual = IntStream.range(0, 4).mapToObj(direction::getPassId).collect(Collectors.toList());
            List<Integer> expected;
            switch (direction) {
                case LEFT:
                    expected = Arrays.asList(1, 2, 3, 0);
                    break;
                case RIGHT:
                    expected = Arrays.asList(3, 0, 1, 2);
                    break;
                case ACROSS:
                    expected = Arrays.asList(2, 3, 0, 1);
                    break;
                case NONE:
                    expected = Arrays.asList(0, 1, 2, 3);
                    break;
                default:
                    throw new AssertionError("Error");
            }
            assertEquals(expected, actual);
        }

    }
}
