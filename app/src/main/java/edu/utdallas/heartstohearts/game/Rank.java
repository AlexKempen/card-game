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

public enum Rank implements Serializable {
    TWO(0, "2"), THREE(1, "3"), FOUR(2, "4"), FIVE(3, "5"), SIX(4, "6"), SEVEN(5, "7"), EIGHT(6, "8"), NINE(7, "9"), TEN(8, "10"), JACK(9, "J"), QUEEN(10, "Q"), KING(11, "K"), ACE(12, "A");

    private static final long serialVersionUID = 4630732092238719595L;

    private final int index;
    private final String name;

    Rank(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public int toIndex() {
        return this.index;
    }

    public static Rank fromIndex(int index) {
        return new Rank[]{TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE}[index];
    }

    public String toString() {
        return this.name;
    }
}