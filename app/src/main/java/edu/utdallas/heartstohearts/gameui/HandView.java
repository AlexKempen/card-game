package edu.utdallas.heartstohearts.gameui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TableLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.EnumMap;
import java.util.List;
import java.util.stream.Collectors;

import edu.utdallas.heartstohearts.R;
import edu.utdallas.heartstohearts.game.Card;
import edu.utdallas.heartstohearts.game.Suit;

public class HandView extends TableLayout {
    public static final String TAG = "HandView";
    private EnumMap<Suit, HandCardAdapter> suitRowMap = new EnumMap<>(Suit.class);

    public HandView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.hand_view, this);

        for (Suit suit : Suit.values()) {
            int id;
            if (suit == Suit.SPADES) {
                id = R.id.spades;
            } else if (suit == Suit.HEARTS) {
                id = R.id.hearts;
            } else if (suit == Suit.DIAMONDS) {
                id = R.id.diamonds;
            } else {
                id = R.id.clubs;
            }
            RecyclerView view = findViewById(id);

            HandCardAdapter adapter = new HandCardAdapter();
            view.setAdapter(adapter);
            suitRowMap.put(suit, adapter);

            view.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void displayHand(GameViewModel model, List<Card> hand, List<Card> selectableCards) {
        for (Suit suit : Suit.values()) {
            List<Card> suitCards = hand.stream().filter(card -> card.getSuit() == suit).sorted().collect(Collectors.toList());
            HandCardAdapter adapter = suitRowMap.get(suit);
            adapter.update(model, suitCards, selectableCards);
        }
    }
}