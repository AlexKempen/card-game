package edu.utdallas.heartstohearts;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class HostGameActivity extends AppCompatActivity {
    NetworkHost host;
    NetworkReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.host_game_activity);

        host = NetworkHost.createNetworkHost(this);
        receiver = host.createReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver.registerReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        receiver.unregisterReceiver();
    }
}
