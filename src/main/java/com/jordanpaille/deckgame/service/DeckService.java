package com.jordanpaille.deckgame.service;

import com.jordanpaille.deckgame.dao.DeckDao;
import com.jordanpaille.deckgame.dto.Deck;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class DeckService {
    private final AtomicLong incrementalDeckId = new AtomicLong(0L);
    private final DeckDao deckDao;

    public DeckService(DeckDao deckDao) {
        this.deckDao = deckDao;
    }

    public Deck createDeck() {
        long newDeckId = incrementalDeckId.getAndIncrement();
        Deck deck = new Deck(newDeckId);
        deckDao.addDeck(deck);
        return deck;
    }
}
