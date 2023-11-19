package edu.utdallas.heartstohearts.network;

/**
 * Listens to whenever a peer connects.
 */
public interface PeerConnectionListener {
    public void peerConnected(PeerConnection connection);
}
