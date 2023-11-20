package edu.utdallas.heartstohearts.game;

import java.util.List;

public enum PlayerAction {
    PLAY_CARD, CHOOSE_CARDS, WAIT;

    public int getSelectionLimit() {
        switch (this) {
            case PLAY_CARD:
                return 1;
            case CHOOSE_CARDS:
                return 3;
            case WAIT:
                return 0;
            default:
                throw new AssertionError("Unhandled PlayerAction");
        }
    }

    /**
     * Sets actions.get(i) to PlayerAction.PLAY_CARD, and the rest to PlayerAction.WAIT.
     */
    public static void setToPlayCard(int playerId, List<PlayerAction> actions) {
        for (int i = 0; i < 4; ++i) {
            PlayerAction action = playerId == i ? PlayerAction.PLAY_CARD : PlayerAction.WAIT;
            actions.set(i, action);
        }
    }
}
