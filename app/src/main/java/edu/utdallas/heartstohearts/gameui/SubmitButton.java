package edu.utdallas.heartstohearts.gameui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatButton;

import java.util.List;

import edu.utdallas.heartstohearts.game.Card;
import edu.utdallas.heartstohearts.game.Player;
import edu.utdallas.heartstohearts.game.PlayerAction;

public class SubmitButton extends AppCompatButton {
    private GameViewModel model;

    public SubmitButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void registerModel(GameViewModel model) {
        this.model = model;
    }

    public void update() {
        PlayerAction action = model.getGameStateData().getValue().getAction();
        List<Card> selectedCards = model.getSelectedCardsData().getValue();

        int visibility = action == PlayerAction.WAIT ? INVISIBLE : VISIBLE;
        this.setVisibility(visibility);

        boolean enabled = action != PlayerAction.WAIT && (selectedCards.size() == action.getSelectionLimit());
        this.setEnabled(enabled);

        if (action == PlayerAction.PLAY_CARD && enabled) {
            this.setOnClickListener(view -> model.playCard());
        } else if (action == PlayerAction.CHOOSE_CARDS && enabled) {
            this.setOnClickListener(view -> model.passCards());
        }
    }
}
