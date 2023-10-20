package edu.utdallas.heartstohearts.mock;

import edu.utdallas.heartstohearts.command.ResultCommand;

/**
 * Reads data from Memory.
 */
public class ReadCommand extends ResultCommand<Memory, Integer> {
    public ReadCommand(int address) {
        this.address = address;
    }

    @Override
    public Integer execute(Memory memory) {
        return memory.read(address);
    }

    private int address;
}
