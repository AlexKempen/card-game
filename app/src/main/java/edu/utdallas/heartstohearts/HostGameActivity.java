package edu.utdallas.heartstohearts;

import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

import edu.utdallas.heartstohearts.network.NetworkManager;
import edu.utdallas.heartstohearts.network.PeerConnection;
import edu.utdallas.heartstohearts.network.PeerServer;
import kotlin.random.Random;


/**
 * An activity that uses WiFi Direct APIs to discover and connect with available
 * devices. WiFi Direct APIs are asynchronous and rely on callback mechanism
 * using interfaces to notify the application of operation success or failure.
 * The application should also register a BroadcastReceiver for notification of
 * WiFi state related events.
 */
public class HostGameActivity extends AppCompatActivity implements DeviceListFragment.PeerSelectionListener,
        WifiP2pManager.ConnectionInfoListener, PeerConnection.MessageListener {

    NetworkManager p2p_network_manager;
    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION = 1001;

    private PrintStream message_out_stream;
    private final int PORT = 8984;
    private final String TAG = "HostGameActivity";

    private PeerServer peer_server;
    private PeerConnection peer_connection;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.e("HostGameActivity", "Fine location permission is not granted!");
                    finish();
                }
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        p2p_network_manager = new NetworkManager(this.getApplicationContext());
        p2p_network_manager.addPeerListListener((DeviceListFragment) getFragmentManager().findFragmentById(R.id.frag_list));
        p2p_network_manager.addConnectionListener(this);

        Button p2p_init_button = (Button) findViewById(R.id.button_p2p_initialize);
        p2p_init_button.setOnClickListener((View v) -> p2p_network_manager.initialize());

        Button p2p_discovery_button = (Button) findViewById(R.id.button_p2p_discovery);
        p2p_discovery_button.setOnClickListener((View v) -> p2p_network_manager.discoverPeers(null));

        Button make_socket_button = (Button) findViewById(R.id.button_make_socket);
        make_socket_button.setOnClickListener((x) -> startClientServer());

        Button send_message_button = (Button) findViewById(R.id.button_send_message);
        send_message_button.setOnClickListener((View v) -> sendMessage("hello " + Random.Default.nextInt(1, 100)));
    }

    /**
     * register the BroadcastReceiver with the intent values to be matched
     */
    @Override
    public void onResume() {
        super.onResume();
        p2p_network_manager.register();
    }

    @Override
    public void onPause() {
        super.onPause();
        p2p_network_manager.unregister();
    }

    /**
     * Remove all peers and clear all fields. This is called on
     * BroadcastReceiver receiving a state change event.
     */
    public void resetData() {
        DeviceListFragment fragmentList = (DeviceListFragment) getFragmentManager().findFragmentById(R.id.frag_list);
        DeviceDetailFragment fragmentDetails = (DeviceDetailFragment) getFragmentManager().findFragmentById(R.id.frag_detail);
        if (fragmentList != null) {
            fragmentList.clearPeers();
        }
        if (fragmentDetails != null) {
            fragmentDetails.resetViews();
        }
    }

    @Override
    public void connect_to(WifiP2pDevice config) {
        p2p_network_manager.connectToPeer(config, null);
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        Log.d(TAG, "Connection info available");
        Log.d(TAG, "Am leader: " + info.isGroupOwner);

    }

    public void startClientServer() {
        if (p2p_network_manager.isGroupLeader()) {
            PeerServer.makeServerAsync(PORT, (peer_server) -> {
                this.peer_server = peer_server;
                peer_server.addPeerConnectionListener((PeerConnection client) -> setPeerConnection(client));
                peer_server.startAcceptingConnections();
                Log.d(TAG, "Listening for client connections");
            });
        } else {
            Log.d(TAG, "Connecting to server...");
            PeerConnection.connectAsync(p2p_network_manager.getGroupLeaderAddress(), PORT, (connection) ->{
               Log.d(TAG, "Connection created");
               setPeerConnection(connection);
           });
        }
    }

    public void setPeerConnection(PeerConnection c) {
        Log.d(TAG, "New client connected");
        peer_connection = c; // TODO close resources
        c.addMessageListener(this);
    }

    public void sendMessage(String msg) {
        try {
            peer_connection.sendMessage(msg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void messageReceived(Object o) {
        String s = (String) o;
        Log.d(TAG, "Message received: " + s);
        Toast.makeText(this, s, Toast.LENGTH_SHORT);
    }
}