package edu.utdallas.heartstohearts;

import org.junit.Before;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import edu.utdallas.heartstohearts.game.Card;
import edu.utdallas.heartstohearts.game.GameManager;
import edu.utdallas.heartstohearts.game.GameState;
import edu.utdallas.heartstohearts.game.GameStateBuilder;
import edu.utdallas.heartstohearts.game.PassDirection;
import edu.utdallas.heartstohearts.game.PlayerBuilder;
import edu.utdallas.heartstohearts.game.Rank;
import edu.utdallas.heartstohearts.game.Suit;


public class GameTest {
    private List<List<Card>> hands;
    private PlayerBuilder playerBuilder;
    private List<GameStateBuilder> stateBuilders;

    @Before
    public void initialize() {
        playerBuilder = new PlayerBuilder();
        stateBuilders = Collections.nCopies(4, new GameStateBuilder());
        hands = Card.dealHands(Card.makeDeck());
    }

    @Test
    public void testSimpleDeal() {
        for (int i = 0; i < 4; i++) {
            stateBuilders.get(i).setHandAndAction(hands.get(i));
        }
        GameManager manager = new GameManager(playerBuilder.make(), PassDirection.NONE);
        manager.deal(hands);
        assertFalse(manager.shouldPass());
        assertEquals(GameStateBuilder.makeAll(stateBuilders), manager.getGameStates());
    }

    @Test
    public void testPassingPhase() {
        List<List<Card>> cardsToPass = Collections.nCopies(4, new ArrayList<>());
        for (int i = 0; i < 2; i += 1) {
            int oppositeIndex = PassDirection.ACROSS.mapPassIndex(i);
            List<Card> hand = hands.get(i);
            List<Card> oppositeHand = hands.get(oppositeIndex);

            cardsToPass.set(i, new ArrayList<>(hand.subList(0, 3)));
            cardsToPass.set(oppositeIndex, new ArrayList<>(oppositeHand.subList(0, 3)));

            hand.removeAll(cardsToPass.get(i));
            oppositeHand.removeAll(cardsToPass.get(oppositeIndex));

            stateBuilders.get(i).setHandAndAction(hand);
            stateBuilders.get(oppositeIndex).setHandAndAction(oppositeHand);
        }
        GameManager manager = new GameManager(playerBuilder.make(), PassDirection.ACROSS);
        manager.deal(hands);
        assertTrue(manager.shouldPass());
        manager.passCards(cardsToPass);
        assertEquals(GameStateBuilder.makeAll(stateBuilders), manager.getGameStates());
    }

    @Test
    public void testPlayCard() {
        // Test situation where a player plays a card

    }

    @Test
    public void testTakeTrick() {
        // Test situation where a player takes a trick after a card is played
    }

    @Test
    public void testScoring() {
        List<Card> trick = Arrays.asList(Card.QUEEN_OF_SPADES, new Card(Suit.HEARTS, Rank.TWO), new Card(Suit.DIAMONDS, Rank.QUEEN));
        playerBuilder.tricks.set(0, trick);

        GameManager manager = new GameManager(playerBuilder.make(), PassDirection.NONE);
        manager.finishRound();

        stateBuilders.get(0).score = 14;
        assertEquals(GameStateBuilder.makeAll(stateBuilders), manager.getGameStates());
        assertTrue(manager.shouldPass());
    }

    @Test
    public void testShootTheMoon() {
        List<Card> trick = Arrays.asList(Card.QUEEN_OF_SPADES);
        trick.addAll(Arrays.stream(Rank.values()).map(rank -> new Card(Suit.HEARTS, rank)).collect(Collectors.toList()));
        playerBuilder.tricks.set(0, trick);

        GameManager manager = new GameManager(playerBuilder.make(), PassDirection.NONE);
        manager.finishRound();

        for (int i = 1; i < 3; ++i) {
            stateBuilders.get(i).score = 26;
        }
        assertEquals(GameStateBuilder.makeAll(stateBuilders), manager.getGameStates());
    }

    @Test
    public void testGameEnd() {
        playerBuilder.points.set(0, 105);
        GameManager manager = new GameManager(playerBuilder.make(), PassDirection.ACROSS);
        assertTrue(manager.finishRound());
        assertEquals(105, manager.getGameStates().get(0).getScore());
    }
}
