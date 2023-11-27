package edu.utdallas.heartstohearts.game;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Card implements Serializable, Comparable<Card>, Cloneable {

    private static final long serialVersionUID = 6236789073934771400L;
    public static Card QUEEN_OF_SPADES = new Card(Suit.SPADES, Rank.QUEEN);
    public static Card TWO_OF_CLUBS = new Card(Suit.CLUBS, Rank.TWO);
    private Suit suit;
    private Rank rank;
    private boolean selectable;

    public Card(Suit suit, Rank rank, boolean selectable) {
        this.suit = suit;
        this.rank = rank;
        this.selectable = selectable;
    }

    public Card(Suit suit, Rank rank) {
        this(suit, rank, true);
    }

    /**
     * @param id : A card id, ranging from 0 to 51.
     */
    public Card(int id) {
        this(Suit.fromIndex(id / 13), Rank.fromIndex(id % 13), true);
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
        } else if (this.suit.equals(Suit.HEARTS)) {
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
     * Cards are sorted by suit, then rank.
     */
    @Override
    public int compareTo(Card card) {
        return Comparator.<Card>comparingInt(c -> c.getSuit().toIndex()).thenComparing(c -> c.getRank().toIndex()).compare(this, card);
    }

    @Override
    protected Card clone() {
        return new Card(this.suit, this.rank, this.selectable);
    }

    @Override
    public String toString() {
        return rank.toString() + " of " + suit.toString();
    }
}