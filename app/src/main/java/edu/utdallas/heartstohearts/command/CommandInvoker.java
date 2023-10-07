package edu.utdallas.heartstohearts.command;

import java.io.Serializable;

/**
 * Executes Commands by sending them to a given CommandStream.
 * Generally speaking, the CommandStream should be hooked to a CommandReceiver, which
 * will execute the Commands on the passed in Executor.
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
     * Writes a Command to out.
     */
    public void send(Command<T> command) {
        stream.write(command);
    }

    /**
     * Writes a ResultCommand to out and returns the result.
     */
    public <R extends Serializable> R send(ResultCommand<T, R> command) {
        stream.write(command);
        return stream.read();
    }

    /**
     * Writes an exit command to the stream.
     * Note the underlying CommandReceiver may still take some time to exit.
     */
    public void exit() {
        stream.write(new ExitCommand());
        stream.close();
    }

    private final CommandStream stream;
}