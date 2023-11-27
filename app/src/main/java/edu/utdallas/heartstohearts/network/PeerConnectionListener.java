package edu.utdallas.heartstohearts.network;

/**
 * Represents a class with a method which is invoked when a peer is connected.
 */
public interface PeerConnectionListener {
    void peerConnected(PeerConnection connection);
}
