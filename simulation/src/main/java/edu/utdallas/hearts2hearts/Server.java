package edu.utdallas.hearts2hearts;

import java.net.*;
import java.util.ArrayList;

import edu.utdallas.hearts2hearts.GameState.Direction;
import edu.utdallas.hearts2hearts.Message.MSG_TYPE;

import java.io.*;

public class Server extends Thread {

    private final int NUM_PLAYERS = 4;
    private int port;
    private ServerSocket serverSocket;
    private boolean[] clientsJoined;
    private Socket[] clientSockets;
    private ObjectOutputStream[] outputStreams;
    private ObjectInputStream[] inputStreams;


    private void startServer(){
        try{
            serverSocket = new ServerSocket(port);
            System.out.println("(Server) Server started.");
        }
        catch(IOException i){
            System.out.println(i);
        }
    }

    private void waitForClientConnections(){
        System.out.println("(Server) Waiting for players to join ...");
        for (int playerID = 0; playerID < NUM_PLAYERS; playerID++) {
            if (!clientsJoined[playerID]){
                try{
                    clientSockets[playerID] = serverSocket.accept(); // blocks until new connection comes in
                    outputStreams[playerID] = new ObjectOutputStream(clientSockets[playerID].getOutputStream());
                    inputStreams[playerID] = new ObjectInputStream(clientSockets[playerID].getInputStream());
                    clientsJoined[playerID] = true;
                    outputStreams[playerID].writeObject(playerID);  // send assigned ID back to client
                    System.out.printf("(Server) Client %d connected to server.\n", playerID);
                }
                catch(IOException i){
                    System.out.println(i);
                }
            }
        }
    }

    private void closeClientConnections (){
        for (int playerID = 0; playerID < NUM_PLAYERS; playerID++) {
            if (clientsJoined[playerID]){
                try {
                    inputStreams[playerID].close();
                    outputStreams[playerID].close();
                    clientSockets[playerID].close();
                    clientsJoined[playerID] = false;
                }
                catch(IOException i){
                    System.out.println(i);
                }
            }
        }
    }

    private void closeServer(){
        try {
            serverSocket.close();
        }
        catch(IOException i){
            System.out.println(i);
        }
    }

    private void sendGameStateToClients(GameState gameState){
        // send GameState to clients
        for (int playerID = 0; playerID < NUM_PLAYERS; playerID++) {
            try {
                Message msg = new Message(Message.MSG_TYPE.GAME_STATE, gameState);
                outputStreams[playerID].writeObject(msg);
            }
            catch (IOException i) {
                System.out.println(i);
            }
        }
    }

    private ArrayList<ArrayList<Card>> receiveCardsToPassFromClients(){
        ArrayList<ArrayList<Card>> cardsToPass = new ArrayList<ArrayList<Card>>();
        for (int i = 0; i < 4; i++){
            try{
                Message msg = (Message) inputStreams[i].readObject(); // block until first client writes
                if (msg.getMessageType() == MSG_TYPE.PASS_CARDS){
                    cardsToPass.add((ArrayList<Card>) msg.getObject());
                }
            }
            catch(IOException e){
                System.out.println(e);
            }
            catch(ClassNotFoundException e){
                System.out.println(e);
            }

        }
        return cardsToPass;
    }

    private void passingRound(GameState gameState){
        sendGameStateToClients(gameState);
        if (gameState.currentDirection == Direction.NONE)
            return;

        ArrayList<ArrayList<Card>> cardsToPass = receiveCardsToPassFromClients();
        System.out.println("(Server) Received cards to pass from clients.");

        // remove passed cards from current hands
        for (int playerID = 0; playerID < 4; playerID++){
            ArrayList<Card> cardsToRemove = cardsToPass.get(playerID);
            ArrayList<Card> handToRemoveFrom = gameState.players[playerID].hand;
            handToRemoveFrom.removeAll(cardsToRemove);
        }
        
        // repetitive but should work, someone could make this "neater"
        switch(gameState.currentDirection){
            case RIGHT: {
                gameState.players[0].hand.addAll(cardsToPass.get(1));
                gameState.players[1].hand.addAll(cardsToPass.get(2));
                gameState.players[2].hand.addAll(cardsToPass.get(3));
                gameState.players[3].hand.addAll(cardsToPass.get(0));
                break;
            }
            case LEFT: {
                gameState.players[0].hand.addAll(cardsToPass.get(3));
                gameState.players[1].hand.addAll(cardsToPass.get(0));
                gameState.players[2].hand.addAll(cardsToPass.get(1));
                gameState.players[3].hand.addAll(cardsToPass.get(2));
                break;
            }
            case ACROSS: {
                gameState.players[0].hand.addAll(cardsToPass.get(2));
                gameState.players[1].hand.addAll(cardsToPass.get(3));
                gameState.players[2].hand.addAll(cardsToPass.get(0));
                gameState.players[3].hand.addAll(cardsToPass.get(1));
                break;
            }
            case NONE: {
                // should never reach here
                break;
            }
        }

        System.out.println("(Server) Successfully passed cards to players in GameState.");
    }


    /* TODO: there's "turn" variable in GameState to monitor who starts (player index)
     * in passingRound() assign that turn to whoever has 2 of clubs
     * 
     * Turns should be clockwise
     * -> 2 ->
     * 1     3
     * <- 0 <-
    */
    private void playingRound(GameState gameState){
        sendGameStateToClients(gameState);
        
    }
    

    public void run(){

        startServer();
        waitForClientConnections();

        GameState gameState = new GameState();  // initialize game
        passingRound(gameState);
        playingRound(gameState);
        
        closeClientConnections();
        closeServer();
        return;
    }



    public Server (int port) {
        this.port = port;
        this.clientSockets = new Socket[NUM_PLAYERS];
        this.clientsJoined = new boolean[NUM_PLAYERS];
        this.inputStreams = new ObjectInputStream[NUM_PLAYERS];
        this.outputStreams = new ObjectOutputStream[NUM_PLAYERS];
    }
}
