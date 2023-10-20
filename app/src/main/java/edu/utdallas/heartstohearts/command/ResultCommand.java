package edu.utdallas.heartstohearts.command;

import java.io.Serializable;

public abstract class ResultCommand<T extends Executor, R extends Serializable> implements Serializable {
    public abstract R execute(T executor);

    private static final long serialVersionUID = 1L;
}