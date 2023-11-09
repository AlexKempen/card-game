package edu.utdallas.heartstohearts.gameui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;
import java.util.stream.Collectors;

import edu.utdallas.heartstohearts.R;
import edu.utdallas.heartstohearts.game.Card;
import edu.utdallas.heartstohearts.game.GameState;
import edu.utdallas.heartstohearts.game.Suit;

public class HandFragment extends Fragment {
    private List<Card> selectedCards;

    public HandFragment() {
        super(R.layout.hand_view);
        GameActivity gameActivity = (GameActivity) getActivity();
        this.selectedCards = gameActivity.getSelectedCards();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ViewModelProvider provider = new ViewModelProvider(requireActivity(), ViewModelProvider.Factory.from(GameViewModel.initializer));
        final GameViewModel model = provider.get(GameViewModel.class);

        model.getGameStateData().observe(this, gameState -> {
            List<Card> selectableCards = gameState.selectableCards(selectedCards);
            List<Card> hand = gameState.getHand();
            // Iterate over hand
            for (Suit suit : Suit.values()) {
                List<Card> cards = suit.filterBySuit(hand);
                // Render each card in the appropriate hand row
                // If the card is in selectableCards, add a selection handler
                // Else if the card is already selected, add a deselection handler
                // Note the handlers should trigger a redraw of the hand
            }
        });
    }
}
