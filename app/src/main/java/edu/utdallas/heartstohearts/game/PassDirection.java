package edu.utdallas.heartstohearts.game;

import java.util.Arrays;

public enum PassDirection {
    LEFT, RIGHT, ACROSS, NONE;

    public PassDirection nextPassDirection() {
        switch (this) {
            case LEFT:
                return RIGHT;
            case RIGHT:
                return ACROSS;
            case ACROSS:
                return NONE;
            case NONE:
                return LEFT;
            default:
                throw new AssertionError("Unhandled PassDirection");
        }
    }

    public int mapPassIndex(int index) {
        switch (this) {
            // Assume clockwise seating (and turn order)
            case LEFT:
                return Arrays.asList(1, 2, 3, 0).get(index);
            case RIGHT:
                return Arrays.asList(3, 0, 1, 2).get(index);
            case ACROSS:
                return (index + 2) % 4;
            case NONE:
                return 0;
            default:
                throw new AssertionError("Unhandled PassDirection");
        }
    }
}
