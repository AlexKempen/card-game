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

    /**
     * Returns the playerId the given playerId should pass to.
     */
    public int getPassId(int playerId) {
        switch (this) {
            // Assume clockwise seating (and turn order)
            case LEFT:
                return Arrays.asList(1, 2, 3, 0).get(playerId);
            case RIGHT:
                return Arrays.asList(3, 0, 1, 2).get(playerId);
            case ACROSS:
                return (playerId + 2) % 4;
            case NONE:
                return 0;
            default:
                throw new AssertionError("Unhandled PassDirection");
        }
    }
}
