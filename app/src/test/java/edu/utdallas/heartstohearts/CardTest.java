/**
 * Hearts to Hearts project
 * Senior design project, University of Texas at Dallas CS 4485.0W1
 * Fall 2023
 * <p>
 * File authors:
 * - Alex Kempen
 */
package edu.utdallas.heartstohearts;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.Test;

import java.util.List;

import edu.utdallas.heartstohearts.game.Card;

public class CardTest {
    @Test
    public void testDeal() {
        List<Card> deck = Card.makeDeck();
        assertEquals(52, deck.size());
        List<List<Card>> hands = Card.dealHands(deck);
        assertEquals(4, hands.size());
        for (List<Card> hand : hands) {
            assertEquals(13, hand.size());
        }
    }
}
