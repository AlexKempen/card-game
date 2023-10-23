package edu.utdallas.heartstohearts.gameui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.EnumMap;

import edu.utdallas.heartstohearts.R;
import edu.utdallas.heartstohearts.game.Suit;

public class HandView extends TableLayout {

    private EnumMap<Suit, TableRow> suiteRows;

    public HandView(Context context) {
        super(context);
        init(context);
    }

    public HandView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    protected void init(Context context) {
        // Load all elements from the XML into this view
        LayoutInflater.from(context).inflate(R.layout.hand_view, this);
        suiteRows = new EnumMap<>(Suit.class);
        suiteRows.put(Suit.HEARTS, this.findViewById(R.id.hearts_row));

        addCard("Dynamic card");

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    /**
     * TODO make this do what its supposed to, not just add text
     *
     * @param card_name
     * @return id of the added card
     */
    public int addCard(String card_name) {
        TableRow row = suiteRows.get(Suit.HEARTS);
        Button button = new Button(getContext());
        button.setText(card_name);
        row.addView(button);
        row.invalidate();

        // TODO get rid of this part:
        button.setOnClickListener((View clicked) -> {
            removeCard(button.getId());
        });

        return button.getId();
    }

    public void removeCard(int card_id) {
        // TODO: safety. What if wrong card id?
        TableRow row = (TableRow) findViewById(card_id).getParent();
        row.removeView(findViewById(card_id));
        row.invalidate();
    }

}