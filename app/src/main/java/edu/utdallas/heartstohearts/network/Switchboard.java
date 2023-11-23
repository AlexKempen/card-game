package edu.utdallas.heartstohearts.network;

import android.util.Log;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * A central mailbox for sending and receiving messages. Handles all server and client setup.
 * <p>
 * Not scalable: this class focuses on simplicity rather than scalability, since we should only be
 * working with small connection groups. For instance, we retain a list of all addresses forever,
 * creating a potential memory leak that doesn't occur in our use case, and do operations one at a time.
 */
public class Switchboard implements Closeable {

    private static final String TAG = "Switchboard";

    private static int defaultPort = 8888;
    private static final Object defaultSwitchboardWriteLock = new Object();
    private static Switchboard defaultSwitchboard;

    private final int port;

    private Object connectionsLock = new Object();
    private Map<InetAddress, List<PeerConnection>> connections;

    private Object listenersLock = new Object();
    private Map<InetAddress, List<MessageListener>> listeners;

    private PeerServer server;

    // Number of times to try opening a connection if the first attempt did not succeed.
    private int connectionRetries;
    private int connectionRetryInterval;


    public static Switchboard getDefault(){
        synchronized (defaultSwitchboardWriteLock){
            if(defaultSwitchboard == null){
                defaultSwitchboard = new Switchboard(defaultPort, 2, 1000);
            }
            return defaultSwitchboard;
        }
    }

    /**
     * Creates a new switchboard. Outgoing connections will attempt to connect to the provided port
     * on the remote machine, while (if started) this machine will listen for connections on the same
     * port.
     *
     * @param port
     * @param retryCount    - number of additional attempts to connect to a remote when creating a connection.
     *                      Set to 0 for no retry.
     * @param retryInterval - Time between connection attempts when retrying a connection, in milliseconds
     */
    public Switchboard(int port, int retryCount, int retryInterval) {
        this.port = port;

        connections = new HashMap<>();
        listeners = new HashMap<>();
        listeners.put(null, new ArrayList<>()); // null listeners should always be initialized

        connectionRetries = retryCount;
        connectionRetryInterval = retryInterval;
    }

    /**
     * Asynchronously begins accepting connection
     * @param onError called when there is an error in the startup or execution of the server
     * @throws IOException
     */
    public void acceptIncoming(Callback<IOException> onError) {
        new Thread(()-> {
            if (server == null || !server.isActive()) {
                try {
                    if(server != null){
                        server.close();
                    }
                    server = new PeerServer(port);
                    server.addPeerConnectionListener(Switchboard.this::registerConnection);
                    server.startAcceptingConnections(onError);
                } catch (IOException e){
                    Callback.callOrThrow(onError, e);
                }
            }
        }).start();
    }

    /**
     * Sends a message to the given recipient- no prior connection required. If a message cannot
     * be delivered, calls onError.
     * @param destination
     * @param msg
     * @param onError
     */
    public void sendMessageAsync(InetAddress destination, Object msg, Callback<IOException> onError){
        new Thread(()->{
            try {
                sendMessage(destination, msg);
            } catch(IOException e){
                Callback.callOrThrow(onError, e);
            }
        }).start();
    }

    /**
     * Registers a listener for incoming messages
     * @param source - Address to listen for messages from. If null the listener will receive all messages.
     * @param listener
     */
    public void addListener(InetAddress source, MessageListener listener){
        synchronized (listenersLock){
            getListenerList(source).add(listener);
        }
    }

    /**
     * De-registers a listener. Must be called once per time the listener was added.
     * @param source - Address used when addListener was called
     * @param listener - The listener object
     */
    public void removeListener(InetAddress source, MessageListener listener){
        synchronized (listenersLock){
            getListenerList(source).remove(listener);
        }
    }

