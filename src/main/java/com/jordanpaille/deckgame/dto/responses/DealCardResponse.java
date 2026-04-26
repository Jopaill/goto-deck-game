package com.jordanpaille.deckgame.dto.responses;

public record DealCardResponse(
   boolean success,
   String errorMessage,
   long gameId,
   String username
) {}
