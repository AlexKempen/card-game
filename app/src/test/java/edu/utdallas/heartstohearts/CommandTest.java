package edu.utdallas.heartstohearts;

import org.junit.Rule;
import org.junit.Test;
import org.junit.Before;
import org.junit.rules.TemporaryFolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import edu.utdallas.heartstohearts.command.CommandInvoker;
import edu.utdallas.heartstohearts.command.CommandReceiver;
import edu.utdallas.heartstohearts.command.CommandStream;
import edu.utdallas.heartstohearts.command.ExitCommand;
import edu.utdallas.heartstohearts.mock.Memory;
import edu.utdallas.heartstohearts.mock.ReadCommand;
import edu.utdallas.heartstohearts.mock.WriteCommand;

public class CommandTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    /**
     * Sets up tests by initializing two CommandStreams.
     * The serverStream represents a stream belonging to the server.
     * The clientStream represents a stream belonging to the client.
     */
    @Before
    public void initializeCommandStreams() throws IOException {
        File sentCommands = folder.newFile("commands.txt");
        File commandResults = folder.newFile("results.txt");

        serverStream = new CommandStream(new FileOutputStream(sentCommands));
        clientStream = new CommandStream(new FileOutputStream(commandResults));

        serverStream.addInputStream(new FileInputStream(commandResults));
        clientStream.addInputStream(new FileInputStream(sentCommands));
    }

    public CommandStream serverStream;
    public CommandStream clientStream;

    /**
     * Tests CommandInvoker to ensure that commands sent to it are properly piped to the receiverStream,
     * and that responses piped to the invokerStream are properly returned to the callee.
     */
    @Test
    public void TestCommandInvoker() {
        Memory memory = new Memory();
        CommandInvoker<Memory> server = new CommandInvoker<>(memory, serverStream);

        Integer value = 10;
        int testAddress = 5;
        WriteCommand writeCommand = new WriteCommand(testAddress, value);
        ReadCommand readCommand = new ReadCommand(testAddress);

        // read executor from stream
        assertNotNull(clientStream.<Memory>read());

        server.send(writeCommand);
        assertNotNull(clientStream.<WriteCommand>read());

        // write result before command is executed (since this test isn't async)
        clientStream.write(value);
        Integer result = server.send(readCommand);

        assertEquals(result, value);
        assertNotNull(clientStream.<ReadCommand>read());

        server.exit();
        assertInstanceOf(ExitCommand.class, clientStream.read());
        clientStream.close();
    }

    /**
     * Tests CommandReceiver, ensuring that commands sent to the invokerStream are properly processed and returned.
     */
    @Test
    public void TestCommandReceiver() {
        serverStream.write(new Memory());

        CommandReceiver client = new CommandReceiver(clientStream);

        Integer value = 10;
        int testAddress = 5;
        serverStream.write(new WriteCommand(testAddress, value));
        serverStream.write(new ReadCommand(testAddress));
        serverStream.write(new ExitCommand());

        client.processCommands();
        assertEquals(value, serverStream.<Integer>read());
        serverStream.close();
    }
}