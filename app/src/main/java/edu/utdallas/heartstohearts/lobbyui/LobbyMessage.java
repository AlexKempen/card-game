/**
 * Hearts to Hearts project
 * Senior design project, University of Texas at Dallas CS 4485.0W1
 * Fall 2023
 * <p>
 * File authors:
 * - Egan Johnson
 */
package edu.utdallas.heartstohearts.lobbyui;

import java.io.Serializable;
import java.net.InetAddress;

/**
 * A message used during lobby setup.
 */
public interface LobbyMessage {
}

/**
 * Sends the mac address and InetAddress of lobby members. If InetAddress is null, it is that of
 * the sender, since the sender can't always verify their address easily.
 */
class Greet implements LobbyMessage, Serializable {
    private static final long serialVersionUID = 841882668670464207L;
    public String mac;
    public InetAddress address;

    /**
     * @param mac Mac address of greeting device
     * @param address - Internet address of greeting device, or null if that of this sending device
     */
    public Greet(String mac, InetAddress address) {
        this.mac = mac;
        this.address = address;
    }
}

/**
 * Signals game start. Empty as receiving an instance and knowing the sender is sufficient.
 */
class StartGame implements LobbyMessage, Serializable {
    private static final long serialVersionUID = 8175700297880807538L;
}