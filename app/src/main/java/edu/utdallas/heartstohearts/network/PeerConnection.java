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
    private ObjectInputStream message_input_stream;
    private ObjectOutputStream message_output_stream;
    private Thread listening_thread = null;
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
    public static void fromAddressAsync(InetAddress host, int port,
                                        Callback<PeerConnection> onConnectionAvailable,
                                        Callback<IOException> onError) {
        new Thread(() -> {
            try {
                PeerConnection connection = PeerConnection.fromAddress(host, port);
                onConnectionAvailable.call(connection);
            } catch (IOException e) {
                CallbackUtils.callOrThrow(onError, e);
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

        OutputStream out_stream = socket.getOutputStream();
        InputStream in_stream = socket.getInputStream();

        // Do not transpose these next two lines!
        message_output_stream = new ObjectOutputStream(out_stream);
        // This may block until the other end of the connection creates the ObjectOutputStream
        message_input_stream = new ObjectInputStream(in_stream);

        listeners = new ArrayList<MessageListener>();
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
        if (listening_thread != null && listening_thread.isAlive()) return;

        listening_thread = new Thread() {
            public void run() {
                while (true) {
                    try {
                        Object msg = message_input_stream.readObject();
                        broadcastMessageRead(msg);
                    }
                    catch (Exception e) {
                        if (e instanceof IOException || e instanceof ClassNotFoundException) {
                            CallbackUtils.callOrThrow(onError, e);
                        } else if (e instanceof InterruptedException){
                            break;
                        }{
                            // Wrong type, rethrow
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        };
        listening_thread.start();
    }

    public void stopListening(){
        if (listening_thread != null && listening_thread.isAlive()){
            listening_thread.interrupt();
        }
    }

    /**
     * Notifies all message listeners that a new message has been recieved
     * @param msg
     */
    protected void broadcastMessageRead(Object msg) {
        listeners.forEach((MessageListener l) -> l.messageReceived(msg));
    }

    /**
     * Registers a listener to receive future messages received on this connection
     * @param l
     */
    public void addMessageListener(MessageListener l) {
        listeners.add(l);
    }

    /**
     * De-registers an existing listener from messages on this connection
     * @param l
     */
    public void removeMessageListener(MessageListener l) {
        listeners.remove(l);
    }

    /**
     * Sends a message over this connection. Involved in IO operations: do not use on main thread.
     * Instead, use sendMessageAsync.
     * @param msg
     * @throws IOException
     */
    public void sendMessage(Object msg) throws IOException {
        message_output_stream.writeObject(msg);
        message_output_stream.flush();
    }

    /**
     * Sends a message on a new thread.
     * @param msg
     * @param onError
     */
    public void sendMessageAsync(Object msg, @Nullable Callback<IOException> onError) {
        new Thread(() -> {
            try {
                sendMessage(msg);
            } catch (IOException e) {
                if (onError == null) {
                    throw new RuntimeException(e);
                } else {
                    onError.call(e);
                }
            }
        }).start();
    }

    /**
     *
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        if (listening_thread != null && listening_thread.isAlive()) {
            listening_thread.interrupt();
        }
        message_output_stream.close();
        message_input_stream.close();
        // closing the streams is supposed to close the socket, but hey- doesn't hurt to check
        assert  socket.isClosed();
    }
}