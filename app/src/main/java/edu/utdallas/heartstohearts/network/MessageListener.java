/**
 * Hearts to Hearts project
 * Senior design project, University of Texas at Dallas CS 4485.0W1
 * Fall 2023
 * <p>
 * File authors:
 * - Egan Johnson
 */

package edu.utdallas.heartstohearts.network;

import java.net.InetAddress;

/**
 * Signature for a function accepting message events
 */
public interface MessageListener {
    void messageReceived(final Object o, InetAddress author);
}
