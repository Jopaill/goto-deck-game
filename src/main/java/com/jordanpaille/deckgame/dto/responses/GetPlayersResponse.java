package com.jordanpaille.deckgame.dto.responses;

import com.jordanpaille.deckgame.dto.PlayerWithScore;

import java.util.List;

public record GetPlayersResponse(
        boolean foundGame,
        String errorMessage,
        List<PlayerWithScore> playersWithScore
        ) {}
