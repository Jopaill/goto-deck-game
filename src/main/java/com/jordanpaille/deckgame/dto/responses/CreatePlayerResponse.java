package com.jordanpaille.deckgame.dto.responses;

public record CreatePlayerResponse(
        boolean success,
        String errorMessage,
        String username
) {}
