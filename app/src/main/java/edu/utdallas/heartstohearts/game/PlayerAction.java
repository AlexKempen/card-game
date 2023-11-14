package edu.utdallas.heartstohearts.game;

import java.util.List;

public enum PlayerAction {
    PLAY_CARD, CHOOSE_CARDS, WAIT;

    public static void setToPlayCard(int playerId, List<PlayerAction> actions) {
        for (int i = 0; i < 4; ++i) {
            PlayerAction action = playerId == i ? PlayerAction.PLAY_CARD : PlayerAction.WAIT;
            actions.set(i, action);
        }
    }

    public static void setToChooseCards(int playerId, List<PlayerAction> actions) {
        for (int i = 0; i < 4; ++i) {
            PlayerAction action = PlayerAction.CHOOSE_CARDS;
            actions.set(i, action);
        }
    }
}
