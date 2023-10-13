package edu.utdallas.heartstohearts.mock;

import edu.utdallas.heartstohearts.command.Command;

/**
 * Writes data to Memory.
 */
public class WriteCommand extends Command<Memory> {
    public WriteCommand(int address, int data) {
        this.address = address;
        this.data = data;
    }

    @Override
    public void execute(Memory memory) {
        memory.write(address, data);
    }

    private int address;
    private int data;
}