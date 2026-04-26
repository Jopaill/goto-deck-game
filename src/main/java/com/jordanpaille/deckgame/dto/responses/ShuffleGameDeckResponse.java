package com.jordanpaille.deckgame.dto.responses;

public record ShuffleGameDeckResponse(
        boolean success,
        String errorMessage,
        long gameId
) {
}