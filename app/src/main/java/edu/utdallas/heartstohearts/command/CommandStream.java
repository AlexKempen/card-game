package edu.utdallas.heartstohearts.command;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * A class wrapping an input and output stream which can be used to send
 * serialized command objects.
 */
public class CommandStream {
    public CommandStream(OutputStream out) {
        addOutputStream(out);
    }

    private void addOutputStream(OutputStream out) {
        try {
            this.out = new ObjectOutputStream(out);
        } catch (IOException e) {
            throw new AssertionError("Failed to construct object output stream.", e);
        }
    }

    /**
     * Registers an input stream.
     * Must be called after construction prior to using read.
     */
    public void addInputStream(InputStream in) {
        try {
            this.in = new ObjectInputStream(in);
        } catch (IOException e) {
            throw new AssertionError("Failed to construct object input stream.", e);
        }
    }

    /**
     * Writes Object o to the stream.
     */
    public void write(Object o) {
        try {
            out.writeObject(o);
            out.flush();
        } catch (IOException e) {
            throw new AssertionError("Failed to write object.", e);
        }
    }

    /**
     * Reads an Object of type T from the stream.
     */
    public <T> T read() {
        return this.<T>castObject(readObject());
    }

    /**
     * Reads an Object from the stream.
     */
    public Object readObject() {
        if (in == null) {
            throw new NullPointerException("Expected Output stream to be registered.");
        }

        try {
            return in.readObject();
        } catch (Exception e) {
            throw new AssertionError("Failed to read object.", e);
        }
    }

    /**
     * Performs an unchecked cast on Object to type T.
     */
    public <T> T castObject(Object o) {
        @SuppressWarnings("unchecked") T result = (T) o;
        return result;
    }

    public void close() {
        try {
            in.close();
            out.close();
        } catch (IOException e) {
            throw new AssertionError("Failed to close command streams.", e);
        }
    }

    private ObjectInputStream in;
    private ObjectOutputStream out;
}