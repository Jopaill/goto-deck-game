package com.jordanpaille.deckgame.dto.responses;

public record DeletePlayerResponse(
    boolean playerDeleted,
    String username
) {}
