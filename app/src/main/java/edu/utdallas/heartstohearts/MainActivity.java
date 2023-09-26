package edu.utdallas.heartstohearts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    View.OnClickListener handleHostGameClick = v -> {
        Log.d("Main", "Host game");
        startActivity(new Intent(MainActivity.this, HostGameActivity.class));
    };

    View.OnClickListener handleJoinGameClick = v -> {
        Log.d("Main", "Join game");
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button hostGameButton = findViewById(R.id.host_game);
        hostGameButton.setOnClickListener(handleHostGameClick);

        final Button joinGameButton = findViewById(R.id.join_game);
        joinGameButton.setOnClickListener(handleJoinGameClick);
    }
}