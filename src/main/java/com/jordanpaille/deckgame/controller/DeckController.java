package com.jordanpaille.deckgame.controller;

import com.jordanpaille.deckgame.dto.Deck;
import com.jordanpaille.deckgame.service.DeckService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeckController {
    private final DeckService deckService;
    public DeckController(DeckService deckService) {
        this.deckService = deckService;
    }

    @Operation(summary = "Create a new deck")
    @PostMapping("/decks")
    public Deck createDeck() {
        return deckService.createDeck();
    }
}
