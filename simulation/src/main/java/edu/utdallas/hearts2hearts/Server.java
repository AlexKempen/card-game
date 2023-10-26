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

    // takes playerID as parameter so it knows which input stream to check
    private Card receiveCardToPlayFromClient(int playerID){
        Card cardToPlay = new Card(0, 0);
        try{
            Message msg = (Message) inputStreams[playerID].readObject(); // block until client writes
            if (msg.getMessageType() == MSG_TYPE.PLAY_CARD){
                cardToPlay = (Card) msg.getObject();
            }
        }
        catch(IOException e){
            System.out.println(e);
        }
        catch(ClassNotFoundException e){
            System.out.println(e);
        }
    }
    private void passingRound(GameState gameState){
        sendGameStateToClients(gameState);
        /*
        if (gameState.currentDirection == Direction.NONE)
            return;
        */

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
                break;
            }

            // ASSIGN TURN TO WHOEVER HAS 2 OF CLUBS
            Card twoOfClubs = new Card(0, 0);
            for (int playerID = 0; i < 4; playerID++) {
                for (int cardIndex = 0; cardIndex < 13; cardIndex++) {
                    if (gameState.players[playerID].hand.get(cardIndex).equals(twoOfClubs)) {
                        gameState.turn = playerID;
                    }
                }
                
            }
        }

        System.out.println("(Server) Successfully passed cards to players in GameState.");
    }

    /*TODO: determine the winner of the trick based on the trump suit and currentPlay list
    * For now, just return player 0 as the winner
    */
    private int determineWinnerOfTrick(GameState gameState) {
        return 0;
    }

    private void giveTrickToWinner(GameState gameState, int winner) {
        gameState.players[winner].tricksTaken.addAll(gameState.currentPlay);
    }

    private void givePlayersPoints(GameState gameState) {
        int pointsGainedThisRound = 0;
        for (int player = 0; player < 4; player++) {
            pointsGainedThisRound = 0;
            for (int cardIndex = 0; cardIndex < gameState.players[player].tricksTaken.size(); cardIndex++) {
                if (gameState.players[player].tricksTaken.get(cardIndex).getRank() == 3) { // card is a Heart
                    pointsGainedThisRound += 1;
                }
                else if (gameState.players[player].tricksTaken.get(cardIndex).getRank() == 1gameState.players[player].tricksTaken.get(cardIndex).getSuit() == 10) { // card is Queen of Spades
                    pointsGainedThisRound += 13;
                }
            }
            if (pointsGainedThisRound == 26) { //player shot the moon, subtract 26 points instead
                pointsGainedThisRound = -26;
            }
            gameState.players[player].points += pointsGainedThisRound;
        }
    }


    /* Turns should be clockwise
     * -> 2 ->
     * 1     3
     * <- 0 <-
    */
    private void playingRound(GameState gameState){
        sendGameStateToClients(gameState);
        for (int trickNumber = 0; trickNumber < 13; trickNumber++) { // 13 tricks per round played
            gameState.trumpSuit = -1; // there is no trump suit at the beginning of each trick
            for (int i = 0; i < 4; i ++) { // four iterations so that each player plays one card
                int turn = gameState.turn;
    
                Card cardBeingPlayed = receiveCardToPlayFromClient(turn);
        
                //remove card being played from player's hand, put it in play
                gameState.players[turn].hand.remove(cardBeingPlayed);
                gameState.currentPlay.add(cardBeingPlayed);
        
                // next player's turn for this trick
                if (gameState.turn < 3) {
                    gameState.turn++;
                }
                else {
                    gameState.turn = 0;
                }
            }
    
            gameState.turn = determineWinnerOfTrick(gameState);
            giveTrickToWinner(gameState.turn);
        }

        //after all cards have been played, calculate each player's points based on the tricks they took
        givePlayersPoints(gameState);
        
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
