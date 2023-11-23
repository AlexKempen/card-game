package edu.utdallas.heartstohearts.network;

import java.net.InetAddress;

public interface MessageListener {
    void messageReceived(final Object o, InetAddress author);
}
