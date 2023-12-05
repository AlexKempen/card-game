/**
 * Hearts to Hearts project
 * Senior design project, University of Texas at Dallas CS 4485.0W1
 * Fall 2023
 * <p>
 * File authors:
 * - Egan Johnson
 */

package edu.utdallas.heartstohearts.network;

import android.net.wifi.p2p.WifiP2pDevice;

public interface SelfDeviceListener {
    void selfDeviceChanged(WifiP2pDevice self);
}
