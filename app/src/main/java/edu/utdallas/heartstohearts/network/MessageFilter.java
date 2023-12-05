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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageFilter implements MessageListener {
    private List<Class> allowedTypes;
    private List<MessageListener> subscribers;

    /**
     * Creates a filter that only passes messages of the provided class type, using an "instance of"
     * relation.
     *
     * @param types
     */
    public MessageFilter(Class... types) {
        allowedTypes = Arrays.asList(types);
        subscribers = new ArrayList<>();
    }

    /**
     * Registers a listener to receive only filtered messages
     * @param l
     * @return
     */
    public MessageFilter addChildren(MessageListener... l) {
        subscribers.addAll(Arrays.asList(l));
        return this;
    }

    @Override
    public void messageReceived(final Object o, InetAddress author) {
        if (allowedTypes.stream().anyMatch((t) -> t.isInstance(o))) {
            // message matches type, broadcast
            subscribers.forEach((s) -> s.messageReceived(o, author));
        }
    }
}
