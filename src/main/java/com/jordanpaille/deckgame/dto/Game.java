package com.jordanpaille.deckgame.dto;


import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Game {

    private final long gameId;
    private final List<Card> cards = new ArrayList<>();
    private final List<Player> players = new ArrayList<>();

    // Constructor
    public Game(long gameId) {
        this.gameId = gameId;
    }

    public void addCards(List<Card> cards) {
        this.cards.addAll(cards);
    }

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    // Return true if the element was removed
    // Return false if the element wasn't removed
    public boolean removePlayer(Player player) {
        return this.players.remove(player);
    }
}
