/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.utdallas.heartstohearts.app;

// import androidx.app.fragment.Fragment;

import android.app.Fragment;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import edu.utdallas.heartstohearts.R;
//import android.support.v4.content.FileProvider;

//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.OutputStream;


/**
 * A fragment that manages a particular peer and allows interaction with device
 * i.e. setting up network connection and transferring data.
 */
public class DeviceDetailView extends ConstraintLayout {
    private WifiP2pDevice device;
    private TextView name_field;
    private TextView status_field;

    private Button invite_button;

    public DeviceDetailView(@NonNull Context context) {
        super(context);
        inflate(context, R.layout.device_detail, this);

        name_field = (TextView) findViewById(R.id.name_field);
        status_field = (TextView) findViewById(R.id.status_field);
        invite_button = (Button) findViewById(R.id.invite_button);
    }

    public void setDevice(WifiP2pDevice device){
        boolean revalidate = (device == null) || !deviceInfoSame(this.device, device);

        this.device = device;

        if(revalidate){
            if (device == null) {
                name_field.setText("Unknown");
                status_field.setText("Unknown");
            } else {
                String name = device.deviceName;
                if (name.trim().isEmpty()) name = "<blank>";
                name_field.setText(name);
                status_field.setText(DeviceDetailView.deviceStatusString(device.status));
            }
            invalidate();
            requestLayout();
        }
    }

    public void showInviteButton(boolean should_show){
        int visibility = View.GONE;
        if(should_show){
            visibility = View.VISIBLE;
        }

        invite_button.setVisibility(visibility);
        invite_button.invalidate();
    }

    public Button getInviteButton(){
        return invite_button;
    }

    /**
     * Determines if two devices are physically the same. Currently uses address.
     * @param d1
     * @param d2
     * @return whether the two objects represent the same physical device
     */
    public static boolean deviceSame(WifiP2pDevice d1, WifiP2pDevice d2){
        if(d1 == null || d2 == null){
            return d1 == d2; // both null
        }

        return d1.deviceAddress.equals(d2.deviceAddress);
    }

    /**
     * @param d1
     * @param d2
     * @return whether the two objects hold the same information, in the context of the view. Presently,
     *  this cares only about device name, address, and status.
     */
    public static boolean deviceInfoSame(WifiP2pDevice d1, WifiP2pDevice d2){
        if(d1 == null || d2 == null){
            return d1 == d2; // both null
        }

        return deviceSame(d1, d2)
                && d1.status == d2.status
                && d1.deviceName.equals(d2.deviceName);
    }

    public static String deviceStatusString(int status_code){
        switch (status_code){
            case WifiP2pDevice.UNAVAILABLE:
                return "Unavailable";
            case WifiP2pDevice.AVAILABLE:
                return "Available";
            case WifiP2pDevice.FAILED:
                return "Failed";
            case WifiP2pDevice.INVITED:
                return "Invited";
            case WifiP2pDevice.CONNECTED:
                return "Connected";
            default:
                return "Unknown";
        }
    }
}