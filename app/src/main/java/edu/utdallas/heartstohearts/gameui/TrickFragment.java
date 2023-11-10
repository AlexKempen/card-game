package edu.utdallas.heartstohearts.gameui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import edu.utdallas.heartstohearts.R;

public class TrickFragment extends Fragment {

    private ImageView[] trickImageViews;
    public TrickFragment() {
        super(R.layout.fragment_trick);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        trickImageViews = new ImageView[4];
        trickImageViews[0] = (ImageView) view.findViewById(R.id.trick_image_view_0);
        trickImageViews[1] = (ImageView) view.findViewById(R.id.trick_image_view_1);
        trickImageViews[2] = (ImageView) view.findViewById(R.id.trick_image_view_2);
        trickImageViews[3] = (ImageView) view.findViewById(R.id.trick_image_view_3);

        trickImageViews[0].setImageResource(R.drawable.clubs_1);
        trickImageViews[1].setImageResource(R.drawable.diamonds_1);
        trickImageViews[2].setImageResource(R.drawable.spades_3);
        trickImageViews[3].setImageResource(R.drawable.hearts_4);

    }
}