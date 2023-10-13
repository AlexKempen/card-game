package edu.utdallas.heartstohearts.command;

/**
 * A command which can be used to cause a CommandProcess to
 * gracefully close. Note this is exposed via CommandInvoker.exit(),
 * and should not be used directly.
 */
public class ExitCommand extends Command<Executor> {
    @Override
    public boolean exit() {
        return true;
    }

    @Override
    public void execute(Executor executor) {
        // do nothing
    }
}