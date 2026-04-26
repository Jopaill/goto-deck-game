package com.jordanpaille.deckgame.dto.responses;

public record AddDeckToGameResponse(
        boolean success,
        String errorMessage,
        long gameId,
        long deckId
) {}

