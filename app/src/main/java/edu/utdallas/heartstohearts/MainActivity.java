package edu.utdallas.heartstohearts;

import android.os.Bundle;
import android.util.Pair;

import edu.utdallas.heartstohearts.appui.BaseActivity;
import edu.utdallas.heartstohearts.gameui.GameActivity;
import edu.utdallas.heartstohearts.lobbyui.FormLobbyActivity;

public class MainActivity extends BaseActivity {

    // Adds all activities to navigation at boot time
    static Void temp = BaseActivity.registerNavigationItems(Pair.create(MainActivity.class, "Home"), Pair.create(GameActivity.class, "Play Game"), Pair.create(FormLobbyActivity.class, "Manage Group"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}