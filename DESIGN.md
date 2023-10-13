# Commands

The Command architecture utilizes the classes in the command package.

The included classes are used as follows:

### CommandInvoker

Corresponds roughly with a server. Uses a CommandStream to stream
Commands to a CommandReceiver (client), and to stream responses back.

### CommandReceiver

Corresponds roughly to a client. Uses a CommandStream to receive Commands
from a CommandInvoker (server), executes the commands, and uses the CommandStream
to send the results back to the server.

### CommandStream

A class wrapping two ObjectStreams. Used to send Commands in an abstract way.

### Executor

Corresponds to a business logic class on the Client which may be manipulated by Commands.
When a Command is executed, it receives an Executor as an argument and is free to manipulate it as
it sees fit.

ReadCommands may also extract information from the Executor to return back to the server.

### Command

An interface for a command. Commands are initialized like normal classes on the server.
They are then sent by an Invoker to a CommandReceiver hosted on the client.
The CommandReceiver executes the command by passing the Executor to it.
The Command may then perform CRUD operations on the Executor.

### ResultCommand

A variant of a command which can also return data to the server.




