package edu.utdallas.heartstohearts.gameui;

import java.util.ArrayList;
import java.util.List;

import edu.utdallas.heartstohearts.game.Card;

public class SelectionState {
    private List<Card> selectedCards = new ArrayList<>();

    public void clearSelection() {
        selectedCards.clear();
    }

    public void selectCard(Card card) {
        selectedCards.add(card);
    }

    public void deselectCard(Card card) {
        selectedCards.remove(card);
    }
}
