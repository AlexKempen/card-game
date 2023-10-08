package edu.utdallas.heartstohearts.command;

import java.io.Serializable;

/**
 * Executes Commands by sending them to a client via a CommandStream.
 */
public class CommandInvoker<T extends Executor> {
    /**
     * @param executor: An executor, which is passed to the client and used to process future commands.
     * @param stream:   A CommandStream connecting to the client.
     */
    public CommandInvoker(T executor, CommandStream stream) {
        this.stream = stream;
        // send executor to the process
        stream.write(executor);
    }

    /**
     * Writes a Command to the client.
     */
    public void send(Command<T> command) {
        stream.write(command);
    }

    /**
     * Writes a ResultCommand to the client.
     *
     * @return The response from the client (which is generally the value returned by the Command).
     */
    public <R extends Serializable> R send(ResultCommand<T, R> command) {
        stream.write(command);
        return stream.read();
    }

    /**
     * Writes an ExitCommand to the client and closes the connection.
     * Note the client may still take some time to exit afterwards.
     */
    public void exit() {
        stream.write(new ExitCommand());
        stream.close();
    }

    private final CommandStream stream;
}