package edu.utdallas.heartstohearts.networkui;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import edu.utdallas.heartstohearts.network.Callback;

public class DeviceDetailAdapter extends ArrayAdapter<WifiP2pDevice> {


    boolean showInviteButton;
    Callback<WifiP2pDevice> onSelect;

    public DeviceDetailAdapter(@NonNull Context context, boolean showInviteButton) {
        super(context, 0);
        this.showInviteButton = showInviteButton;
        onSelect = (x) -> {};
    }

    public void onDeviceSelected(Callback<WifiP2pDevice> callback) {
        onSelect = callback;
    }

    private void deviceSelected(WifiP2pDevice device) {
        if (onSelect != null) onSelect.call(device);
    }

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
        view.getInviteButton().setOnClickListener((View v) -> {
            deviceSelected(device);
        });

        return view;
    }

    public void updateList(ArrayList<WifiP2pDevice> updatedList) {
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
