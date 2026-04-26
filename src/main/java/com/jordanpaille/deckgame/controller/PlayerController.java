package com.jordanpaille.deckgame.controller;

import com.jordanpaille.deckgame.dto.responses.CreatePlayerResponse;
import com.jordanpaille.deckgame.dto.responses.DeletePlayerResponse;
import com.jordanpaille.deckgame.dto.responses.GetPlayerResponse;
import com.jordanpaille.deckgame.service.PlayerService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

@RestController
public class PlayerController {
    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @Operation(summary = "Register a new player")
    @PostMapping("/players/{username}")
    public CreatePlayerResponse createPlayer(@PathVariable String username) {
        return playerService.createPlayer(username);
    }

    @Operation(summary = "Get a player by username")
    @GetMapping("/players/{username}")
    public GetPlayerResponse getPlayer(@PathVariable String username) {
        return playerService.getPlayer(username);
    }

    @Operation(summary = "Delete a player by username")
    @DeleteMapping("/players/{username}")
    public DeletePlayerResponse deletePlayer(@PathVariable String username) {
        return playerService.deletePlayer(username);
    }
}
