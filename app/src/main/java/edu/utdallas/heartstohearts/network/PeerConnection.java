package edu.utdallas.heartstohearts.network;

import android.net.InetAddresses;
import android.util.Log;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;

public class PeerConnection implements Closeable {

    private Socket socket;
    private ObjectInputStream message_input_stream;
    private ObjectOutputStream message_output_stream;
    private Thread listening_thread = null;
    private Collection<MessageListener> listeners;

    public static void connectAsync(InetAddress host, int port, Callback<PeerConnection> onConnectionAvailable){
        new Thread(()->{
            try {
                PeerConnection connection = new PeerConnection(host, port);
                onConnectionAvailable.call(connection);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    /**
     * Creates and attempts to connect a new PeerConnection
     *
     * @param host
     * @param port
     * @throws IOException
     */
    public PeerConnection(InetAddress host, int port) throws IOException {
        this(new Socket(host, port));
    }

    /**
     * Creates a PeerConnection from a pre-existing socket
     *
     * @param socket
     */
    public PeerConnection(Socket socket) throws IOException {
        this.socket = socket;
        Log.d("PeerConnection", "Post-socket creation");
        message_input_stream = new ObjectInputStream(socket.getInputStream());
        message_output_stream = new ObjectOutputStream(socket.getOutputStream());

        listeners = new ArrayList<MessageListener>();
    }

    public void listenForMessages() {
        if (listening_thread != null && listening_thread.isAlive()) return;

        listening_thread = new Thread() {
            public void run() {
                while (true) {
                    try {
                        Object msg = message_input_stream.readObject();
                        broadcastMessageRead(msg);
                    } catch (IOException e) {
                        //TODO
                        assert false : "TODO: handle socket failure error";
                    } catch (ClassNotFoundException e) {
                        //TODO
                        throw new RuntimeException(e);
                    }
                }
            }
        };
        listening_thread.start();
    }

    protected void broadcastMessageRead(Object msg) {
        listeners.forEach((MessageListener l) -> l.messageReceived(msg));
    }

    public void addMessageListener(MessageListener l) {
        listeners.add(l);
    }

    public void removeMessageListener(MessageListener l) {
        listeners.remove(l);
    }

    public void sendMessage(Object msg) throws IOException {
        message_output_stream.writeObject(msg);
        message_output_stream.flush();
    }

    @Override
    public void close() throws IOException {
        if (listening_thread != null) {
            listening_thread.interrupt();
        }
        message_output_stream.close();
        message_input_stream.close();
        socket.close();
    }


    public interface MessageListener {
        void messageReceived(Object o);
    }
}

