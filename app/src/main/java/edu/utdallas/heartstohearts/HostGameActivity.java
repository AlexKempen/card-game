package edu.utdallas.heartstohearts;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


/**
 * An activity that uses WiFi Direct APIs to discover and connect with available
 * devices. WiFi Direct APIs are asynchronous and rely on callback mechanism
 * using interfaces to notify the application of operation success or failure.
 * The application should also register a BroadcastReceiver for notification of
 * WiFi state related events.
 */
public class HostGameActivity extends AppCompatActivity implements DeviceListFragment.PeerSelectionListener {

    ConnectionManager p2p_connection_manager;
    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION = 1001;


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

        p2p_connection_manager = new ConnectionManager(this.getApplicationContext());
        p2p_connection_manager.addPeerListListener((DeviceListFragment) getFragmentManager().findFragmentById(R.id.frag_list));

        Button p2p_init_button = (Button) findViewById(R.id.button_p2p_initialize);
        p2p_init_button.setOnClickListener((View v) -> p2p_connection_manager.initialize());

        Button p2p_discovery_button = (Button)  findViewById(R.id.button_p2p_discovery);
        p2p_discovery_button.setOnClickListener((View v)-> p2p_connection_manager.discoverPeers(null));
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
}