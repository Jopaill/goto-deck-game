package com.jordanpaille.deckgame.dto.responses;

public record GetCountPerSuitResponse(
        boolean success,
        String errorMessage,
        int numberOfHearts,
        int numberOfSpades,
        int numberOfClubs,
        int numberOfDiamonds
) {
}