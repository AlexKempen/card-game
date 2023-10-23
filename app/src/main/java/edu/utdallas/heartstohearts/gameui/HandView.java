package edu.utdallas.heartstohearts.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.EnumMap;
import java.util.Map;

import edu.utdallas.heartstohearts.R;

/**
 * TODO: document your custom view class.
 */

/**
 * TODO: get rid of these suites, have single suite enum throughout project
 */
enum Suites{
    Hearts,
    Spades,
    Diamonds,
    Clubs,
}

public class HandView extends TableLayout {

    private EnumMap<Suites, TableRow> suite_rows;

    public HandView(Context context) {
        super(context);
        init(context);
    }

    public HandView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    protected void init(Context context){
        // Load all elements from the XML into this view
        LayoutInflater.from(context).inflate(R.layout.hand_view, this);
        suite_rows = new EnumMap<Suites, TableRow>(Suites.class);
        suite_rows.put(Suites.Hearts, this.findViewById(R.id.heartsRow));

        addCard("Dynamic card");

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    /**
     * TODO make this do what its supposed to, not just add text
     * @param card_name
     *
     * @return id of the added card
     */
    public int addCard(String card_name){
        TableRow row = suite_rows.get(Suites.Hearts);
        Button button = new Button(getContext());
        button.setText(card_name);
        row.addView(button);
        row.invalidate();

        // TODO get rid of this part:
        button.setOnClickListener((View clicked)-> {
            removeCard(button.getId());
        });

        return button.getId();
    }

    public void removeCard(int card_id){
        // TODO: safety. What if wrong card id?
        TableRow row = (TableRow) findViewById(card_id).getParent();
        row.removeView(findViewById(card_id));
        row.invalidate();
    }

}