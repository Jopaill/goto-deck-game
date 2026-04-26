package com.jordanpaille.deckgame.dao;

import com.jordanpaille.deckgame.dto.Game;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class GameDao {
    private final Map<Long, Game> games = new ConcurrentHashMap<>();

    public void addGame(Game game) {
        games.put(game.getGameId(), game);
    }

    public Game getGame(long gameId) {
        return games.get(gameId);
    }

    public Game deleteGame(long gameId) {
        return games.remove(gameId);
    }
}
