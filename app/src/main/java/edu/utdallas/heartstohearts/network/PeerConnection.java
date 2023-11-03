package edu.utdallas.heartstohearts.network;

import android.util.Log;

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
    private ObjectInputStream message_input_stream;
    private ObjectOutputStream message_output_stream;
    private Thread listening_thread = null;
    private Collection<MessageListener> listeners;

    public static void fromAddressAsync(InetAddress host, int port, Callback<PeerConnection> onConnectionAvailable){
        new Thread(()->{
            try {
                PeerConnection connection = PeerConnection.fromAddress(host, port);
                onConnectionAvailable.call(connection);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    /**
     * Creates and attempts to connect a new PeerConnection. Blocks.
     *
     * @param host
     * @param port
     * @throws IOException
     */
    public static PeerConnection fromAddress(InetAddress host, int port) throws IOException {
        Socket sock = new Socket();
        // sock.bind(null);

        InetSocketAddress address = new InetSocketAddress(host, port);
        Log.d("PeerConnection", "Connecting to server at " +address.toString());
        sock.connect(address);

        return new PeerConnection(sock);
    }

    /**
     * Creates a PeerConnection from a pre-existing socket
     *
     * @param socket
     */
    public PeerConnection(Socket socket) throws IOException {
        this.socket = socket;
        OutputStream out_stream = socket.getOutputStream();
        InputStream in_stream = socket.getInputStream();
        message_output_stream = new ObjectOutputStream(out_stream);
        message_input_stream = new ObjectInputStream(in_stream);
        listeners = new ArrayList<MessageListener>();
        Log.d("PeerConnection", "Peer Connection creation completed");

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

    public void sendMessageAsync(Object msg, Callback<IOException> onError) {
        new Thread(()-> {
            try{
                sendMessage(msg);
            } catch (IOException e){
                if(onError == null){
                    throw new RuntimeException(e);
                } else {
                    onError.call(e);
                }
            }
        }).start();
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

