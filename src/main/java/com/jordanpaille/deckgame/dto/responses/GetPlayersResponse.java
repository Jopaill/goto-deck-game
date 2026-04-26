package com.jordanpaille.deckgame.dto.responses;

import com.jordanpaille.deckgame.dto.PlayerWithScore;

import java.util.List;

public record GetPlayersResponse(
        boolean success,
        String errorMessage,
        List<PlayerWithScore> playersWithScore
        ) {}
