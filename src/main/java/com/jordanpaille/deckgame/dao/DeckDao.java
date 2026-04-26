package com.jordanpaille.deckgame.dao;

import com.jordanpaille.deckgame.dto.Deck;
import com.jordanpaille.deckgame.exceptions.GameException;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class DeckDao {
    private final List<Deck> decks = new ArrayList<>();

    public synchronized void addDeck(Deck deck) {
        decks.add(deck);
    }

    public Deck getDeckById(long deckId) {
        for (Deck deck : decks) {
            if (deck.getDeckId() == deckId) {
                return deck;
            }
        }
        return null;
    }

    public synchronized Deck deleteDeckByDeckId(long deckId) {
        for (Deck deck : decks) {
            if (deckId == deck.getDeckId()) {
                decks.remove(deck);
                return deck;
            }
        }
        return null;
    }

}
