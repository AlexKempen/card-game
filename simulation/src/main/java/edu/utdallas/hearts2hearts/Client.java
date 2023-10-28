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
                //System.out.printf("(Client %d) Received GameState object\n", id);
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

    /* Before a card is played, this function ensures it is a legal play based on the trump suit
     * 
     */
    private boolean cardIsLegal (GameState gameState, Card card, ArrayList<Card> hand) {
        // card is not being lead-> a suit is already trump
        // for card to be legal, it must be trump suit or player must not have any trump cards in their hand
        if (gameState.trumpSuit != -1) {
            if (card.getSuit() == gameState.trumpSuit) {
                return true;
            }
            else {
                boolean hasTrumpCard = false;
                for (int cardIndex = 0; cardIndex < hand.size(); cardIndex++) {
                    if (hand.get(cardIndex).getSuit() == gameState.trumpSuit) {
                        hasTrumpCard = true;
                    }
                }
                if (hasTrumpCard) {
                    return false;
                }
                else {
                    return true;
                }
            }
        }
        else { // card is being lead-> if it's a Heart, check if they're broken
            if (gameState.trickNumber == 0) {
                if (card.getSuit() != 0 || card.getRank() != 0) {
                    return false;
                }
            }
            if (card.getSuit() == 3) {
                if (gameState.areHeartsBroken == true) {
                    return true;
                }
                else {
                    return false;
                }
            }
            else { // not a Heart-> always a legal lead
                //gameState.trumpSuit = card.getSuit(); // set new trump suit
                return true;
            }
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
        if (gameState.turn != id) {
            return;
        }
        else {
            Card cardToPlay = new Card(0,0);
            int i = 0;
            cardToPlay = gameState.players[id].hand.get(i); //for now, player will play first legal card in their hand
            while (!cardIsLegal(gameState, cardToPlay, gameState.players[id].hand) && i < gameState.players[id].hand.size() - 1) {
                i++;
                cardToPlay = gameState.players[id].hand.get(i);
            }
            sendCardToPlayToServer(cardToPlay);
        }
        
    }

    private void playRound() {
        for (int trickNumber = 0; trickNumber < 13; trickNumber++) { // 13 tricks per round played
            for (int i = 0; i < 4; i ++) {
                playCard();
                
            }
        }
    }

    public void run() {

        boolean connectedToServer = connectToServer();
        if (!connectedToServer)
            return;
        
        passCards();
        playRound();
        
        closeConnection();
    }

    public Client (String address, int port) {
        this.address = address;
        this.port = port;
    }
}
