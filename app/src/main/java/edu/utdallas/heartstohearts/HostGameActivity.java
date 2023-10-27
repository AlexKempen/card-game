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
import java.io.InputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.sql.Connection;
import java.util.Scanner;

import kotlin.random.Random;


/**
 * An activity that uses WiFi Direct APIs to discover and connect with available
 * devices. WiFi Direct APIs are asynchronous and rely on callback mechanism
 * using interfaces to notify the application of operation success or failure.
 * The application should also register a BroadcastReceiver for notification of
 * WiFi state related events.
 */
public class HostGameActivity extends AppCompatActivity implements DeviceListFragment.PeerSelectionListener, WifiP2pManager.ConnectionInfoListener {

    NetworkManager p2p_connection_manager;
    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION = 1001;

    private Socket message_socket;
    private Thread message_in_thread;
    private PrintStream message_out_stream;
    private final int PORT = 8988;
    private final String TAG = "HostGameActivity";


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

        p2p_connection_manager = new NetworkManager(this.getApplicationContext());
        p2p_connection_manager.addPeerListListener((DeviceListFragment) getFragmentManager().findFragmentById(R.id.frag_list));
        p2p_connection_manager.addConnectionListener(this);

        Button p2p_init_button = (Button) findViewById(R.id.button_p2p_initialize);
        p2p_init_button.setOnClickListener((View v) -> p2p_connection_manager.initialize());

        Button p2p_discovery_button = (Button)  findViewById(R.id.button_p2p_discovery);
        p2p_discovery_button.setOnClickListener((View v)-> p2p_connection_manager.discoverPeers(null));

        Button send_message_button = (Button) findViewById(R.id.button_send_message);
        send_message_button.setOnClickListener((View v)-> sendMessage("hello " + Random.Default.nextInt(1, 100)));
    }

    /**
     * register the BroadcastReceiver with the intent values to be matched
     */
    @Override
    public void onResume() {
        super.onResume();
        p2p_connection_manager.register();
    }

    @Override
    public void onPause() {
        super.onPause();
        p2p_connection_manager.unregister();
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
        p2p_connection_manager.connectToPeer(config, null);
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        Log.d(TAG, "Connection info available");
        /**
         * Resource management is at its all-time low. Use as a demo but mind the possible leaks.
         */
        new Thread(){
            @Override
            public void run(){
                if (info.groupFormed){
                    if(info.isGroupOwner) {
                        Log.d(TAG, "Am group owner");
                        try {
                            Log.d(TAG, "Opening server socket");
                            ServerSocket serverSock = new ServerSocket(PORT, 4, info.groupOwnerAddress);
                            Log.d(TAG, "Waiting for connection on " + serverSock.getInetAddress());
                            Socket sock = serverSock.accept();
                            Log.d(TAG, "received connection to server socket");
                            useSocketForMessages(sock);
                            serverSock.close();
                        } catch (IOException e) {
                            Log.d(TAG, "Here we are: owner");
                            Log.e(TAG, e.toString());

                        }
                    } else {
                        Log.d(TAG, "Not group owner");
                        try {
                            // Trying this sleep for debug purposes
                            sleep(1000);
                            Log.d(TAG, "Attempting to connect to owner at " + info.groupOwnerAddress);
                            Socket socketToOwner = new Socket();
                            Log.d("TAG", "After socket init before connect");
                            socketToOwner.connect(new InetSocketAddress(info.groupOwnerAddress, PORT));
                            Log.d(TAG, "Connection succeeded: " + socketToOwner.isConnected());
                            useSocketForMessages(socketToOwner);
                        } catch (IOException e){
                            Log.d(TAG, "Here we are");
                            Log.e(TAG, e.toString());
                        } catch (InterruptedException e){
                        }
                        Log.d(TAG, "Here we are outside of the try/catch");
                    }
                }
            }
        }.start();
    }

    public void sendMessage(String msg){
        Log.d(TAG, "Sending message" + msg);
        if(message_socket == null){
            Toast.makeText(this, "Connect first!", Toast.LENGTH_SHORT);
        } else {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT);
            message_out_stream.println(msg);
            message_out_stream.flush();

        }
    }
    public void useSocketForMessages(Socket sock) throws IOException {
        Log.d(TAG, "Socket connected: " + sock.isConnected());
        if (message_socket != null){
            message_socket.close();
            message_out_stream.close();
        }
        message_socket = sock;
        message_out_stream = new PrintStream(sock.getOutputStream());

        if (message_in_thread != null){
            message_in_thread.interrupt();
        }

        message_in_thread = new Thread(){
            @Override
            public void run() {
                try {
                    InputStream message_stream = sock.getInputStream();
                    Scanner sc = new Scanner(message_stream);
                    while (sc.hasNext()) {
                        String message = sc.nextLine();
                        Log.d(TAG, "Recieved message " + message);
                        Toast.makeText(HostGameActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }catch (IOException e){
                    Log.e(TAG, e.toString());
                }
            }
        };
        message_in_thread.start();
    }
}