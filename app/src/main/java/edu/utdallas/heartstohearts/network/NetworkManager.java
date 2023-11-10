package edu.utdallas.heartstohearts.network;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Network;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

public class NetworkManager extends BroadcastReceiver implements WifiP2pManager.ConnectionInfoListener {

    private static NetworkManager instance = null;

    private static final String TAG = "NetworkManager";

    protected Context context;

    WifiP2pManager manager;
    WifiP2pManager.Channel channel;

    private Set<WifiP2pManager.PeerListListener> peer_listeners;
    private Set<WifiP2pManager.ConnectionInfoListener> connection_listeners;
    private Set<SelfDeviceListener> self_listeners;


    private WifiP2pInfo last_connection_info = null;
    private WifiP2pDevice last_self_device = null;


    public static synchronized NetworkManager getInstance(Context context){
        if (instance == null) {
            instance = new NetworkManager(context);
        }
        return instance;
    }

    /**
     * Initializes this connection manager and registers as a broadcast receiver on the
     * provided context. Requests android services but does not start discovery.
     *
     * Set to protected since this is a singleton class
     *
     * @param context
     */
    protected NetworkManager(Context context) {
        this.context = context;

        peer_listeners = new HashSet<WifiP2pManager.PeerListListener>();
        connection_listeners = new HashSet<WifiP2pManager.ConnectionInfoListener>();
        self_listeners = new HashSet<SelfDeviceListener>();

        manager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(context, context.getMainLooper(), null);

        IntentFilter p2pIntents = new IntentFilter();
        p2pIntents.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        p2pIntents.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        p2pIntents.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        p2pIntents.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        context.registerReceiver(this, p2pIntents);

        addConnectionListener(this); // listen to own connection availability requests
    }

    /**
     * Asynchronously starts a connection with a peer.
     *
     * @param peer
     * @param actionListener - listens to success or failure. May be left null. Note only listens to
     *                       success/failure of initialization of connection: use a
     *                       WifiP2pManager.ConnectionInfoListener to track connection status.
     */
    @SuppressLint("MissingPermission")
    public void connectToPeer(WifiP2pDevice peer, WifiP2pManager.ActionListener actionListener) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = peer.deviceAddress;
        config.wps.setup = WpsInfo.PBC; // who knows what this does, it's in the tutorial

        if (actionListener == null) {
            actionListener = new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "Peer connection succeeded");
                }

                @Override
                public void onFailure(int i) {
                    Log.e(TAG, "Peer connection failed with error code " + i);
                }
            };
        }

        manager.connect(channel, config, actionListener);
    }

    /**
     * Splits out receive events to their respective functions, for clarity and inheritability.
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            onConnectionChanged(intent);
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            onPeersChanged(intent);
        } else if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            onP2PStateChanged(intent);
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            onP2PThisDeviceChanged(intent);
        } else {
            Log.e(TAG, "Received unexpected intent action: " + action);
        }
    }

    protected void onConnectionChanged(Intent intent) {
        // Need to request new connection info.
        connection_listeners.forEach((listener -> manager.requestConnectionInfo(channel, listener)));
    }

    @SuppressLint("MissingPermission")
    protected void onPeersChanged(Intent intent) {

        peer_listeners.forEach((listener) -> manager.requestPeers(channel, listener));
        Log.d(TAG, "Peers changed");
    }

    public void onP2PStateChanged(Intent intent) {
        // Check if WiFi direct mode is enabled
        int is_enabled_flag = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
        if (is_enabled_flag == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
            // TODO
            Log.d(TAG, "Wifi Direct Enabled");
        } else {
            // TODO
            Log.d(TAG, "Wifi Direct Disabled");
        }
    }

    public void onP2PThisDeviceChanged(Intent intent) {
         last_self_device = (WifiP2pDevice) intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);

         // notify listeners
        self_listeners.forEach((listener) -> listener.self_device_changed(last_self_device));
    }


    /**
     * Starts peer discovery asynchronously.
     *
     * @param listener: may be left null and a default will be used.
     */
    @SuppressLint("MissingPermission")
    public void discoverPeers(@Nullable WifiP2pManager.ActionListener listener) {
        if (listener == null) {
            listener = new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                } // Do nothing

                @Override
                public void onFailure(int i) {
                    Log.e(TAG, "Discover peers failed with code " + i);
                }
            };
        }
        manager.discoverPeers(channel, listener);
    }

    public void stopPeerDiscovery(@Nullable WifiP2pManager.ActionListener listener){
        if (listener == null) {
            listener = new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onFailure(int i) {
                    Log.e(TAG, "Failed to stop peer discovery with code " + i);
                }
            };
        }
        manager.stopPeerDiscovery(channel, listener);
    }

    public synchronized void addPeerListListener(WifiP2pManager.PeerListListener listener) {
        peer_listeners.add(listener);
    }

    public synchronized void removePeerListListener(WifiP2pManager.PeerListListener listener) {
        peer_listeners.remove(listener);
    }

    public synchronized void addConnectionListener(WifiP2pManager.ConnectionInfoListener listener) {
        connection_listeners.add(listener);
    }

    public synchronized void removeConnectionListener(WifiP2pManager.ConnectionInfoListener listener) {
        connection_listeners.remove(listener);
    }

    public synchronized void addSelfDeviceListener(SelfDeviceListener l){
        self_listeners.add(l);
    }

    public synchronized void removeSelfDeviceListener(SelfDeviceListener l){
        self_listeners.remove(l);
    }

    /**
     * @return the last known address of the group leader. Warning: this information may be out of
     * date. Register a ConnectionInfoListener to receive events about group changes.
     * <p>
     * May return null if no address known.
     */
    public  InetAddress getGroupLeaderAddress() {
        if (last_connection_info == null) {
            return null;
        }

        return last_connection_info.groupOwnerAddress;
    }

    public boolean isGroupLeader() {
        if (last_connection_info == null) {
            return false;
        }

        return last_connection_info.isGroupOwner;
    }

    public WifiP2pDevice getLastKnownSelf(){
        return last_self_device;
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
        last_connection_info = wifiP2pInfo;
    }

}
