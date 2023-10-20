package edu.utdallas.heartstohearts.command;

import java.io.Serializable;

/**
 * Defines a command Executor which may be passed as an argument to a Command.
 * The only requirement for an Executor is that it must be serializable; the
 * rest is left up to the Command-Executor implementation.
 */
public abstract class Executor implements Serializable {
    private static final long serialVersionUID = 1L;
};