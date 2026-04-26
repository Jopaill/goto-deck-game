package com.jordanpaille.deckgame.dto.responses;

public record AddDeckToGameResponse(
        boolean gameSuccessfullyDeleted,
        String errorMessage,
        long gameId,
        long deckId
) {}

