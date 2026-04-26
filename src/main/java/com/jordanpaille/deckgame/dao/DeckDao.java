package com.jordanpaille.deckgame.dao;

import com.jordanpaille.deckgame.dto.Deck;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class DeckDao {
    private final Map<Long, Deck> decks = new ConcurrentHashMap<>();

    public void addDeck(Deck deck) {
        decks.put(deck.getDeckId(), deck);
    }

    public Deck getDeckById(long deckId) {
        return decks.get(deckId);
    }

    public Deck deleteDeckByDeckId(long deckId) {
        return decks.remove(deckId);
    }
}
