package edu.utdallas.heartstohearts.gameui;

import java.util.List;

import edu.utdallas.heartstohearts.game.Card;

public enum CardButtonState {
    SELECT, DESELECT, DISABLED;

    public static CardButtonState computeState(Card card, List<Card> selectedCards, List<Card> selectableCards) {
        if (selectedCards.contains(card)) {
            return CardButtonState.DESELECT;
        } else if (selectableCards.contains(card)) {
            return CardButtonState.SELECT;
        }
        return CardButtonState.DISABLED;
    }
}
