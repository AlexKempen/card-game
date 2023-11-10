package edu.utdallas.heartstohearts.app;

import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import edu.utdallas.heartstohearts.R;
import edu.utdallas.heartstohearts.network.NetworkManager;
import edu.utdallas.heartstohearts.network.SelfDeviceListener;


/**
 * Stage before the game starts where the 4 player devices find each other.
 */
public class FormLobbyActivity extends AppCompatActivity implements WifiP2pManager.PeerListListener, SelfDeviceListener {

    NetworkManager networkManager;

    ListView connectedDevices;
    DeviceDetailAdapter connectedDevicesAdapter;
    ListView nearbyDevices;
    DeviceDetailAdapter nearbyDevicesAdapter;
    DeviceDetailView thisDeviceView;

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
        setContentView(R.layout.form_lobby_activity);

        // Set up lists
        connectedDevices = (ListView) findViewById(R.id.connected_devices_list);
        nearbyDevices = (ListView) findViewById(R.id.nearby_devices_list);
        connectedDevicesAdapter = new DeviceDetailAdapter(getApplicationContext(), false);
        nearbyDevicesAdapter = new DeviceDetailAdapter(getApplicationContext(), true);
        nearbyDevices.setAdapter(nearbyDevicesAdapter);
        connectedDevices.setAdapter(connectedDevicesAdapter);

        // Set up this device view
        thisDeviceView = new DeviceDetailView(getApplicationContext());
        thisDeviceView.showInviteButton(false);
        // swap out
        View placeholder = findViewById(R.id.this_device_details_placeholder);
        ViewGroup parent = (ViewGroup) placeholder.getParent();
        int index = parent.indexOfChild(placeholder);
        parent.removeView(placeholder);
        parent.addView(thisDeviceView, index);

        // set up networking
        networkManager = NetworkManager.getInstance(getApplicationContext());
        networkManager.addPeerListListener(this);
        networkManager.addSelfDeviceListener(this);

        nearbyDevicesAdapter.onDeviceSelected((WifiP2pDevice device) -> {
            networkManager.connectToPeer(device, null);
        });
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
                Toast.makeText(getApplicationContext(), "Unable to search for nearby devices!", Toast.LENGTH_LONG);
            }
        });
    }

    @Override
    public void onPause() {
        networkManager.stopPeerDiscovery(null);
        super.onPause();
    }

    @Override
    public void selfDeviceChanged(WifiP2pDevice self) {
        thisDeviceView.setDevice(self);
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList devices) {
        ArrayList<WifiP2pDevice> connectedList = new ArrayList<>();
        ArrayList<WifiP2pDevice> nearbyList = new ArrayList<>();

        devices.getDeviceList().forEach((device) -> {
            if (device.status == WifiP2pDevice.CONNECTED) {
                connectedList.add(device);
            } else {
                nearbyList.add(device);
            }
        });

        connectedDevicesAdapter.updateList(connectedList);
        nearbyDevicesAdapter.updateList(nearbyList);
    }
}