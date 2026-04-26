package com.jordanpaille.deckgame.dto.responses;


import com.jordanpaille.deckgame.dto.Game;

public record GetGameResponse(
        boolean success,
        Game game
) {}