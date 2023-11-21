package edu.utdallas.heartstohearts;

import android.os.Bundle;
import android.util.Pair;

import java.text.Normalizer;

import edu.utdallas.heartstohearts.appui.BaseActivity;
import edu.utdallas.heartstohearts.gameui.GameActivity;
import edu.utdallas.heartstohearts.networkui.FormLobbyActivity;

public class MainActivity extends BaseActivity {

    // Adds to navigation items for all activities.
    static Void add_to_nav = BaseActivity.registerNavigationItems(
            Pair.create(MainActivity.class, "Home"),
            Pair.create(GameActivity.class, "Play Game"),
            Pair.create(FormLobbyActivity.class, "Manage Group")
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}