package com.jordanpaille.deckgame.dao;

import com.jordanpaille.deckgame.dto.Player;
import com.jordanpaille.deckgame.exceptions.GameException;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PlayerDao {
    List<Player> players = new ArrayList<>();
    
    public synchronized void addPlayer(Player player) {
        for (Player p : players) {
            if (player.getUserName().equals(p.getUserName())) {
                throw new GameException("Player with this username already exists");
            }
        }
        
        players.add(player);
    }

    public Player getPlayer(String username) {
        for (Player p : players) {
            if (username.equals(p.getUserName())) {
                return p;
            }
        }
        return null;
    }

    public synchronized Player deletePlayer(String username) {
        for (Player p : players) {
            if (username.equals(p.getUserName())) {
                players.remove(p);
                return p;
            }
        }
        return null;
    }
}
