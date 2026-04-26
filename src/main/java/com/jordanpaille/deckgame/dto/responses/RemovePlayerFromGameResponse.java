package com.jordanpaille.deckgame.dto.responses;

public record RemovePlayerFromGameResponse(
        boolean removalCompleted,
        String errorMessage,
        long gameId,
        String username
) {}
