package edu.utdallas.hearts2hearts;

import java.net.*;
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

    public void run(){

        boolean connectedToServer = connectToServer();
        if (!connectedToServer)
            return;

        try { 
            GameState gameState = (GameState) objectInputStream.readObject();
            System.out.printf("(Client %d) Received GameState object\n", id);
            String handString = String.format("(Client %d) Hand: ", id);
            for (int i = 0; i < 13; i++)
                handString += gameState.players[id].hand.get(i).toString() + ", ";
            System.out.println(handString);
        }
        catch(IOException i){
            System.out.println(i);
        }catch (ClassNotFoundException c){
            System.out.println(c);
        }

        closeConnection();
    }

    public Client (String address, int port) {
        this.address = address;
        this.port = port;
    }
}
