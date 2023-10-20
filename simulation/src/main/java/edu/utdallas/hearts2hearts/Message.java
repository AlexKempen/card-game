package edu.utdallas.hearts2hearts;

import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable{
    private int messageType;
    private Serializable objectToSend;

    public Message (int messageType, Serializable object){
        this.messageType = messageType;
        this.objectToSend = object;
    }

    public static void main(String[] args) {
        ArrayList<Card> cardList = new ArrayList<Card>();
        Message msg = new Message(0, cardList);
    }
}



