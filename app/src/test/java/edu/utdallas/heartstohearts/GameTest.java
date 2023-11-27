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
import edu.utdallas.heartstohearts.game.GameManagerBuilder;
import edu.utdallas.heartstohearts.game.GamePhase;
import edu.utdallas.heartstohearts.game.PlayerStateBuilder;
import edu.utdallas.heartstohearts.game.ListUtils;
import edu.utdallas.heartstohearts.game.PassDirection;
import edu.utdallas.heartstohearts.game.PlayerAction;
import edu.utdallas.heartstohearts.game.PlayerBuilder;
import edu.utdallas.heartstohearts.game.PlayerState;
import edu.utdallas.heartstohearts.game.Rank;
import edu.utdallas.heartstohearts.game.Suit;


public class GameTest {
    private List<List<Card>> hands;
    private PlayerBuilder playerBuilder;
    private PlayerStateBuilder stateBuilder;
    private GameManagerBuilder managerBuilder;

    public void assertPlayerStateEqual(GameManager manager) {
        // Multiple lines to facilitate debugging
        List<PlayerState> expected = stateBuilder.build();
        List<PlayerState> actual = manager.getPlayerStates();
        assertEquals(expected, actual);
    }

    @Before
    public void initialize() {
        playerBuilder = new PlayerBuilder();
        managerBuilder = new GameManagerBuilder(playerBuilder);
        stateBuilder = new PlayerStateBuilder();
        hands = Card.dealHands(Card.makeDeck());
    }

    @Test
    public void testSimpleDeal() {
        stateBuilder.hands = hands;
        GameManager manager = managerBuilder.build();
        manager.deal(hands);
        assertEquals(GamePhase.PASS, manager.getGamePhase());
        assertPlayerStateEqual(manager);
    }


    @Test
    public void testPassingPhase() {
        List<List<Card>> cardsToPass = ListUtils.fourCopies(ArrayList::new);
        managerBuilder.direction = PassDirection.ACROSS;
        List<List<Card>> finalHands = new ArrayList<>();
        for (List<Card> hand : hands) {
            finalHands.add(new ArrayList<>(hand));
        }

        // pass cards directly across in the stateBuilder to compare to the manager
        for (int i = 0; i < 2; i += 1) {
            int oppositeIndex = PassDirection.ACROSS.getPassId(i);
            List<Card> hand = finalHands.get(i);
            List<Card> oppositeHand = finalHands.get(oppositeIndex);

            // Slice cards from each hand
            cardsToPass.set(i, ListUtils.slice(hand, 0, 3));
            cardsToPass.set(oppositeIndex, ListUtils.slice(oppositeHand, 0, 3));

            // Add cards to opposite hand
            hand.addAll(cardsToPass.get(oppositeIndex));
            oppositeHand.addAll(cardsToPass.get(i));

            stateBuilder.setHandForPlay(i, hand);
            stateBuilder.setHandForPlay(oppositeIndex, oppositeHand);
        }

        // reset hands and deal them to manager so starting hands are the same as they were for stateBuilder
        GameManager manager = managerBuilder.build();
        manager.deal(hands);
        assertEquals(GamePhase.PASS, manager.getGamePhase());

        manager.passCards(cardsToPass);

        assertPlayerStateEqual(manager);
    }

    @Test
    public void testPlayCard() {
        // Mark player 0 as current player
        Card playedCard = new Card(Suit.SPADES, Rank.SEVEN);
        Card extraCard = new Card(Suit.CLUBS, Rank.QUEEN, false);
        playerBuilder.hands.get(0).addAll(Arrays.asList(playedCard, extraCard));

        managerBuilder.currentPlayerId = 0;
        managerBuilder.phase = GamePhase.PLAY;
        GameManager manager = managerBuilder.build();

        assertEquals(GamePhase.PLAY, manager.playCard(playedCard));
        assertEquals(1, manager.getCurrentPlayerId());

        // Player 1 should be up
        PlayerAction.setToPlayCard(1, stateBuilder.actions);
        stateBuilder.trick.add(playedCard);
        stateBuilder.hands.get(0).add(extraCard);

        assertPlayerStateEqual(manager);
    }

    @Test
    public void testTakeTrick() {
        // Player 1 set the trump suit as Queen of Hearts
        managerBuilder.currentTrick.put(new Card(Suit.HEARTS, Rank.QUEEN), 1);
        managerBuilder.currentTrick.put(Card.QUEEN_OF_SPADES, 0);
        managerBuilder.currentTrick.put(Card.TWO_OF_CLUBS, 0);
        Card playedCard = new Card(Suit.HEARTS, Rank.SEVEN);
        Card extraCard = new Card(Suit.CLUBS, Rank.QUEEN, false);
        playerBuilder.hands.get(0).add(playedCard);
        playerBuilder.hands.get(0).add(extraCard);

        managerBuilder.phase = GamePhase.PLAY;
        managerBuilder.heartsBroken = true;
        managerBuilder.currentPlayerId = 0;

        GameManager manager = managerBuilder.build();

        assertEquals(0, manager.getCurrentPlayerId());
        assertEquals(GamePhase.TRICK_FINISHED, manager.playCard(playedCard));

        assertEquals(GamePhase.PLAY, manager.finishTrick());
        assertEquals(1, manager.getCurrentPlayerId());

        // Player 1 won the trick, so they should be set to play (lead) the next card
        PlayerAction.setToPlayCard(1, stateBuilder.actions);
        stateBuilder.hands.get(0).add(extraCard);

        assertPlayerStateEqual(manager);
    }

    @Test
    public void testScoring() {
        // Create a mutable list
        List<Card> trick = new ArrayList<>(Arrays.asList(Card.QUEEN_OF_SPADES, Card.TWO_OF_CLUBS, new Card(Suit.HEARTS, Rank.QUEEN)));
        playerBuilder.tricks.set(0, trick);
        managerBuilder.phase = GamePhase.ROUND_FINISHED;
        GameManager manager = managerBuilder.build();

        manager.finishRound();
        stateBuilder.setWait();
        stateBuilder.points.set(0, 14);
        assertPlayerStateEqual(manager);
    }

    @Test
    public void testPassChange() {
        managerBuilder.direction = PassDirection.ACROSS;
        managerBuilder.phase = GamePhase.ROUND_FINISHED;
        GameManager manager = managerBuilder.build();

        assertEquals(GamePhase.DEAL, manager.finishRound());
        assertEquals(GamePhase.PLAY, manager.deal());
    }

    @Test
    public void testShootTheMoon() {
        List<Card> trick = new ArrayList<>(Collections.singletonList(Card.QUEEN_OF_SPADES));
        trick.addAll(Arrays.stream(Rank.values()).map(rank -> new Card(Suit.HEARTS, rank)).collect(Collectors.toList()));
        playerBuilder.tricks.set(0, trick);
        managerBuilder.phase = GamePhase.ROUND_FINISHED;
        GameManager manager = managerBuilder.build();
        manager.finishRound();

        stateBuilder.points = ListUtils.fourCopies(() -> 26);
        stateBuilder.points.set(0, 0);
        stateBuilder.setWait();
        assertPlayerStateEqual(manager);
    }

    @Test
    public void testGameEnd() {
        playerBuilder.points.set(0, 105);
        managerBuilder.phase = GamePhase.ROUND_FINISHED;
        GameManager manager = managerBuilder.build();
        assertEquals(GamePhase.COMPLETE, manager.finishRound());
        assertEquals(105, manager.getPlayerStates().get(0).getPoints());
    }
}
