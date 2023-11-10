package edu.utdallas.heartstohearts.app;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import edu.utdallas.heartstohearts.R;


/**
 * A fragment that manages a particular peer and allows interaction with device
 * i.e. setting up network connection and transferring data.
 */
public class DeviceDetailView extends ConstraintLayout {
    private WifiP2pDevice device;
    private TextView nameField;
    private TextView statusField;

    private Button inviteButton;

    public DeviceDetailView(@NonNull Context context) {
        super(context);
        inflate(context, R.layout.device_detail, this);

        nameField = (TextView) findViewById(R.id.name_field);
        statusField = (TextView) findViewById(R.id.status_field);
        inviteButton = (Button) findViewById(R.id.invite_button);
    }

    public void setDevice(WifiP2pDevice device) {
        boolean revalidate = (device == null) || !deviceInfoSame(this.device, device);

        this.device = device;

        if (revalidate) {
            if (device == null) {
                nameField.setText("Unknown");
                statusField.setText("Unknown");
            } else {
                String name = device.deviceName;
                if (name.trim().isEmpty()) name = "<blank>";
                nameField.setText(name);
                statusField.setText(DeviceDetailView.deviceStatusString(device.status));
            }
            invalidate();
            requestLayout();
        }
    }

    public void showInviteButton(boolean shouldShow) {
        int visibility = View.GONE;
        if (shouldShow) {
            visibility = View.VISIBLE;
        }

        inviteButton.setVisibility(visibility);
        inviteButton.invalidate();
    }

    public Button getInviteButton() {
        return inviteButton;
    }

    /**
     * Determines if two devices are physically the same. Currently uses address.
     *
     * @param d1
     * @param d2
     * @return whether the two objects represent the same physical device
     */
    public static boolean deviceSame(WifiP2pDevice d1, WifiP2pDevice d2) {
        if (d1 == null || d2 == null) {
            return d1 == d2; // both null
        }

        return d1.deviceAddress.equals(d2.deviceAddress);
    }

    /**
     * @param d1
     * @param d2
     * @return whether the two objects hold the same information, in the context of the view. Presently,
     * this cares only about device name, address, and status.
     */
    public static boolean deviceInfoSame(WifiP2pDevice d1, WifiP2pDevice d2) {
        if (d1 == null || d2 == null) {
            return d1 == d2; // both null
        }

        return deviceSame(d1, d2) && d1.status == d2.status && d1.deviceName.equals(d2.deviceName);
    }

    public static String deviceStatusString(int statusCode) {
        switch (statusCode) {
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