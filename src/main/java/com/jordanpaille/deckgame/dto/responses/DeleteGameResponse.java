package com.jordanpaille.deckgame.dto.responses;

public record DeleteGameResponse(
        boolean success,
        long gameId
) {}
