package com.jordanpaille.deckgame.dto.responses;

public record GetCountPerSuitResponse(
        boolean gameFound,
        String errorMessage,
        int numberOfHearts,
        int numberOfDiamonds,
        int numberOfSpades,
        int numberOfClubs
) {
}
