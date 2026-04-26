package com.jordanpaille.deckgame.dto.responses;

public record RemovePlayerFromGameResponse(
        boolean success,
        String errorMessage,
        long gameId,
        String username
) {}
