package com.jordanpaille.deckgame.dto.responses;

public record AddPlayerToGameResponse(
        boolean success,
        String errorMessage,
        long gameId,
        String username
) {
}
