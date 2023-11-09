package edu.utdallas.heartstohearts.gameui;

import android.widget.Button;

import java.util.List;

import edu.utdallas.heartstohearts.game.Card;

public class CardButtonWrapper {
    private GameActivity activity;
    private List<Card> selectedCards;
    private List<Card> selectableCards;

    CardButtonWrapper(GameActivity activity, List<Card> selectedCards, List<Card> selectableCards) {
        this.activity = activity;
        this.selectedCards = selectedCards;
        this.selectableCards = selectableCards;
    }

    /**
     * Configures the given button to match the given card.
     */
    public void configureButton(Button button, Card card) {
        button.setText(card.getRank().toString());

        CardButtonState state = CardButtonState.computeState(card, selectedCards, selectableCards);
        if (state == CardButtonState.SELECT) {
            button.setEnabled(true);
            button.setOnClickListener(view -> {
                selectedCards.add(card);
            });
        } else if (state == CardButtonState.DESELECT) {
            button.setEnabled(true);
            button.setOnClickListener(view -> selectedCards.remove(card));
        } else {
            button.setEnabled(false);
        }
    }
}