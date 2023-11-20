package edu.utdallas.heartstohearts.game;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Card implements Serializable, Comparable<Card>, Cloneable {
    public static Card QUEEN_OF_SPADES = new Card(Suit.SPADES, Rank.QUEEN);
    public static Card TWO_OF_CLUBS = new Card(Suit.CLUBS, Rank.TWO);
    private Suit suit;
    private Rank rank;
    private boolean selectable;
    private boolean playable;

    public Card(Suit suit, Rank rank, boolean playable) {
        this.suit = suit;
        this.rank = rank;
        this.playable = playable;
    }

    public Card(Suit suit, Rank rank) {
        this(suit, rank, true);
    }

    /**
     * @param id : A card id, ranging from 0 to 51.
     */
    public Card(int id) {
        this(Suit.fromInt(id / 13), Rank.fromInt(id % 13), true);
    }

    public boolean isSelectable() {
        return selectable;
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    /**
     * Returns the point value of the card.
     */
    public int getPoints() {
        if (this.equals(QUEEN_OF_SPADES)) {
            return 13;
        } else if (this.suit == Suit.HEARTS) {
            return 1;
        }
        return 0;
    }

    /**
     * Returns a shuffled deck of 52 cards.
     */
    public static List<Card> makeDeck() {
        List<Card> deck = IntStream.range(0, 52).mapToObj(Card::new).collect(Collectors.toList());
        Collections.shuffle(deck);
        return deck;
    }

    /**
     * Divides a deck of cards into four hands equally.
     * The deck is assumed to be 52 cards.
     */
    public static List<List<Card>> dealHands(List<Card> deck) {
        List<List<Card>> hands = new ArrayList<>();
        for (int i = 0; i < 4; ++i) {
            hands.add(new ArrayList<>(deck.subList(i * 13, (i + 1) * 13)));
        }
        return hands;
    }

    /**
     * Note equality for cards does not depend on selectable.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return suit == card.suit && rank == card.rank;
    }

    @Override
    public int hashCode() {
        return Objects.hash(suit, rank);
    }

    /**
     * Cards are sorted by rank to facilitate UI display.
     */
    @Override
    public int compareTo(Card card) {
        return this.rank.toInt() - card.rank.toInt();
    }

    @Override
    protected Card clone() {
        return new Card(this.suit, this.rank);
    }
}