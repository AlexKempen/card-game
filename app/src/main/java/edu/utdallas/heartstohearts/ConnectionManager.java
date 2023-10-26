package edu.utdallas.heartstohearts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

/**
 * Major TODO: disconnect
 */
public class ConnectionManager extends BroadcastReceiver {

    private static final String TAG = "WifiDirect";

    protected Context context;
    private boolean initialized;
    WifiP2pManager manager;
    WifiP2pManager.Channel channel;
    IntentFilter p2pIntents;

    private Set<WifiP2pManager.PeerListListener> peer_listeners;


    /**
     * Initializes this connection manager and registers as a broadcast reciever on the
     * provided context. Note that no attempt is made to request android services, start discovery,
     * or anything else: initialize() must be called first.
     * @param context
     */
    public ConnectionManager(Context context){
        this.context = context;
        initialized = false;

        peer_listeners = new HashSet<WifiP2pManager.PeerListListener>();

        p2pIntents = new IntentFilter();
        p2pIntents.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        p2pIntents.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        p2pIntents.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        p2pIntents.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        register();
    }

    /**
     * Creates P2P managers and channels. Calling again once already initialized does nothing.
     *
     * Unsure if this needs to be separate from the constructor.
     * TODO: at time of writing there are a lot of locations, often involving registering the listener,
     * where manager/channel are used without checking if initialized.
     */
    public void initialize(){
        if(initialized) return;

        manager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(context, context.getMainLooper(), null);
        initialized = true;
    }

    public void register(){
        context.registerReceiver(this, p2pIntents);
    }

    public void unregister(){
        context.unregisterReceiver(this);
    }

    /**
     * Splits out receive events to their respective functions, for clarity and inheritability.
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            onConnectionChanged(intent);
        } else if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)){
            onPeersChanged(intent);
        } else if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)){
            onP2PStateChanged(intent);
        } else if(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)){
            onP2PThisDeviceChanged(intent);
        } else {
            Log.e(TAG, "Received unexpected intent action: " + action);
        }
    }

    // TODO: Do any of these methods need to be public?
    public void onConnectionChanged(Intent intent){} // TODO

    public void onPeersChanged(Intent intent){
        peer_listeners.forEach((listener) -> manager.requestPeers(channel, listener));
        Log.d(TAG, "Peers changed");
    }

    public void onP2PStateChanged(Intent intent){
        // Check if WiFi direct mode is enabled
        int is_enabled_flag = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
        if(is_enabled_flag == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
            // TODO
            Log.d(TAG, "Wifi Direct Enabled");
        } else{
            // TODO
            Log.d(TAG, "Wifi Direct Disabled");
        }
    }

    public void onP2PThisDeviceChanged(Intent intent){} // TODO


    /**
     * Starts peer discovery asynchronously.
     * @param listener: may be left null and a default will be used.
     */
    public void discoverPeers(WifiP2pManager.ActionListener listener){
        assert initialized;

        if (listener == null){
            listener = new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {} // Do nothing

                @Override
                public void onFailure(int i) {
                Log.e(TAG, "Discover peers failed with code " + i);
                }
            };
        }
        manager.discoverPeers(channel, listener);
    }

    public void addPeerListListener(WifiP2pManager.PeerListListener listener){
        peer_listeners.add(listener);
    }

    public void removePeerListListener(WifiP2pManager.PeerListListener listener){
        peer_listeners.remove(listener);
    }

    /**
     * Asynchronously starts a connection with a peer.
     * @param peer
     * @param actionListener - listens to success or failure. May be left null. Note only listens to
     *                       success/failure of initialization of connection: use a
     *                       WifiP2pManager.ConnectionInfoListener to track connection status.
     */
    public void connectToPeer(WifiP2pDevice peer, WifiP2pManager.ActionListener actionListener){
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = peer.deviceAddress;
        config.wps.setup = WpsInfo.PBC; // who knows what this does, it's in the tutorial

        if (actionListener == null){
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


}
