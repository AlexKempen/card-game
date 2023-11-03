package edu.utdallas.heartstohearts.network;

import android.util.Log;

import androidx.annotation.Nullable;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Listens on a given port for incoming PeerConnections: essentially a wrapped version of
 * ServerSocket
 */
public class PeerServer implements Closeable {
    ServerSocket server_sock;
    Collection<PeerConnectionListener> listeners;
    Thread accept_connections_thread;

    // TODO is host necessary?

    /**
     * Asynchronously creates a server but does NOT start listing for connections.
     * @param host
     * @param port
     * @param onServerCreated
     * @param onError
     */
    public static void makeServerAsync(InetAddress host, int port,
                                       Callback<PeerServer> onServerCreated,
                                       @Nullable Callback<IOException> onError) {
        new Thread(() -> {
            try {
                PeerServer server = null;
                server = new PeerServer(host, port);
                onServerCreated.call(server);

            } catch (IOException e) {
                CallbackUtils.callOrThrow(onError, e);
            }
        }).start();
    }

    /**
     * Synchronously creates a server but does NOT start listining for connections.
     *
     * Do not use on main thread, as it performs networking operations.
     * @param host
     * @param port
     * @throws IOException
     */
    public PeerServer(InetAddress host, int port) throws IOException {
        server_sock = new ServerSocket();
        server_sock.bind(new InetSocketAddress(host, port));
        listeners = new ArrayList<>();
    }

    /**
     * Starts accepting client connections. Non-blocking: can be called on any thread.
     */
    public void startAcceptingConnections(@Nullable Callback<IOException> onError) {
        if (accept_connections_thread != null && accept_connections_thread.isAlive()) return;

        accept_connections_thread = new Thread(() -> {
            while (true) {
                try {
                    Log.d("PeerServer", "Waiting to accept at " + server_sock.getLocalSocketAddress().toString());
                    Socket client = server_sock.accept();
                    Log.d("PeerServer", "Client socket connected. Creating peer connection");
                    PeerConnection connection = new PeerConnection(client);
                    notifyPeerConnected(connection);
                } catch (IOException e) {
                    CallbackUtils.callOrThrow(onError, e);
                }
            }
        });
        accept_connections_thread.start();
    }

    /**
     * Listens for any incoming client connections.
     * @param l
     */
    public void addPeerConnectionListener(PeerConnectionListener l) {
        listeners.add(l);
    }

    public void removePeerConnectionListener(PeerConnectionListener l) {
        listeners.remove(l);
    }

    /**
     * Notifies all listeners of new connection, using the callers thread.
     * @param connection
     */
    protected void notifyPeerConnected(PeerConnection connection) {
        Log.d("PeerServer", "Notifying new peer connection");
        listeners.forEach((l) -> l.peerConnected(connection));
    }

    /**
     * Leaves all spawned connections untouched but closes the server
     *
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        if (accept_connections_thread != null && accept_connections_thread.isAlive()) {
            accept_connections_thread.interrupt();
        }
        server_sock.close();
    }
}

