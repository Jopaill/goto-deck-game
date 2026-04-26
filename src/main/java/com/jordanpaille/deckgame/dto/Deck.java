package com.jordanpaille.deckgame.dto;

import com.jordanpaille.deckgame.utils.ShuffleUtils;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Deck {

    private final long deckId;
    List<Card> cardDeck = new ArrayList<>();

    public Deck(long deckId) {
        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Rank rank : Card.Rank.values()) {
                cardDeck.add(new Card(rank, suit));
            }
        }
        ShuffleUtils.shuffle(cardDeck);

        this.deckId = deckId;
    }
}
