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
     * Reads and processes commands from the CommandStream until an ExitCommand is received.
     */
    public void processCommands() {
        while (true) {
            if (executeCommandObject(stream.readObject())) {
                stream.close();
                return;
            }
        }
    }

    /**
     * Executes a command object.
     *
     * @return `true` if the Receiver should exit afterwards.
     */
    private boolean executeCommandObject(Object object) {
        if (object instanceof Command<?>) {
            Command<Executor> command = CommandStream.castObject(object);
            command.execute(executor);
            return command.exit();
        } else if (object instanceof ResultCommand<?, ?>) {
            ResultCommand<Executor, ?> resultCommand = CommandStream.castObject(object);
            stream.write(resultCommand.execute(executor));
            return false; // always continue after ResultCommand
        }
        throw new AssertionError("Expected valid command.");
    }

    private final Executor executor;
    private final CommandStream stream;
}
