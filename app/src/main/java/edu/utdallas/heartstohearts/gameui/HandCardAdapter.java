package edu.utdallas.heartstohearts.gameui;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.utdallas.heartstohearts.R;
import edu.utdallas.heartstohearts.game.Card;
import edu.utdallas.heartstohearts.game.PlayerAction;

/**
 * Displays a suite of cards as a horizontal list.
 */
public class HandCardAdapter extends RecyclerView.Adapter<HandCardAdapter.HandCardViewHolder> {
    private List<Card> cards = new ArrayList<>();
    private GameViewModel model;

    public void registerModel(GameViewModel model) {
        this.model = model;
    }

    /**
     * Updates the data displayed by the adapter.
     */
    public void update(List<Card> cards) {
        this.cards = cards;
        notifyDataSetChanged();
    }

    /**
     * Creates a new arbitrary ViewHolder.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     */
    @Override
    public HandCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Button button = (Button) LayoutInflater.from(parent.getContext()).inflate(R.layout.hand_card, parent, false);
        return new HandCardViewHolder(button);
    }

    /**
     * Binds a given ViewHolder to match a specific card by styling it appropriately.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(HandCardViewHolder holder, int position) {
        holder.bind(cards.get(position));
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    public class HandCardViewHolder extends RecyclerView.ViewHolder {
        private Button button;

        public HandCardViewHolder(Button button) {
            super(button);
            this.button = button;
        }

        public void bind(Card card) {
            PlayerAction action = model.getGameStateData().getValue().getAction();
            List<Card> selectedCards = model.getSelectedCardsData().getValue();

            button.setText(card.getRank().toString());
            boolean enabled = true;
            int color = 0;
            if (selectedCards.contains(card)) {
                color = Color.BLUE;
                button.setOnClickListener(view -> {
                    model.deselectCard(card);
                });
            } else if (card.isPlayable() && selectedCards.size() < action.getSelectionLimit()) {
                button.setOnClickListener(view -> {
                    model.selectCard(card);
                });
            } else {
                enabled = false;
            }
            button.setEnabled(enabled);
            button.setBackgroundColor(color);
        }
    }
}
