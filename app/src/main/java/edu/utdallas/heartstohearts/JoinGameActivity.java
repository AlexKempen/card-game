package edu.utdallas.heartstohearts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import edu.utdallas.heartstohearts.gameui.GameActivity;


public class JoinGameActivity extends AppCompatActivity {
    public static final String TAG = "JoinGame";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_game_activity);

        TextView startGame = (TextView) findViewById(R.id.start_game);
        startGame.setOnClickListener(view -> {
            Intent startGameIntent = new Intent(this, GameActivity.class);
            startGameIntent.putExtra("socket", "1111");
            this.startActivity(startGameIntent);
        });
    }
}