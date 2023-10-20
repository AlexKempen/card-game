package edu.utdallas.hearts2hearts;

import java.net.*;
import java.util.ArrayList;
import java.io.*;

public class Server extends Thread {

    private final int NUM_PLAYERS = 4;
    private int port;
    private ServerSocket serverSocket;
    private boolean[] clientsJoined;
    private Socket[] clientSockets;
    private ObjectOutputStream[] outputStreams;
    private ObjectInputStream[] inputStreams;


    public void startServer(){
        try{
            serverSocket = new ServerSocket(port);
            System.out.println("(Server) Server started.");
        }
        catch(IOException i){
            System.out.println(i);
        }
    }

    public void waitForClientConnections(){
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

    public void closeClientConnections (){
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

    public void closeServer(){
        try {
            serverSocket.close();
        }
        catch(IOException i){
            System.out.println(i);
        }
    }

    public void sendGameStateToClients(GameState gameState){
        // send GameState to clients
        for (int playerID = 0; playerID < NUM_PLAYERS; playerID++) {
            try {
                outputStreams[playerID].writeObject(gameState);
            }
            catch (IOException i) {
                System.out.println(i);
            }
        }
    }

    public GameState[] receiveGameStateFromClients(){
        GameState[] gameStates = new GameState[4];
        for (int i = 0; i < 4; i++){
            try{
                gameStates[i] = (GameState) inputStreams[i].readObject(); // block until first client writes
            }
            catch(IOException e){
                System.out.println(e);
            }
            catch(ClassNotFoundException e){
                System.out.println(e);
            }

        }
        return gameStates;
    }

    public void passAround(GameState gameState){

        sendGameStateToClients(gameState);
        GameState[] clientGameStates = receiveGameStateFromClients();
        ArrayList<ArrayList<Card>> cardsToPass = new ArrayList<ArrayList<Card>>();
        for (int i = 0; i < 4; i++){
            cardsToPass.add(clientGameStates[i].players[i].cardsToPlay);
        }
        switch(gameState.currentDirection){
            case LEFT: {
                gameState.players[0].hand.addAll(cardsToPass.get(3));
                gameState.players[1].hand.addAll(cardsToPass.get(0));
                gameState.players[2].hand.addAll(cardsToPass.get(1));
                gameState.players[3].hand.addAll(cardsToPass.get(2));
                break;
            }
            case RIGHT: {


                break;
            }
            case ACROSS: {

                break;
            }
            case NONE: {

            }
        }
        
    }

    public void run(){

        startServer();
        waitForClientConnections();

        GameState gameState = new GameState();  // initialize game
        passAround(gameState);
        
        
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
