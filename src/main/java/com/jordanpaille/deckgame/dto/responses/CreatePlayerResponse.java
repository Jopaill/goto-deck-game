package com.jordanpaille.deckgame.dto.responses;

public record CreatePlayerResponse(
        boolean playerCreated,
        String errorMessage,
        String username
) {}
