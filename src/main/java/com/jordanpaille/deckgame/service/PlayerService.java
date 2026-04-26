package com.jordanpaille.deckgame.service;

import com.jordanpaille.deckgame.dao.PlayerDao;
import com.jordanpaille.deckgame.dto.Player;
import com.jordanpaille.deckgame.dto.responses.CreatePlayerResponse;
import com.jordanpaille.deckgame.dto.responses.DeletePlayerResponse;
import com.jordanpaille.deckgame.dto.responses.GetPlayerResponse;
import com.jordanpaille.deckgame.exceptions.GameException;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {
    private final PlayerDao playerDao;

    public PlayerService(PlayerDao playerDao) {
        this.playerDao = playerDao;
    }


    public CreatePlayerResponse createPlayer(String username) {
        try {
            playerDao.addPlayer(new Player(username));
            return new CreatePlayerResponse(true, null, username);
        } catch (GameException e) {
            return new CreatePlayerResponse(false, e.getMessage(), username);
        }

    }

    public GetPlayerResponse getPlayer(String username) {
        Player player = playerDao.getPlayer(username);
        return new GetPlayerResponse(
                null != player,
                username,
                null != player ? player.getHand() : null);
    }

    public DeletePlayerResponse deletePlayer(String username) {
        return new DeletePlayerResponse(null != playerDao.deletePlayer(username), username);
    }
}
