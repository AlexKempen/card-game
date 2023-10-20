package edu.utdallas.hearts2hearts;

import java.net.*;
import java.util.ArrayList;
import java.io.*;

public class Client extends Thread {

    private String address;
    private int port;
    private int id;
    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    public boolean connectToServer(){
        try {
            socket = new Socket(address, port);
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

            this.id = (int) objectInputStream.readObject();
            //System.out.printf("(Client %d) Connected to server.\n", id);
        }
        catch(UnknownHostException u) {
            System.out.println(u);
            return false;
        }
        catch(IOException i) {
            System.out.println(i);
            return false;
        }
        catch(ClassNotFoundException c) {
            System.out.println(c);
            return false;
        }
        return true;
    }

    public void closeConnection(){
        try {
            objectInputStream.close();
            objectOutputStream.close();
            socket.close();
        }
        catch(IOException i){
            System.out.println(i);
        }
    }

    public GameState receiveGameStateFromServer(){
        GameState gameState = null;
        try { 
            gameState = (GameState) objectInputStream.readObject(); // block until server has sent object
            System.out.printf("(Client %d) Received GameState object\n", id);
        }
        catch(IOException i){
            System.out.println(i);
        }catch (ClassNotFoundException c){
            System.out.println(c);
        }
        return gameState;
    }

    public void sendGameStateToServer(GameState gameState){
        try{
            objectOutputStream.writeObject(gameState);
        }
        catch (IOException i) {
            System.out.println(i);
        }
    }

    public void passCards(){
        GameState gameState = receiveGameStateFromServer();
        // logic for passing around
        ArrayList<Card> cardsToPass = gameState.players[id].cardsToPlay;
        ArrayList<Card> hand = gameState.players[id].hand; 
        for (int i = 0; i < 3; i++)
            cardsToPass.add(hand.remove(0));
        sendGameStateToServer(gameState);
    }

    public void run(){

        boolean connectedToServer = connectToServer();
        if (!connectedToServer)
            return;
        

        passCards();
        closeConnection();
    }

    public Client (String address, int port) {
        this.address = address;
        this.port = port;
    }
}
