package edu.utdallas.heartstohearts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import edu.utdallas.heartstohearts.gameui.GameActivity;
import edu.utdallas.heartstohearts.gameui.HandView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        Intent startGameIntent = new Intent(this, GameActivity.class);
        this.startActivity(startGameIntent);
//        setContentView(new HandView(getApplicationContext()));
    }
}