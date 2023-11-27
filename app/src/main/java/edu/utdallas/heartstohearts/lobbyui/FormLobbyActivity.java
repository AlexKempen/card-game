package edu.utdallas.heartstohearts.lobbyui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import edu.utdallas.heartstohearts.R;
import edu.utdallas.heartstohearts.appui.BaseActivity;
import edu.utdallas.heartstohearts.gamenetwork.GameClient;
import edu.utdallas.heartstohearts.gamenetwork.GameServer;
import edu.utdallas.heartstohearts.gameui.GameActivity;
import edu.utdallas.heartstohearts.network.Callback;
import edu.utdallas.heartstohearts.network.MessageFilter;
import edu.utdallas.heartstohearts.network.MessageListener;
import edu.utdallas.heartstohearts.network.NetworkManager;
import edu.utdallas.heartstohearts.network.SelfDeviceListener;
import edu.utdallas.heartstohearts.network.Switchboard;


/**
 * Stage before the game starts where the 4 player devices find each other.
 */
public class FormLobbyActivity extends BaseActivity implements WifiP2pManager.PeerListListener, SelfDeviceListener, MessageListener, WifiP2pManager.ConnectionInfoListener {

    NetworkManager networkManager;
    Switchboard switchboard;

    ListView connectedDevices;
    DeviceDetailAdapter connectedDevicesAdapter;
    ListView nearbyDevices;
    DeviceDetailAdapter nearbyDevicesAdapter;
    DeviceDetailView thisDeviceView;

    Button startGameButton;

    LimitedLinkedHashMap<String, InetAddress> macToAddress;
    List<String> connectedMacs;

