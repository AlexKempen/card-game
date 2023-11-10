package edu.utdallas.heartstohearts.app;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import edu.utdallas.heartstohearts.network.Callback;

public class DeviceDetailAdapter extends ArrayAdapter<WifiP2pDevice> {


    boolean show_invite_button;
    Callback<WifiP2pDevice> on_select;
    public DeviceDetailAdapter(@NonNull Context context, boolean show_invite_button) {
        super(context, 0);
        this.show_invite_button = show_invite_button;
        on_select = (x) -> {};
    }

    public void onDeviceSelected(Callback<WifiP2pDevice> callback){
        on_select = callback;
    }

    private void deviceSelected(WifiP2pDevice device){
        if (on_select != null) on_select.call(device);
    }

    @Override
    public View getView(int position, View convert_view, ViewGroup parent){

        // Either use convert view or create a fresh one
        DeviceDetailView view;
        if(convert_view != null){
            assert convert_view instanceof DeviceDetailView;
            view = (DeviceDetailView) convert_view;
        } else {
            view = new DeviceDetailView(getContext());
        }

        // Update view with device
        WifiP2pDevice device = getItem(position);
        view.setDevice(device);
        view.showInviteButton(show_invite_button);
        view.getInviteButton().setOnClickListener((View v)->{
            deviceSelected(device);
        });

        return view;
    }

    public void updateList(ArrayList<WifiP2pDevice> new_list){
        new_list.sort((a, b) ->{
            if (a.deviceName.isEmpty() && !b.deviceName.isEmpty()) return 1;
            else if (!a.deviceName.isEmpty() && b.deviceName.isEmpty()) return -1;
            else return a.status - b.status;
        });
        this.clear();
        this.addAll(new_list);
        this.notifyDataSetChanged();
    }
}
