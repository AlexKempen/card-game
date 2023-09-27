package edu.utdallas.heartstohearts;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Initializes P2p connections.
 */
// I might delete this class later, idk
public class NetworkHost {
    private WifiP2pManager manager;
    private Channel channel;
    private AppCompatActivity activity;

    static public NetworkHost createNetworkHost(AppCompatActivity activity) {
        WifiP2pManager manager = (WifiP2pManager) activity.getSystemService(Context.WIFI_P2P_SERVICE);
        Channel channel = manager.initialize(activity, activity.getMainLooper(), null);
        return new NetworkHost(manager, channel, activity);
    }

    public NetworkHost(WifiP2pManager manager, Channel channel, AppCompatActivity activity) {
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
    }

    public NetworkReceiver createReceiver() {
        return new NetworkReceiver(manager, channel, activity);
    }
}
