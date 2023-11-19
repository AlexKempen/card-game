package edu.utdallas.heartstohearts;

import org.junit.Before;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import edu.utdallas.heartstohearts.game.Card;
import edu.utdallas.heartstohearts.game.GameManager;
import edu.utdallas.heartstohearts.game.GameManagerBuilder;
import edu.utdallas.heartstohearts.game.GameStateBuilder;
import edu.utdallas.heartstohearts.game.ListUtils;
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

    /**
     * PASSES
     */
    @Test
    public void testSimpleDeal() {
        stateBuilder.hands = hands;
        GameManager manager = managerBuilder.make();
        manager.deal(hands);
        assertTrue(manager.shouldPass());
        assertEquals(stateBuilder.make(), manager.getGameStates());
    }


    /**
     * PASSES
     */
    @Test
    public void testPassingPhase() {
        List<List<Card>> cardsToPass = ListUtils.fourCopies(ArrayList::new);
        managerBuilder.direction = PassDirection.ACROSS;


        // pass cards directly across in the stateBuilder to compare to the manager
        for (int i = 0; i < 2; i += 1) {
            int oppositeIndex = PassDirection.ACROSS.mapPassIndex(i);
            List<Card> hand = hands.get(i);
            List<Card> oppositeHand = hands.get(oppositeIndex);
            cardsToPass.set(i, new ArrayList<>(hand.subList(0, 3)));
            cardsToPass.set(oppositeIndex, new ArrayList<>(oppositeHand.subList(0, 3)));


            hand.addAll(cardsToPass.get(oppositeIndex));
            oppositeHand.addAll(cardsToPass.get(i));
            hand.removeAll(cardsToPass.get(i));
            oppositeHand.removeAll(cardsToPass.get(oppositeIndex));

            stateBuilder.setHandAndAction(i, hand);
            stateBuilder.setHandAndAction(oppositeIndex, oppositeHand);
        }

        // reset hands and deal them to manager so starting hands are the same as they were for stateBuilder
        GameManager manager = managerBuilder.make();
        manager.deal(hands);

        for (int i = 0; i < 4; i++) {
            for (Card c : cardsToPass.get(i)) {
                assertTrue(c.isSelectable());
            }
        }

        assertTrue(manager.shouldPass());
        manager.passCards(cardsToPass);

        assertEquals(stateBuilder.make(), manager.getGameStates());
    }

    /**
     * PASSES
     */
    @Test
    public void testPlayCard() {
        // Mark player 0 as current player
        PlayerAction.setToPlayCard(0, playerBuilder.actions);
        //hands.get(0).set(0, new Card(Suit.SPADES, Rank.SEVEN));
        Card playedCard = new Card(Suit.SPADES, Rank.SEVEN);

        managerBuilder.heartsBroken = true; // Ensure test is deterministic
        Suit trumpSuit = playedCard.getSuit(); // Ensures card is definitely playable
        GameManager manager = managerBuilder.make();
        manager.deal(hands);

        // mark player 0 as should play a card and all others as wait, this skips passing phase for this test
        manager.getPlayers().get(0).setAction(PlayerAction.PLAY_CARD, trumpSuit, true);
        manager.getPlayers().get(1).setAction(PlayerAction.WAIT, trumpSuit, true);
        manager.getPlayers().get(2).setAction(PlayerAction.WAIT, trumpSuit, true);
        manager.getPlayers().get(3).setAction(PlayerAction.WAIT, trumpSuit, true);

        assertEquals(0, manager.shouldPlayCard());
        manager.playCard(playedCard);
        assertEquals(1, manager.shouldPlayCard());

        // Player 1 should be up
        PlayerAction.setToPlayCard(1, stateBuilder.actions);
        // The card should be gone from their hand
        stateBuilder.hands = hands;
        stateBuilder.hands.get(0).remove(0);
        // And it should be in the trick
        stateBuilder.trick.add(playedCard);
        assertEquals(stateBuilder.make(), manager.getGameStates());
    }

    /**
     * PASSES
     */
    @Test
    public void testTakeTrick() {
        List<Card> trick = Arrays.asList(Card.QUEEN_OF_SPADES, Card.TWO_OF_CLUBS, new Card(Suit.HEARTS, Rank.QUEEN));
        Card playedCard = new Card(Suit.SPADES, Rank.SEVEN);
        Suit trumpSuit = Suit.SPADES;
        PlayerAction.setToPlayCard(0, playerBuilder.actions);

        managerBuilder.heartsBroken = true;
        managerBuilder.trumpSuit = Suit.SPADES;
        managerBuilder.currentTrick.addAll(trick);
        GameManager manager = managerBuilder.make();
        manager.deal(hands);

        // mark player 0 as should play a card and all others as wait, this skips passing phase for this test
        manager.getPlayers().get(0).setAction(PlayerAction.PLAY_CARD, trumpSuit, true);
        manager.getPlayers().get(1).setAction(PlayerAction.WAIT, trumpSuit, true);
        manager.getPlayers().get(2).setAction(PlayerAction.WAIT, trumpSuit, true);
        manager.getPlayers().get(3).setAction(PlayerAction.WAIT, trumpSuit, true);


        assertEquals(0, manager.shouldPlayCard());
        manager.playCard(playedCard);
        assertEquals(1, manager.shouldPlayCard());

        for (Card c : manager.getPlayers().get(0).getHand()) {
            assertFalse(c.isSelectable());
        }

        for (Card c : manager.getPlayers().get(2).getHand()) {
            assertFalse(c.isSelectable());
        }
        for (Card c : manager.getPlayers().get(3).getHand()) {
            assertFalse(c.isSelectable());
        }

        // Player 1 won the trick, so they should be set to play (lead) the next card
        PlayerAction.setToPlayCard(1, stateBuilder.actions);

        stateBuilder.hands = hands;
        stateBuilder.hands.get(0).remove(0);

        assertEquals(stateBuilder.make(), manager.getGameStates());

        // Test score is actually added at the end
        manager.finishRound();

        assertEquals(14, manager.getGameStates().get(1).getPoints());
    }

    /**
     * PASSES
     */
    @Test
    public void testScoring() {
        List<Card> trick = Arrays.asList(Card.QUEEN_OF_SPADES, new Card(Suit.HEARTS, Rank.TWO), new Card(Suit.DIAMONDS, Rank.QUEEN));
        //playerBuilder.tricks.set(0, trick);
        managerBuilder.currentTrick.addAll(trick);
        stateBuilder.points.set(0, 14);
        stateBuilder.hands = hands;
        stateBuilder.trick = trick;

        GameManager manager = managerBuilder.make();
        manager.deal(hands);

        manager.finishRound();

        assertEquals(stateBuilder.make(), manager.getGameStates());
    }

    /**
     * PASSES
     */
    @Test
    public void testPassChange() {
        managerBuilder.direction = PassDirection.NONE;
        GameManager manager = managerBuilder.make();
        manager.finishRound();
        assertTrue(manager.shouldPass());
        assertEquals(manager.getDirection(), PassDirection.LEFT);

        managerBuilder.direction = PassDirection.ACROSS;
        manager = managerBuilder.make();
        manager.finishRound();
        assertFalse(manager.shouldPass());
        assertEquals(manager.getDirection(), PassDirection.NONE);

        managerBuilder.direction = PassDirection.LEFT;
        manager = managerBuilder.make();
        manager.finishRound();
        assertTrue(manager.shouldPass());
        assertEquals(manager.getDirection(), PassDirection.RIGHT);

        managerBuilder.direction = PassDirection.RIGHT;
        manager = managerBuilder.make();
        manager.finishRound();
        assertTrue(manager.shouldPass());
        assertEquals(manager.getDirection(), PassDirection.ACROSS);
    }

    /**
     * PASSES
     */
    @Test
    public void testShootTheMoon() {
        List<Card> trick = new ArrayList<>(Arrays.asList(Card.QUEEN_OF_SPADES));
        trick.addAll(Arrays.stream(Rank.values()).map(rank -> new Card(Suit.HEARTS, rank)).collect(Collectors.toList()));
        playerBuilder.tricks.set(0, trick);
        GameManager manager = managerBuilder.make();
        manager.finishRound();

        IntStream.range(1, 4).forEach(i -> stateBuilder.points.set(i, 26));
        assertEquals(0, manager.getGameStates().get(0).getPoints());
        assertEquals(26, manager.getGameStates().get(1).getPoints());
        assertEquals(stateBuilder.make(), manager.getGameStates());
    }

    /**
     * PASSES
     */
    @Test
    public void testGameEnd() {
        playerBuilder.points.set(0, 105);
        GameManager manager = managerBuilder.make();
        assertTrue(manager.finishRound());
        assertEquals(105, manager.getGameStates().get(0).getPoints());
    }
}
