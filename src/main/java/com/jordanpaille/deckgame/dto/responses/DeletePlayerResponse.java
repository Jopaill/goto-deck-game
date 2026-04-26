package com.jordanpaille.deckgame.dto.responses;

public record DeletePlayerResponse(
    boolean success,
    String username
) {}
