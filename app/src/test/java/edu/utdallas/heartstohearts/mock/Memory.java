package edu.utdallas.heartstohearts.mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.utdallas.heartstohearts.command.Executor;

/**
 * A mock example of a command executor (client).
 * This one trivially keeps track of an array of integers.
 */
public class Memory extends Executor {
    public int read(int address) {
        return memory.get(address);
    }

    public void write(int address, int data) {
        memory.set(address, data);
    }

    // initialize array list with 0
    private List<Integer> memory = new ArrayList<>(Collections.nCopies(2000, 0));
}