    /**
     * Adds a new connection to those available to the switchboard and sets up desired listeners.
     * The connection should not already be listening to incoming messages.
     *
     * @param connection
     */
    private void registerConnection(PeerConnection connection) {
        InetAddress address = connection.getRemoteAddress();

        synchronized (connectionsLock) {

            List<PeerConnection> connectionsForAddress = getConnectionList(address);

            connection.addMessageListener(this::messageReceived);
            connectionsForAddress.add(connection);

            connection.listenForMessages((error) -> {
                Log.d(TAG, "Closing connection to " + address + " due to error " + error);
                try {
                    connection.close();
                } catch (IOException e) {
                    Log.e(TAG, "Unable to close connection to " + address + " due to error " + e);
                }
            });
        }
    }

    private synchronized void sendMessage(InetAddress destination, Object msg) throws IOException{
        Log.d(TAG, "Sending message of type " + msg.getClass() + " to " + destination);
        PeerConnection connection = getActiveConnection(destination);
        connection.sendMessage(msg);
    }

    /**
     * Return any active connection to the given address. If no connection is found, create one
     * synchronously.
     *
     * @param address
     * @return
     */
    private PeerConnection getActiveConnection(InetAddress address) throws IOException{
        synchronized (connectionsLock) {
            List<PeerConnection> addressConnections = getConnectionList(address);
            while (addressConnections.size() > 0){
                PeerConnection connection = addressConnections.get(0);
                // Remove disconnected connections
                if(connection.getState() != ConnectionState.CONNECTED){
                    connection.close();
                    addressConnections.remove(0);
                } else {
                    // Found an active connection, send using that
                    return connection;
                }
            }

            // no connected connection found, make one.
            PeerConnection connection = createConnection(address);
            return connection;
        }
    }

    /**
     * Finds the list for a given address or creates an empty one if not found
     * @param address
     * @return
     */
    private List<PeerConnection> getConnectionList(InetAddress address) {
        synchronized (connectionsLock) {
            if (!connections.containsKey(address)) connections.put(address, new ArrayList<>());

            return connections.get(address);
        }
    }

    /**
     * Finds the list for a given address or creates an empty one if not found
     * @param address
     * @return
     */
    private List<MessageListener> getListenerList(InetAddress address) {
        synchronized (listenersLock) {
            if (!listeners.containsKey(address)) listeners.put(address, new ArrayList<>());

            return listeners.get(address);
        }
    }

    /**
     * Connect to a remot address at the default port
     * @param address
     * @return
     * @throws IOException
     */
    private PeerConnection createConnection(InetAddress address) throws IOException {
        IOException originalError = null;
        PeerConnection connection = null;
        // continue trying until either success or hit retry maximum
        for (int i = 0; i < connectionRetries + 1 && connection == null; i++) {
            try {
                // if retry wait
                if (i > 0) Thread.sleep(connectionRetryInterval);
                connection = PeerConnection.fromAddress(address, port);

            } catch (IOException e) {
                if (originalError == null) {
                    originalError = e;
                }
            } catch (InterruptedException e) {
                // Not sure what to do about this one
                Log.e(TAG, "Interrupted when connecting: " + e);
            }
        }
        // All connection attempts failed
        if (originalError != null) {
            throw originalError;
        }

        registerConnection(connection);
        return connection;
    }

    /**
     * Distributes the object to interested listeners
     * @param source
     * @param msg
     */
    private void messageReceived(final Object msg, InetAddress source) {
        synchronized (listenersLock) {
            Log.d("TAG", "Received message of type " + msg.getClass() + " from " + source);
            // For all listeners to the given address or blanket/null listeners, dispatch the message
            // TODO what if listeners list null
            Stream<MessageListener> interestedListeners = Stream.concat(getListenerList(source).stream(), getListenerList(null).stream());
            interestedListeners.forEach((l) -> l.messageReceived(msg, source));
        }
    }

    @Override
    public void close() throws IOException {
        // TODO synchronization
        server.close(); // TODO check not null
    }
}
