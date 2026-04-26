package com.jordanpaille.deckgame.dto;

import com.jordanpaille.deckgame.exceptions.GameException;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class Player {
    private static final long NON_PLAYING_GAME_ID = -1L;

    private final String userName;
    private long gameId = NON_PLAYING_GAME_ID;
    private final List<Card> hand;

    // Constructor
    public Player(String userName) {
        this.userName = userName;
        hand = new ArrayList<>();
    }
    public Player(String userName, List<Card> hand) {
        this.userName = userName;
        this.hand = hand;
    }

    public void leaveGame() {
        if (gameId == NON_PLAYING_GAME_ID) {
            throw new GameException("Player with username " + userName + " was asked to leave the game, but wasn't playing");
        }
        gameId = NON_PLAYING_GAME_ID;
    }

    public boolean isCurrentlyPlaying() {
         return gameId != NON_PLAYING_GAME_ID;
    }

    public void addCardToHand(Card card) {
        hand.add(card);
    }
}
