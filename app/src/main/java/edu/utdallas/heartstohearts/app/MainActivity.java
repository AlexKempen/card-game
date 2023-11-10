package edu.utdallas.heartstohearts.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.util.Log;

import edu.utdallas.heartstohearts.R;

public class MainActivity extends AppCompatActivity {

    private View.OnClickListener handleHostGameClick = v -> {
        Log.d("Main", "Host game");
        startActivity(new Intent(MainActivity.this, FormLobbyActivity.class));
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button hostGameButton = findViewById(R.id.host_game);
        hostGameButton.setOnClickListener(handleHostGameClick);
    }
}