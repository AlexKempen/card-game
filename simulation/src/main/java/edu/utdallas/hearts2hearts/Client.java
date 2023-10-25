package edu.utdallas.hearts2hearts;

import java.net.*;
import java.util.ArrayList;

import edu.utdallas.hearts2hearts.GameState.Direction;
import edu.utdallas.hearts2hearts.Message.MSG_TYPE;

import java.io.*;

public class Client extends Thread {

    private String address;
    private int port;
    private int id;
    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    private boolean connectToServer(){
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

    private void closeConnection(){
        try {
            objectInputStream.close();
            objectOutputStream.close();
            socket.close();
        }
        catch(IOException i){
            System.out.println(i);
        }
    }

    private GameState receiveGameStateFromServer(){
        GameState gameState = null;
        try { 
            Message msg = (Message) objectInputStream.readObject(); // block until server has sent object
            if (msg.getMessageType() == MSG_TYPE.GAME_STATE){
                gameState = (GameState) msg.getObject();
                System.out.printf("(Client %d) Received GameState object\n", id);
            }
        }
        catch(IOException i){
            System.out.println(i);
        }catch (ClassNotFoundException c){
            System.out.println(c);
        }
        return gameState;
    }

    private void sendCardsToPassToServer(ArrayList<Card> cards){
        try{
            Message msg = new Message(MSG_TYPE.PASS_CARDS, cards);
            objectOutputStream.writeObject(msg);
        }
        catch (IOException i) {
            System.out.println(i);
        }
    }

    private void sendCardToPlayToServer(Card card){
        try{
            Message msg = new Message(MSG_TYPE.PLAY_CARD, card);
            objectOutputStream.writeObject(msg);
        }
        catch (IOException i) {
            System.out.println(i);
        }
    }

    private void passCards(){
        GameState gameState = receiveGameStateFromServer();
        if (gameState.currentDirection == Direction.NONE)
            return;

        // logic for passing around
        ArrayList<Card> cardsToPass = new ArrayList<Card>();
        ArrayList<Card> hand = gameState.players[id].hand; 
        for (int i = 0; i < 3; i++)
            cardsToPass.add(hand.remove(0));

        sendCardsToPassToServer(cardsToPass);
    }

    private void playCard(){
        GameState gameState = receiveGameStateFromServer();
        ArrayList<Card> hand = gameState.players[id].hand;
        Card cardToPlay = new Card(0,0);
        cardToPlay = gameState.players[id].hand.get(0); //for now, player will play first card in their hand
        sendCardToPlayToServer(cardToPlay);
    }

    public void run(){

        boolean connectedToServer = connectToServer();
        if (!connectedToServer)
            return;
        

        passCards();
        playCard();
        closeConnection();
    }

    public Client (String address, int port) {
        this.address = address;
        this.port = port;
    }
}
