package edu.utdallas.hearts2hearts;

import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable{
    private MSG_TYPE messageType;
    private Serializable objectToSend;

    public static enum MSG_TYPE{
        GAME_STATE,
        PASS_CARDS,
        PLAY_CARD
    }

    public Message (MSG_TYPE messageType, Serializable object){
        this.messageType = messageType;
        this.objectToSend = object;
    }

    public MSG_TYPE getMessageType(){
        return messageType;
    }

    public Serializable getObject(){
        return objectToSend;
    }

    public static void main(String[] args) {
        ArrayList<Card> cardList = new ArrayList<Card>();
        Message msg = new Message(MSG_TYPE.GAME_STATE, cardList);
    }
}



