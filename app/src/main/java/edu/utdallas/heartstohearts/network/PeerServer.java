package edu.utdallas.heartstohearts.network;

import android.util.Log;

import androidx.annotation.Nullable;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Listens on a given port for incoming PeerConnections: essentially a wrapped version of
 * ServerSocket
 */
public class PeerServer implements Closeable {
    private ServerSocket serverSocket;
    private Collection<PeerConnectionListener> listeners;
    private Thread acceptConnectionsThread;

    /**
     * Synchronously creates a server but does NOT start listening for connections.
     * <p>
     * Do not use on main thread, as it performs networking operations.
     *
     * @param port
     * @throws IOException
     */
    public PeerServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        listeners = new ArrayList<>();
    }

    /**
     * Asynchronously creates a server but does NOT start listing for connections.
     *
     * @param port
     * @param onServerCreated
     * @param onError
     */
    public static void makeServerAsync(int port,
                                       Callback<PeerServer> onServerCreated,
                                       @Nullable Callback<IOException> onError) {
        new Thread(() -> {
            try {
                PeerServer server = null;
                server = new PeerServer(port);
                onServerCreated.call(server);

            } catch (IOException e) {
                Callback.callOrThrow(onError, e);
            }
        }).start();
    }

    /**
     * Starts accepting client connections. Non-blocking: can be called on any thread.
     */
    public void startAcceptingConnections(@Nullable Callback<IOException> onError) {
        if (acceptConnectionsThread != null && acceptConnectionsThread.isAlive()) return;

        acceptConnectionsThread = new Thread(() -> {
            while (true) {
                try {
                    Log.d("PeerServer", "Waiting to accept at " + serverSocket.getLocalSocketAddress().toString());
                    Socket client = serverSocket.accept();
                    Log.d("PeerServer", "Client socket connected. Creating peer connection");
                    PeerConnection connection = new PeerConnection(client);
                    notifyPeerConnected(connection);
                } catch (IOException e) {
                    Callback.callOrThrow(onError, e);
                }
            }
        });
        acceptConnectionsThread.start();
    }

    /**
     * Listens for any incoming client connections.
     *
     * @param l
     */
    public void addPeerConnectionListener(PeerConnectionListener l) {
        listeners.add(l);
    }

    public void removePeerConnectionListener(PeerConnectionListener l) {
        listeners.remove(l);
    }

    public boolean isActive() {
        return acceptConnectionsThread.isAlive();
    }

    /**
     * Notifies all listeners of new connection using the callers thread.
     */
    protected void notifyPeerConnected(PeerConnection connection) {
        Log.d("PeerServer", "Notifying new peer connection");
        listeners.forEach((l) -> l.peerConnected(connection));
    }

    /**
     * Leaves all spawned connections untouched but closes the server.
     */
    @Override
    public void close() throws IOException {
        if (acceptConnectionsThread != null && acceptConnectionsThread.isAlive()) {
            acceptConnectionsThread.interrupt();
        }
        serverSocket.close();
    }
}

