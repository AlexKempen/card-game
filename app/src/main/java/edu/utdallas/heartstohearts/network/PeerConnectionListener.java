/**
 * Hearts to Hearts project
 * Senior design project, University of Texas at Dallas CS 4485.0W1
 * Fall 2023
 * <p>
 * File authors:
 * - Egan Johnson
 */

package edu.utdallas.heartstohearts.network;

/**
 * Represents a class with a method which is invoked when a peer is connected.
 */
public interface PeerConnectionListener {
    void peerConnected(PeerConnection connection);
}