    private final String TAG = "FormLobbyActivity";


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "Permissions not granted! Request code: " + requestCode + " Permission: " + permissions[i]);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_lobby);


        macToAddress = new LimitedLinkedHashMap<>(3); // Only three peers allowed!;
        connectedMacs = new ArrayList<>();

        // Set up lists
        connectedDevices = (ListView) findViewById(R.id.connected_devices_list);
        nearbyDevices = (ListView) findViewById(R.id.nearby_devices_list);
        connectedDevicesAdapter = new DeviceDetailAdapter(getContext(), false);
        nearbyDevicesAdapter = new DeviceDetailAdapter(getContext(), true);
        nearbyDevices.setAdapter(nearbyDevicesAdapter);
        connectedDevices.setAdapter(connectedDevicesAdapter);

        // Set up this device view
        thisDeviceView = new DeviceDetailView(getContext());
        thisDeviceView.showInviteButton(false);

        // Start game button
        startGameButton = findViewById(R.id.button_start_game);
        startGameButton.setOnClickListener((view) -> startGame());

        // swap out
        View placeholder = findViewById(R.id.this_device_details_placeholder);
        ViewGroup parent = (ViewGroup) placeholder.getParent();
        int index = parent.indexOfChild(placeholder);
        parent.removeView(placeholder);
        parent.addView(thisDeviceView, index);

        // set up networking. Use application context since the network needs to exist over
        // multiple activities
        networkManager = NetworkManager.getInstance(getApplicationContext());
        networkManager.addPeerListListener(this);
        networkManager.addSelfDeviceListener(this);
        networkManager.addConnectionListener(this);

        nearbyDevicesAdapter.onDeviceSelected((WifiP2pDevice device) -> {
            networkManager.connectToPeer(device, null);
        });

        // Start accepting incoming connections for switchboard
        switchboard = Switchboard.getDefault();
        switchboard.addListener(null, new MessageFilter(LobbyMessage.class).addChildren(this));
        switchboard.acceptIncoming((error) -> Log.d("IncomingConnections", "Error accpting incoming: " + error));

    }

    /**
     * register the BroadcastReceiver with the intent values to be matched
     */
    @Override
    public void onResume() {
        super.onResume();
        networkManager.discoverPeers(new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {/* do nothing */}

            @Override
            public void onFailure(int i) {
                Toast.makeText(getContext(), "Unable to search for nearby devices!", Toast.LENGTH_LONG);
            }
        });

    }

    @Override
    public void onPause() {
        networkManager.stopPeerDiscovery(null);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // Need to deregister this as a listener to avoid potential null pointer issues
        networkManager.removePeerListListener(this);
        networkManager.removeSelfDeviceListener(this);
        switchboard.removeListener(null, this);
        super.onDestroy();
    }

    @Override
    public void selfDeviceChanged(WifiP2pDevice self) {
        thisDeviceView.setDevice(self);
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList devices) {
        ArrayList<WifiP2pDevice> connectedList = new ArrayList<>();
        ArrayList<WifiP2pDevice> nearbyList = new ArrayList<>();
        connectedMacs = new ArrayList<>();

        devices.getDeviceList().forEach((device) -> {
            if (device.status == WifiP2pDevice.CONNECTED) {
                connectedList.add(device);
                connectedMacs.add(device.deviceAddress);
            } else {
                nearbyList.add(device);
            }
        });

        // Maybe enable/disable game start button
        checkReadyForStart();

        connectedDevicesAdapter.updateList(connectedList);
        nearbyDevicesAdapter.updateList(nearbyList);
    }

    private void checkReadyForStart(){
        // ready if all devices have greeted.
        boolean ready = connectedMacs.stream().allMatch((mac) -> macToAddress.containsKey(mac));
        startGameButton.setEnabled(ready);
    }

    public void startGame() {
        // Launch the game server. Done in a new thread because, apparently, fetching localhost address
        // is networking on main thread.
        new Thread(() -> {
            Intent intent = new Intent(this, GameServer.class);
            List<String> playerAddresses = new ArrayList<>();

            InetAddress selfAddress;
            try {
                selfAddress = InetAddress.getLocalHost();
                playerAddresses.add(selfAddress.getHostAddress());
            } catch (UnknownHostException e) {
                Log.e(TAG, "Unable to find hostname for self: " + e);
                return;
            }

            macToAddress.forEach((mac, address) -> {
                Log.d(TAG, "Notifying player " + mac + " at address " + address + " of game start.");
                playerAddresses.add(address.getHostAddress());
                switchboard.sendMessageAsync(address, new StartGame(),
                        (e) -> Log.e(TAG, "Could not notify player of game start: " + e));
            });

            String[] parceledPlayers = new String[playerAddresses.size()];
            playerAddresses.toArray(parceledPlayers);
            intent.putExtra("players", parceledPlayers);
            Log.d(TAG, "Player array has n elements: " + parceledPlayers.length);

            startService(intent);

            joinGame(selfAddress);
        }).start();
    }

    public void joinGame(InetAddress host) {
        GameClient.setActiveClient(new GameClient(switchboard, host));
        startActivity(new Intent(this, GameActivity.class));
    }


    @Override
    public void messageReceived(Object o, InetAddress author) {
        if (author.isLoopbackAddress()) return; // in this case uninterested in responding to self

        Log.d(TAG, "Message received from " + author);
        if (o instanceof Greet) {
            Greet greet = (Greet) o;
            if (greet.address == null) {
                greet.address = author;
            }

            macToAddress.put(greet.mac, greet.address);

            if(networkManager.isGroupLeader()) {
                // If we are the lobby owner, distribute the greetings
                Callback<IOException> onError = (error) -> {
                    Log.e(TAG, "Unable to propagate greeting");
                };
                macToAddress.forEach((m, a) -> {
                    if (!m.equals(greet.mac)) {
                        // Connect previously-greeted device with new device
                        switchboard.sendMessageAsync(author, new Greet(m, a), onError);
                        switchboard.sendMessageAsync(a, greet, onError);
                    }
                });

                // send our own greetings
                String selfMac = networkManager.getLastKnownSelf().deviceAddress;
                switchboard.sendMessageAsync(author, new Greet(selfMac, null), (e) ->{
                    Log.e(TAG, "Unable to return greetings");
                });
            }

            this.runOnUiThread(this::checkReadyForStart);

        } else if (o instanceof StartGame) {
                joinGame(author);
        }
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
        // Addresses have possibly changed, greet group owner
        if (wifiP2pInfo.groupFormed) {
            Log.d(TAG, "Group formed");
            if (!wifiP2pInfo.isGroupOwner) {
                String selfMac = networkManager.getLastKnownSelf().deviceAddress;
                switchboard.sendMessageAsync(wifiP2pInfo.groupOwnerAddress, new Greet(selfMac, null),
                        (e) -> Log.e(TAG, "Unable to greet group owner: " + e));
            }
        } else {
            Log.d(TAG, "Group changed but not formed");
        }
    }
}

class LimitedLinkedHashMap<K, V> extends LinkedHashMap<K, V>{
    int maxSize = 0;
    public LimitedLinkedHashMap(int maxSize){
        super(maxSize);
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Entry entry) {
        return size() > maxSize;
    }
}