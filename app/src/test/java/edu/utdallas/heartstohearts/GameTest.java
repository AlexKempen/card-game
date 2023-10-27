package edu.utdallas.heartstohearts;

import org.junit.Before;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import edu.utdallas.heartstohearts.game.Card;
import edu.utdallas.heartstohearts.game.GameManager;
import edu.utdallas.heartstohearts.game.GameManagerBuilder;
import edu.utdallas.heartstohearts.game.GameStateBuilder;
import edu.utdallas.heartstohearts.game.PassDirection;
import edu.utdallas.heartstohearts.game.PlayerAction;
import edu.utdallas.heartstohearts.game.PlayerBuilder;
import edu.utdallas.heartstohearts.game.Rank;
import edu.utdallas.heartstohearts.game.Suit;


public class GameTest {
    private List<List<Card>> hands;
    private PlayerBuilder playerBuilder;
    private GameStateBuilder stateBuilder;
    private GameManagerBuilder managerBuilder;

    @Before
    public void initialize() {
        playerBuilder = new PlayerBuilder();
        managerBuilder = new GameManagerBuilder(playerBuilder);
        stateBuilder = new GameStateBuilder();
        hands = Card.dealHands(Card.makeDeck());
    }

    @Test
    public void testSimpleDeal() {
        stateBuilder.setHandsAndActions(hands);
        GameManager manager = managerBuilder.make();
        manager.deal(hands);
        assertTrue(manager.shouldPass());
        assertEquals(stateBuilder.make(), manager.getGameStates());
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

            stateBuilder.setHandAndAction(i, hand);
            stateBuilder.setHandAndAction(oppositeIndex, oppositeHand);
        }
        managerBuilder.direction = PassDirection.ACROSS;
        GameManager manager = managerBuilder.make();
        manager.deal(hands);
        assertTrue(manager.shouldPass());
        manager.passCards(cardsToPass);
        assertEquals(stateBuilder.make(), manager.getGameStates());
    }

    @Test
    public void testPlayCard() {
        // Mark player 0 as current player
        PlayerAction.playCard(0, playerBuilder.actions);
        Card playedCard = hands.get(0).get(0);

        managerBuilder.heartsBroken = true; // Ensure test is deterministic
        GameManager manager = managerBuilder.make();
        manager.deal(hands);

        assertEquals(0, manager.shouldPlayCard());
        manager.playCard(playedCard);
        assertEquals(1, manager.shouldPlayCard());

        // Player 1 should be up
        PlayerAction.playCard(1, stateBuilder.actions);
        // The card should be gone from their hand
        stateBuilder.hands.get(0).remove(0);
        // And it should be in the trick
        stateBuilder.trick.add(playedCard);
        assertEquals(stateBuilder.make(), manager.getGameStates());
    }

    @Test
    public void testTakeTrick() {
        List<Card> trick = Arrays.asList(Card.QUEEN_OF_SPADES, Card.TWO_OF_CLUBS, new Card(Suit.HEARTS, Rank.QUEEN));
        Card playedCard = hands.get(0).get(0);
        PlayerAction.playCard(0, playerBuilder.actions);
        managerBuilder.currentTrick = trick;
        GameManager manager = managerBuilder.make();
        manager.playCard(playedCard);

        stateBuilder.hands.get(0).remove(0);
        assertEquals(stateBuilder.make(), manager.getGameStates());

        // Test score is actually added at the end
        manager.finishRound();
        assertEquals(14, manager.getGameStates().get(0).getPoints());
    }

    @Test
    public void testScoring() {
        List<Card> trick = Arrays.asList(Card.QUEEN_OF_SPADES, new Card(Suit.HEARTS, Rank.TWO), new Card(Suit.DIAMONDS, Rank.QUEEN));
        playerBuilder.tricks.set(0, trick);
        stateBuilder.points.set(0, 14);

        GameManager manager = managerBuilder.make();
        manager.finishRound();

        assertEquals(stateBuilder.make(), manager.getGameStates());
    }

    @Test
    public void testPassChange() {
        managerBuilder.direction = PassDirection.NONE;
        GameManager manager = managerBuilder.make();
        manager.finishRound();
        assertTrue(manager.shouldPass());
    }

    @Test
    public void testShootTheMoon() {
        List<Card> trick = Arrays.asList(Card.QUEEN_OF_SPADES);
        trick.addAll(Arrays.stream(Rank.values()).map(rank -> new Card(Suit.HEARTS, rank)).collect(Collectors.toList()));
        playerBuilder.tricks.set(0, trick);

        GameManager manager = managerBuilder.make();
        manager.finishRound();

        IntStream.range(1, 4).forEach(i -> stateBuilder.points.set(i, 26));
        assertEquals(stateBuilder.make(), manager.getGameStates());
    }

    @Test
    public void testGameEnd() {
        playerBuilder.points.set(0, 105);
        GameManager manager = managerBuilder.make();
        assertTrue(manager.finishRound());
        assertEquals(105, manager.getGameStates().get(0).getPoints());
    }
}
