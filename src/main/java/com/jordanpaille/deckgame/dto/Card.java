package com.jordanpaille.deckgame.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

public record Card(Rank rank, Suit suit) {
    public enum Suit {
        HEARTS, DIAMONDS, SPADES, CLUBS
    }

    @Getter
    public enum Rank {
        ACE(1),
        TWO(2),
        THREE(3),
        FOUR(4),
        FIVE(5),
        SIX(6),
        SEVEN(7),
        EIGHT(8),
        NINE(9),
        TEN(10),
        JACK(11),
        QUEEN(12),
        KING(13);

        private final int value;

        Rank(int value) {
            this.value = value;
        }
    }

    @JsonIgnore
    public int getRankValue() {
        return rank.getValue();
    }

    @Override
    public String toString() {
        return rank + " of " + suit;
    }
}
