package edu.utdallas.heartstohearts.network;

import android.util.Log;

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

    public static void makeServerAsync(InetAddress host, int port, Callback<PeerServer> onServerCreated){
        new Thread(()->{
            try {
                PeerServer server = null;
                server = new PeerServer(host, port);
                onServerCreated.call(server);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public PeerServer(InetAddress host, int port) throws IOException {
        server_sock = new ServerSocket();
        server_sock.bind(new InetSocketAddress(host, port));
        listeners = new ArrayList<>();
    }

    public void startAcceptingConnections(){
        if(accept_connections_thread != null && accept_connections_thread.isAlive()) return;

        accept_connections_thread = new Thread(() -> {
            while (true) {
                try {
                    Log.d("PeerServer","Waiting to accept at " + server_sock.getLocalSocketAddress().toString());
                    Socket client = server_sock.accept();
                    Log.d("PeerServer", "Client socket connected. Creating peer connection");
                    PeerConnection connection = new PeerConnection(client);
                    notifyPeerConnected(connection);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        accept_connections_thread.start();
    }
    public void addPeerConnectionListener(PeerConnectionListener l){
        listeners.add(l);
    }

    public void removePeerConnectionListener(PeerConnectionListener l){
        listeners.remove(l);
    }

    protected void notifyPeerConnected(PeerConnection connection){
        Log.d("PeerServer", "Notifying new peer connection");
        listeners.forEach((l) -> l.peerConnected(connection));
    }

    /**
     * Leaves all spawned connections untouched but closes the server
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        if(accept_connections_thread != null){
            accept_connections_thread.interrupt();
        }
        server_sock.close();
    }

    /**
     * Listens to whenever a peer connects.
     */
    public interface PeerConnectionListener{
        public void peerConnected(PeerConnection connection);
    }
}

