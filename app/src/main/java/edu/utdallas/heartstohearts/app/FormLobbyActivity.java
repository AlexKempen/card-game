package edu.utdallas.heartstohearts.app;

import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.PrintStream;
import java.util.ArrayList;

import edu.utdallas.heartstohearts.R;
import edu.utdallas.heartstohearts.network.MessageListener;
import edu.utdallas.heartstohearts.network.NetworkManager;
import edu.utdallas.heartstohearts.network.PeerConnection;
import edu.utdallas.heartstohearts.network.PeerServer;
import edu.utdallas.heartstohearts.network.SelfDeviceListener;
import kotlin.random.Random;


/**
 * Stage before the game starts where the 4 player devices find each other.
 */
public class FormLobbyActivity extends AppCompatActivity
        implements WifiP2pManager.PeerListListener, SelfDeviceListener
{

    NetworkManager p2p_network_manager;

    ListView connected_devices;
    DeviceDetailAdapter connected_devices_adapter;
    ListView nearby_devices;
    DeviceDetailAdapter nearby_devices_adapter;
    DeviceDetailView this_device_view;

    private final String TAG = "FormLobbyActivity";


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for(int i = 0; i < permissions.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "Permissions not granted! Request code: " + requestCode + " Permission: " + permissions[i]);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_lobby_activity);

        // Set up lists
        connected_devices = (ListView) findViewById(R.id.connected_devices_list);
        nearby_devices = (ListView) findViewById(R.id.nearby_devices_list);
        connected_devices_adapter = new DeviceDetailAdapter(getApplicationContext(), false);
        nearby_devices_adapter = new DeviceDetailAdapter(getApplicationContext(), true);
        nearby_devices.setAdapter(nearby_devices_adapter);
        connected_devices.setAdapter(connected_devices_adapter);

        // Set up this device view
        this_device_view = new DeviceDetailView(getApplicationContext());
        this_device_view.showInviteButton(false);
        // swap out
        View placeholder = findViewById(R.id.this_device_details_placeholder);
        ViewGroup parent = (ViewGroup) placeholder.getParent();
        int index = parent.indexOfChild(placeholder);
        parent.removeView(placeholder);
        parent.addView(this_device_view, index);

        // set up networking
        p2p_network_manager = NetworkManager.getInstance(getApplicationContext());
        p2p_network_manager.addPeerListListener(this);
        p2p_network_manager.addSelfDeviceListener(this);

        nearby_devices_adapter.onDeviceSelected((WifiP2pDevice device)->{
            p2p_network_manager.connectToPeer(device, null);
        });
    }

    /**
     * register the BroadcastReceiver with the intent values to be matched
     */
    @Override
    public void onResume() {
        super.onResume();
        p2p_network_manager.discoverPeers(new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {/* do nothing */}
            @Override
            public void onFailure(int i) {
                Toast.makeText(getApplicationContext(), "Unable to search for nearby devices!", Toast.LENGTH_LONG);
            }
        });
    }

    @Override
    public void onPause() {
        p2p_network_manager.stopPeerDiscovery(null);
        super.onPause();
    }

    @Override
    public void self_device_changed(WifiP2pDevice self) {
        this_device_view.setDevice(self);
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList devices) {
        ArrayList<WifiP2pDevice> connected_list = new ArrayList<>();
        ArrayList<WifiP2pDevice> nearby_list = new ArrayList<>();

        devices.getDeviceList().forEach((device) ->{
            if(device.status == WifiP2pDevice.CONNECTED){
                connected_list.add(device);
            } else {
                nearby_list.add(device);
            }
        });

        connected_devices_adapter.updateList(connected_list);
        nearby_devices_adapter.updateList(nearby_list);
    }
}