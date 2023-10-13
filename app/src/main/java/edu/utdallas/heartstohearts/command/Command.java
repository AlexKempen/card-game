package edu.utdallas.heartstohearts.command;

import java.io.Serializable;

public abstract class Command<T extends Executor> implements Serializable {
    /**
     * Indicates whether the process should exit after running the command.
     * Overridden by ExitCommand.
     *
     * @return : `true` if the Process should terminate, and false otherwise.
     */
    public boolean exit() {
        return false;
    }

    /**
     * Executes the command using the given executor.
     */
    public abstract void execute(T executor);

    private static final long serialVersionUID = 1L;
}
