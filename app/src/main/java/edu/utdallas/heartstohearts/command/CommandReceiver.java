package edu.utdallas.heartstohearts.command;

/**
 * Defines a generic client for processing Commands received from a CommandStream.
 * The first object is expected to the Executor, which this class will automatically take ownership of.
 */
public class CommandReceiver {
    public CommandReceiver(CommandStream stream) {
        this.stream = stream;
        executor = stream.read();
    }

    /**
     * Reads and processes commands from stream until an ExitCommand is received.
     */
    public void processCommands() {
        while (true) {
            Object object = stream.readObject();
            if (executeCommandObject(object)) {
                stream.close();
                return;
            }
        }
    }

    /**
     * Executes a command object.
     *
     * @return true if the process should exit afterwards.
     */
    private boolean executeCommandObject(Object object) {
        if (object instanceof Command<?>) {
            Command<Executor> command = stream.castObject(object);
            command.execute(executor);
            return command.exit();
        } else if (object instanceof ResultCommand<?, ?>) {
            ResultCommand<Executor, ?> resultCommand = stream.castObject(object);
            stream.write(resultCommand.execute(executor));
            return false; // always continue after ResultCommand
        }
        throw new AssertionError("Expected valid command.");
    }

    private final Executor executor;
    private final CommandStream stream;
}
