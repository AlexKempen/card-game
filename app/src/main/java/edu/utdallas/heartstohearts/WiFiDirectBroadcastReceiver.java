package edu.utdallas.heartstohearts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Listens for WiFi p2p events.
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {
    private WifiP2pManager manager;
    private Channel channel;
    // It's hard to say at this point whether taking activity is a good idea
    private AppCompatActivity activity;

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel, AppCompatActivity activity) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
    }

    /**
     * Creates an IntentFilter which filters for only the actions handled by this receiver.
     */
    private IntentFilter createIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        return intentFilter;
    }

    /**
     * Registers the receiver on the given activity.
     */
    public void registerReceiver() {
        activity.registerReceiver(this, createIntentFilter());
    }

    /**
     * Unregisters the receiver on the given activity.
     */
    public void unregisterReceiver() {
        activity.unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
        } else if (action.equals(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
            handlePeersChanged();
        } else if (action.equals(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)) {
            // Respond to new connection or disconnections

        } else if (action.equals(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)) {
            // Respond to this device's wifi state changing

        }
    }

    private void handlePeersChanged() {


    }
}
