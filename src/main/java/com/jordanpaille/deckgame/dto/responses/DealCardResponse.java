package com.jordanpaille.deckgame.dto.responses;

public record DealCardResponse(
   boolean cardDealtSuccessfully,
   String errorMessage,
   long gameId,
   String username
) {}
