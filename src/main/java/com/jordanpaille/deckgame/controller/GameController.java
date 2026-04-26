package com.jordanpaille.deckgame.controller;

import com.jordanpaille.deckgame.dto.Game;
import com.jordanpaille.deckgame.dto.responses.*;
import com.jordanpaille.deckgame.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

@RestController
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @Operation(summary = "Create a game")
    @PostMapping("/games")
    public Game createGame() {
        return gameService.createGame();
    }

    @Operation(summary = "Verify that Game with a gameId exists")
    @GetMapping("/games/{gameId}")
    public GetGameResponse getGame(@PathVariable long gameId) {
        return gameService.getGame(gameId);
    }

    @Operation(summary = "Delete a game")
    @DeleteMapping("/games/{gameId}")
    public DeleteGameResponse deleteGame(@PathVariable long gameId) {
        return gameService.deleteGame(gameId);
    }

    @Operation(summary = "Add a deck to a game")
    @PostMapping("/games/{gameId}/decks/{deckId}")
    public AddDeckToGameResponse addDeckToGame(@PathVariable long gameId, @PathVariable long deckId) {
        return gameService.addDeckToGame(gameId, deckId);
    }

    @Operation(summary = "Add a player to a game")
    @PostMapping("/games/{gameId}/players/{username}")
    public AddPlayerToGameResponse addPlayerToGame(@PathVariable long gameId, @PathVariable String username) {
        return gameService.addPlayerToGame(gameId, username);
    }

    @Operation(summary = "Remove a player from a game")
    @DeleteMapping("/games/{gameId}/players/{username}")
    public RemovePlayerFromGameResponse removePlayerFromGame(@PathVariable long gameId, @PathVariable String username) {
        return gameService.removePlayerFromGame(gameId, username);
    }

    @Operation(summary = "Deal one or more cards to a player")
    @PostMapping("/games/{gameId}/players/{username}/hand/cards")
    public DealCardResponse dealCard(
            @PathVariable long gameId,
            @PathVariable String username,
            @RequestParam(defaultValue = "1") int count
    ) {
        return gameService.dealCard(gameId, username, count);
    }

    @Operation(summary = "Get the list of players for a game", description = "Input is the 'gameId'. Output is the whole list of players in that game along with the total added value of all the cards each player holds; use face values of cards only. Then sort the list in descending order, from the player with the highest value hand to the player with the lowest value hand: ○ For instance if player ‘A’ holds a 10 + King then her total value is 23 and player ‘B’ holds a 7 + Queen then his total value is 19, so player ‘A’ will be listed first followed by player ‘B’.")
    @GetMapping("/games/{gameId}/players")
    public GetPlayersResponse getPlayers(@PathVariable long gameId) {
        return gameService.getPlayers(gameId);
    }

    @Operation(summary = "Count the number of cards left in game deck per suit")
    @GetMapping("/games/{gameId}/count")
    public GetCountPerSuitResponse getCountPerSuit(@PathVariable long gameId) {
        return gameService.getCountPerSuit(gameId);
    }

    @Operation(summary = "Shuffle the game deck")
    @PostMapping("/games/{gameId}/deck/shuffle")
    public ShuffleGameDeckResponse shuffleGameDeck(@PathVariable long gameId) {
        return gameService.shuffleGameDeck(gameId);
    }

    @Operation(
            summary = "Get count of each remaining card in the game deck",
            description = "Returns counts grouped by suit and rank, sorted by suit in the order hearts, spades, clubs, diamonds, then by rank from King down to Ace."
    )
    @GetMapping("/games/{gameId}/deck/cards/counts")
    public GetRemainingCardCountsResponse getRemainingCardCounts(@PathVariable long gameId) {
        return gameService.getRemainingCardCounts(gameId);
    }
}
