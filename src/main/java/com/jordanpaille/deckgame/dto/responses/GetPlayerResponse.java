package com.jordanpaille.deckgame.dto.responses;

import com.jordanpaille.deckgame.dto.Card;

import java.util.List;

public record GetPlayerResponse(
        boolean success,
        String username,
        List<Card> hand
        ) {}
