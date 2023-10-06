package edu.utdallas.heartstohearts.command;

import java.io.Serializable;

/**
 * Executes Commands by sending them to a given CommandProcess.
 * Note the given command process assumes ownership of a given executor.
 */
public class CommandInvoker<T extends Executor> {
    public CommandInvoker(T executor, CommandStream stream) {
        this.stream = stream;
        // send executor to the process
        stream.write(executor);
    }

    /**
     * Writes a command to out.
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
     * Note the underlying Process may still take some time to exit.
     */
    public void exit() {
        stream.write(new ExitCommand());
        stream.close();
    }

    private CommandStream stream;
}