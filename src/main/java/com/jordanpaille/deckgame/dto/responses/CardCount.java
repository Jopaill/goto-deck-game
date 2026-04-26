package com.jordanpaille.deckgame.dto.responses;

import com.jordanpaille.deckgame.dto.Card;

public record CardCount(
        Card.Suit suit,
        Card.Rank rank,
        int count
) {}