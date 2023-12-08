/**
 * Hearts to Hearts project
 * Senior design project, University of Texas at Dallas CS 4485.0W1
 * Fall 2023
 * <p>
 * File authors:
 * - Alex Kempen
 */
package edu.utdallas.heartstohearts.game;

import java.io.Serializable;

public enum Suit implements Serializable {

    HEARTS(0, "Hearts"), DIAMONDS(1, "Diamonds"), CLUBS(2, "Clubs"), SPADES(3, "Spades");

    private static final long serialVersionUID = 7865183916468303480L;

    private final int index;
    private final String name;

    Suit(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public int toIndex() {
        return this.index;
    }

    public static Suit fromIndex(int index) {
        return new Suit[]{HEARTS, DIAMONDS, CLUBS, SPADES}[index];
    }

    @Override
    public String toString() {
        return this.name;
    }
}