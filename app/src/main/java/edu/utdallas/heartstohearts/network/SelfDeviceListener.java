package edu.utdallas.heartstohearts.network;

import android.net.wifi.p2p.WifiP2pDevice;

public interface SelfDeviceListener {
    void self_device_changed(WifiP2pDevice self);
}
