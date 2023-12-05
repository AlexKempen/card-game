/**
 * Hearts to Hearts project
 * Senior design project, University of Texas at Dallas CS 4485.0W1
 * Fall 2023
 *
 * File authors:
 *  - Egan Johnson
 *  - Alex Kempen
 */

package edu.utdallas.heartstohearts.lobbyui;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Comparator;

import edu.utdallas.heartstohearts.network.Callback;

/**
 * UI element for showing list of nearby devices, with optional button for inviting to group
 */
public class DeviceDetailAdapter extends ArrayAdapter<WifiP2pDevice> {
    private boolean showInviteButton;
    private Callback<WifiP2pDevice> onSelect = null;

    /**
     * @param context UI context
     * @param showInviteButton - if the adapter should include invite button or not
     */
    public DeviceDetailAdapter(@NonNull Context context, boolean showInviteButton) {
        super(context, 0);
        this.showInviteButton = showInviteButton;
    }

    /**
     * Registers the given callback as the onDeviceSelected callback.
     */
    public void setOnDeviceSelected(Callback<WifiP2pDevice> callback) {
        onSelect = callback;
    }

    /**
     * Called whenever the button for a device is pressed
     * @param device - device associated with the pressed button
     */
    private void onDeviceSelected(WifiP2pDevice device) {
        if (onSelect != null) onSelect.call(device);
    }

    /**
     * ArrayAdapter boilerplate for creating the view for a device
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Either use convert view or create a fresh one
        DeviceDetailView view;
        if (convertView != null) {
            assert convertView instanceof DeviceDetailView;
            view = (DeviceDetailView) convertView;
        } else {
            view = new DeviceDetailView(getContext());
        }

        // Update view with device
        WifiP2pDevice device = getItem(position);
        view.setDevice(device);
        view.showInviteButton(showInviteButton);
        // register button press to trigger event with correct device
        view.getInviteButton().setOnClickListener((View v) -> {
            onDeviceSelected(device);
        });

        return view;
    }

    /**
     * Tells the adapter to track an updated list
     * @param updatedList
     */
    public void updateList(ArrayList<WifiP2pDevice> updatedList) {
        // Sorts so that nonempty devices are first
        updatedList.sort((a, b) -> {
            if (a.deviceName.isEmpty() && !b.deviceName.isEmpty()) return 1;
            else if (!a.deviceName.isEmpty() && b.deviceName.isEmpty()) return -1;
            else return a.status - b.status;
        });
        this.clear();
        this.addAll(updatedList);
        this.notifyDataSetChanged();
    }
}
