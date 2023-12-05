/**
 * Hearts to Hearts project
 * Senior design project, University of Texas at Dallas CS 4485.0W1
 * Fall 2023
 * <p>
 * File authors:
 * - Egan Johnson
 */

package edu.utdallas.heartstohearts.network;

import android.util.Log;

import androidx.annotation.Nullable;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A thread-safe event-driven connection class to wrap socket logic. All public methods are thread-safe,
 * but may involve IO operations: read the comments before using them on the main thread.
 * <p>
 * Events are dispatched on new threads which explicitly listen for them.
 */
public class PeerConnection implements Closeable {

    private Socket socket;
    private ObjectInputStream messageInputStream;
    private ObjectOutputStream messageOutputStream;

    private final Object listeningThreadLock = new Object();
    private Thread listeningThread = null;

    private final Object listenersLock = new Object();
    private Collection<MessageListener> listeners;

    private static final String TAG = "PeerConnection";

    private final Object stateLock = new Object();
    private ConnectionState state;


    /**
     * Creates and attempts to connect a new PeerConnection. Blocks: do not use on main thread.
     *
     * @param host
     * @param port
     * @throws IOException
     */
    public static PeerConnection fromAddress(InetAddress host, int port) throws IOException {

        Socket sock = new Socket();

        InetSocketAddress address = new InetSocketAddress(host, port);
        Log.d(TAG, "Connecting to server at " + address);
        sock.connect(address);

        return new PeerConnection(sock);
    }

    /**
     * Creates a PeerConnection from a pre-existing socket. Performs IO operations
     *
     * @param socket
     */
    public PeerConnection(Socket socket) throws IOException {
        this.socket = socket;

        OutputStream outputStream = socket.getOutputStream();
        InputStream inputStream = socket.getInputStream();

        // Do not transpose these next two lines!
        messageOutputStream = new ObjectOutputStream(outputStream);
        // This may block until the other end of the connection creates the ObjectOutputStream
        messageInputStream = new ObjectInputStream(inputStream);

        listeners = new ArrayList<>();
        // Should be no need to synchronize this as we are in the constructor and no threads are being
        // run.
        state = ConnectionState.CONNECTED;
        Log.d(TAG, "Peer Connection creation");
    }

    /**
     * Starts listening for messages coming on on the connection. Does nothing if already listening.
     *
     * @param onError called when either an IOException occurs (from the underlying socket) or a ClassNotFound
     *                exception occurs (from attempting to deserialized. If left null, the exception
     *                will simply be thrown.
     */
    public void listenForMessages(@Nullable Callback<Exception> onError) {
        if (listeningThread != null && listeningThread.isAlive()) return;

        listeningThread = new Thread() {
            public void run() {
                while (true) {
                    try {
                        Object msg = messageInputStream.readObject();
                        broadcastMessageRead(msg);
                    } catch (Exception e) {
                        if (e instanceof IOException || e instanceof ClassNotFoundException) {
                            setState(ConnectionState.ERROR);
                            Callback.callOrThrow(onError, e);
                        } else if (e instanceof InterruptedException) {
                            break;
                        } else {
                            // Wrong type, rethrow
                            setState(ConnectionState.ERROR);
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        };
        listeningThread.start();
    }

    /**
     * Stops this connection from reading incoming messages. This connection may still be used to
     * send messages.
     */
    public void stopListening() {
        synchronized (listeningThreadLock) {
            if (isListening()) { // Reentrant lock acquisition is allowed, this will not deadlock
                listeningThread.interrupt();
            }
        }
    }

    /**
     * @return true if this connection is currently listening.
     */
    public boolean isListening() {
        synchronized (listeningThreadLock) {
            return (listeningThread != null && listeningThread.isAlive());
        }
    }

    /**
     * Thread-safe state setter.
     *
     * @param newState
     */
    private void setState(ConnectionState newState) {
        synchronized (stateLock) {
            state = newState;
        }
    }

    /**
     * @return The current state of the socket. Note that, as the socket may fail at any time, this
     * provides a snapshot only.
     */
    public ConnectionState getState() {
        synchronized (stateLock) {
            return state;
        }
    }

    /**
     * Notifies all message listeners that a new message has been received.
     *
     * @param msg
     */
    private void broadcastMessageRead(Object msg) {
        synchronized (listenersLock) {
            listeners.forEach((MessageListener l) -> l.messageReceived(msg, getRemoteAddress()));
        }
    }

    /**
     * Registers a listener to receive future messages received on this connection. Messages are
     * dispatched on a separate thread than the caller: handle this properly if this is not desired.
     *
     * @param l
     */
    public void addMessageListener(MessageListener l) {
        synchronized (listenersLock) {
            listeners.add(l);
        }
    }

    /**
     * De-registers an existing listener from messages on this connection.
     *
     * @param l
     */
    public void removeMessageListener(MessageListener l) {
        synchronized (listenersLock) {
            listeners.remove(l);
        }
    }

    /**
     * Sends a message over this connection. Involved in IO operations: DO NOT USE on main thread.
     * Instead, use sendMessageAsync.
     *
     * @param msg
     * @throws IOException
     */
    public synchronized void sendMessage(Object msg) throws IOException {
        try {
            Log.d(TAG, "Sending Message: " + msg);
            messageOutputStream.writeObject(msg);
            messageOutputStream.flush();
        } catch (IOException e) {
            setState(ConnectionState.ERROR);
            throw e;
        }
    }

    /**
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        try {
            stopListening();
            messageOutputStream.close();
            messageInputStream.close();
            // Verify closing streams also closes the socket
            assert socket.isClosed();
        } catch (IOException e) {
            setState(ConnectionState.ERROR);
            throw e;
        }
        setState(ConnectionState.CLOSED);
    }

    /**
     * @return the address of the other end of the socket
     */
    public InetAddress getRemoteAddress() {
        return socket.getInetAddress();
    }
}