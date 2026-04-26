package com.jordanpaille.deckgame.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class PlayerWithScore extends Player implements Comparable<PlayerWithScore> {
    private final int score;
    public PlayerWithScore(long gameId, String userName, List<Card> cards, int score) {
        super(userName, cards);
        this.score = score;
        this.setGameId(gameId);
    }

    @Override
    public int compareTo(PlayerWithScore otherPlayer) {
        return Integer.compare(otherPlayer.score, this.score);
    }
}
