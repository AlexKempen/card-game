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

public class PeerConnection implements Closeable {

    private Socket socket;
    private ObjectInputStream messageInputStream;
    private ObjectOutputStream messageOutputStream;
    private Thread listeningThread = null;
    private Collection<MessageListener> listeners;

    private static final String TAG = "PeerConnection";

    /**
     * Connects to a server listening at the given host and port asynchronously, and calls back witthe result
     *
     * @param host
     * @param port
     * @param onConnectionAvailable called when the connection is available
     * @param onError               may be left null, in which case a RuntimeException called on error. Otherwise,
     *                              called whenever an error happens when connecting.
     */
    public static void fromAddressAsync(InetAddress host, int port, Callback<PeerConnection> onConnectionAvailable, Callback<IOException> onError) {
        new Thread(() -> {
            try {
                PeerConnection connection = PeerConnection.fromAddress(host, port);
                onConnectionAvailable.call(connection);
            } catch (IOException e) {
                Callback.callOrThrow(onError, e);
            }
        }).start();
    }

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
        Log.d(TAG, "Connecting to server at " + address.toString());
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
                            Callback.callOrThrow(onError, e);
                        } else if (e instanceof InterruptedException) {
                            break;
                        } else {
                            // Wrong type, rethrow
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        };
        listeningThread.start();
    }

    public void stopListening() {
        if (isListening()) {
            listeningThread.interrupt();
        }
    }

    public boolean isListening() {
        return (listeningThread != null && listeningThread.isAlive());
    }

    public boolean isOpen() {
        return socket.isConnected();
    }

    /**
     * Notifies all message listeners that a new message has been received.
     *
     * @param msg
     */
    protected void broadcastMessageRead(Object msg) {
        listeners.forEach((MessageListener l) -> l.messageReceived(msg));
    }

    /**
     * Registers a listener to receive future messages received on this connection
     *
     * @param l
     */
    public void addMessageListener(MessageListener l) {
        listeners.add(l);
    }

    /**
     * De-registers an existing listener from messages on this connection
     *
     * @param l
     */
    public void removeMessageListener(MessageListener l) {
        listeners.remove(l);
    }

    /**
     * Sends a message over this connection. Involved in IO operations: do not use on main thread.
     * Instead, use sendMessageAsync.
     *
     * @param msg
     * @throws IOException
     */
    public synchronized void sendMessage(Object msg) throws IOException {
        Log.d(TAG, "Sending Message: " + msg);
        messageOutputStream.writeObject(msg);
        messageOutputStream.flush();
    }

    /**
     * Sends a message on a new thread.
     *
     * @param msg
     * @param onError
     */
    public void sendMessageAsync(Object msg, @Nullable Callback<IOException> onError) {
        new Thread(() -> {
            try {
                sendMessage(msg);
            } catch (IOException e) {
                Callback.callOrThrow(onError, e);
            }
        }).start();
    }

    /**
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        stopListening();
        messageOutputStream.close();
        messageInputStream.close();
        // closing the streams is supposed to close the socket, but hey- doesn't hurt to check
        assert socket.isClosed();
    }
}