package com.jordanpaille.deckgame.dao;

import com.jordanpaille.deckgame.dto.Game;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class GameDao {
    private final List<Game> games = new ArrayList<>();

    public void addGame(Game game) {
        games.add(game);
    }

    public Game getGame(long gameId) {
        for (Game game : games) {
            if (gameId == game.getGameId()) {
                return game;
            }
        }

        return null;
    }

    public Game deleteGame(long gameId) {
        for (Game game : games) {
            if (gameId == game.getGameId()) {
                games.remove(game);
                return game;
            }
        }
        return null;
    }
}
