package edu.utdallas.heartstohearts.gameui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import edu.utdallas.heartstohearts.R;

public class HandFragment extends Fragment {
    public HandFragment() {
        super(R.layout.hand_view);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ViewModelProvider provider = new ViewModelProvider(requireActivity(), ViewModelProvider.Factory.from(GameViewModel.initializer));
        final GameViewModel model = provider.get(GameViewModel.class);



        model.getGameStateData().observe(this, gameState -> {
            // Render the hand or something

        });
    }
}
