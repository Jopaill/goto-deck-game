package com.jordanpaille.deckgame.dto.responses;

public record DeleteGameResponse(
        boolean gameSuccessfullyDeleted,
        long gameId
) {}
