package edu.utdallas.heartstohearts.gameui;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.List;

import edu.utdallas.heartstohearts.R;
import edu.utdallas.heartstohearts.game.Card;

public class TrickView extends ConstraintLayout {

    private ImageView trickImageViews[];

    public TrickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.trick_view, this);

        // set the references to image views
        trickImageViews = new ImageView[4];
        trickImageViews[0] = (ImageView) findViewById(R.id.trick_image_view_0);
        trickImageViews[1] = (ImageView) findViewById(R.id.trick_image_view_1);
        trickImageViews[2] = (ImageView) findViewById(R.id.trick_image_view_2);
        trickImageViews[3] = (ImageView) findViewById(R.id.trick_image_view_3);
    }

    public void displayTrick(List<Card> trick) {
        int numCardsInPlay = trick.size();

        // set imageViews to card image
        Resources resources = getResources();
        String packageName = getContext().getPackageName();
        for (int cardIndex = 0; cardIndex < numCardsInPlay; cardIndex++) {
            Card card = trick.get(cardIndex);

            // get image name
            int rank = ((card.getRank().toIndex() + 1) % 13) + 1;
            String suit = card.getSuit().toString().toLowerCase();
            String imageName = suit + "_" + rank;

            // set image view to the image resource
            int resourceID = resources.getIdentifier(imageName, "drawable", packageName);
            trickImageViews[cardIndex].setImageResource(resourceID);
            trickImageViews[cardIndex].setVisibility(VISIBLE);
        }

        // set imageViews not being set to a card as invisible/transparent
        for (int cardIndex = numCardsInPlay; cardIndex < trickImageViews.length; cardIndex++)
            trickImageViews[cardIndex].setVisibility(INVISIBLE);
        //trickImageViews[cardIndex].setImageResource(android.R.color.transparent);

    }
}
