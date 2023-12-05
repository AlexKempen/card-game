/**
 * Hearts to Hearts project
 * Senior design project, University of Texas at Dallas CS 4485.0W1
 * Fall 2023
 * <p>
 * File authors:
 * - Egan Johnson
 */

package edu.utdallas.heartstohearts.network;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Singleton managing the wifi state of the device.
 *
 * TODO synchronization
 */
public class NetworkManager extends BroadcastReceiver implements WifiP2pManager.ConnectionInfoListener {

    // Log tag
    private static final String TAG = "NetworkManager";

    private static NetworkManager instance = null;


    protected Context context;

    WifiP2pManager manager;
    WifiP2pManager.Channel channel;

    // Track registered listeners
    private Set<WifiP2pManager.PeerListListener> peerListListeners = new HashSet<>();
    private Set<WifiP2pManager.ConnectionInfoListener> connectionInfoListeners = new HashSet<>();
    private Set<SelfDeviceListener> selfDeviceListeners = new HashSet<>();


    private WifiP2pInfo lastConnectionInfo = null;
    private WifiP2pDevice lastSelfDevice = null;


    /**
     * Gets the global instance for the network manager
     * @param context - should be the application-wide context, NOT activity-specific.
     * @return
     */
    public static synchronized NetworkManager getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkManager(context);
        }
        return instance;
    }

    /**
     * Initializes this connection manager and registers as a broadcast receiver on the
     * provided context. Requests android services but does not start discovery.
     * <p>
     * Set to protected since this is a singleton class
     *
     * @param context
     */
    protected NetworkManager(Context context) {
        this.context = context;

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
            onConnectionChanged();
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            onPeersChanged();
        } else if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            onP2PStateChanged(intent);
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            onP2PThisDeviceChanged(intent);
        } else {
            Log.e(TAG, "Received unexpected intent action: " + action);
        }
    }

    protected void onConnectionChanged() {
        // Request new connection info to be sent  to each listener
        connectionInfoListeners.forEach((listener -> manager.requestConnectionInfo(channel, listener)));
    }

    @SuppressLint("MissingPermission")
    protected void onPeersChanged() {
        // Request peer information to be sent to each listener
        manager.requestPeers(channel, (result) -> {
            peerListListeners.forEach((listener) -> listener.onPeersAvailable(result));
        });
        Log.d(TAG, "Peers changed");
    }

    public void onP2PStateChanged(Intent intent) {
        // Check if WiFi direct mode is enabled
        int isEnabledFlag = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
        if (isEnabledFlag == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
            // TODO
            Log.d(TAG, "Wifi Direct Enabled");
        } else {
            // TODO
            Log.d(TAG, "Wifi Direct Disabled");
        }
    }

    public void onP2PThisDeviceChanged(Intent intent) {
        lastSelfDevice = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
        // notify listeners
        selfDeviceListeners.forEach((listener) -> listener.selfDeviceChanged(lastSelfDevice));
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

    /**
     * Shuts down peer discovery until startPeerDiscovery next called.
     * @param listener
     */
    public void stopPeerDiscovery(@Nullable WifiP2pManager.ActionListener listener) {
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
        peerListListeners.add(listener);
    }

    public synchronized void removePeerListListener(WifiP2pManager.PeerListListener listener) {
        peerListListeners.remove(listener);
    }

    public synchronized void addConnectionListener(WifiP2pManager.ConnectionInfoListener listener) {
        connectionInfoListeners.add(listener);
    }

    public synchronized void removeConnectionListener(WifiP2pManager.ConnectionInfoListener listener) {
        connectionInfoListeners.remove(listener);
    }

    public synchronized void addSelfDeviceListener(SelfDeviceListener l) {
        selfDeviceListeners.add(l);
    }

    public synchronized void removeSelfDeviceListener(SelfDeviceListener l) {
        selfDeviceListeners.remove(l);
    }

    public WifiP2pInfo getLastConnectionInfo() {
        return lastConnectionInfo;
    }

    public boolean isGroupLeader() {
        if (lastConnectionInfo == null) {
            return false;
        }
        return lastConnectionInfo.isGroupOwner;
    }

    public WifiP2pDevice getLastKnownSelf() {
        return lastSelfDevice;
    }


    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
        lastConnectionInfo = wifiP2pInfo;
    }
}
