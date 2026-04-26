package com.jordanpaille.deckgame.dto.responses;

public record AddPlayerToGameResponse(
        boolean addPlayerToGameCompleted,
        String errorMessage,
        long gameId,
        String username
) {
}
