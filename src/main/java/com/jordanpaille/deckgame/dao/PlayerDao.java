package com.jordanpaille.deckgame.dao;

import com.jordanpaille.deckgame.dto.Player;
import com.jordanpaille.deckgame.exceptions.GameException;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class PlayerDao {
    private final Map<String, Player> players = new ConcurrentHashMap<>();

    public void addPlayer(Player player) {
        Player existingPlayer = players.putIfAbsent(player.getUserName(), player);
        if (existingPlayer != null) {
            throw new GameException("Player with this username already exists");
        }
    }

    public Player getPlayer(String username) {
        return players.get(username);
    }

    public Player deletePlayer(String username) {
        return players.remove(username);
    }
}
