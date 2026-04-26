package com.jordanpaille.deckgame.dto.responses;

import java.util.List;

public record GetRemainingCardCountsResponse(
        boolean success,
        String errorMessage,
        List<CardCount> cardCounts
) {
}