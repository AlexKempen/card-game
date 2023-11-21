package edu.utdallas.heartstohearts.gamenetwork;

import android.util.Log;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.utdallas.heartstohearts.game.Card;
import edu.utdallas.heartstohearts.game.PlayerState;
import edu.utdallas.heartstohearts.network.MessageListener;

public class MockClient implements MessageListener {
    GameClient client;

    public MockClient(InetAddress host){
        GameClient.createGameClientAsync(host, false, (c)->{
            Log.d("MockClient", "Game client created");
            client = c;
            c.addPlayerStateListener(MockClient.this);
            c.requestState();
        }, null);
    }

    @Override
    public void messageReceived(Object o) {
        PlayerState state = (PlayerState) o;
        int selectionLimit = state.getAction().getSelectionLimit();
        List<Card> selection = state.getHand().stream().filter((card)->card.isSelectable()).limit(selectionLimit).collect(Collectors.toList());

        Log.d("MockClient", "Game State received with action " + state.getAction().toString());

        if(selectionLimit == 1){
            client.playCard(selection);
        } else if(selectionLimit == 3){
            client.passCards(selection);
        }
    }
}
