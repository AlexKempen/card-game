package edu.utdallas.heartstohearts.command;

/**
 * A generic base class for managing an executor via a CommandInvoker.
 */
public class Manager<T extends Executor> {
    protected Manager(CommandInvoker<T> invoker) {
        this.invoker = invoker;
    }

    /**
     * Causes the command process to exit.
     */
    public void exit() {
        invoker.exit();
    }

    protected CommandInvoker<T> invoker;
}
