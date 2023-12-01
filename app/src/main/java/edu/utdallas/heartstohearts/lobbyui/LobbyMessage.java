package edu.utdallas.heartstohearts.lobbyui;

import java.io.Serializable;
import java.net.InetAddress;

/**
 * A message used during lobby setup.
 */
public interface LobbyMessage {
}

// Sends the mac address and InetAddress of lobby members. If InetAddress is null, it is that of
// the sender, since the sender can't always verify their address easily.
class Greet implements LobbyMessage, Serializable {
    private static final long serialVersionUID = 841882668670464207L;
    public String mac;
    public InetAddress address;

    public Greet(String mac, InetAddress address) {
        this.mac = mac;
        this.address = address;
    }
}

class StartGame implements LobbyMessage, Serializable {
    private static final long serialVersionUID = 8175700297880807538L;
}