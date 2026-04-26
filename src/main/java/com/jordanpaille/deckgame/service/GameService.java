package com.jordanpaille.deckgame.service;

import com.jordanpaille.deckgame.dao.DeckDao;
import com.jordanpaille.deckgame.dao.GameDao;
import com.jordanpaille.deckgame.dao.PlayerDao;
import com.jordanpaille.deckgame.dto.*;
import com.jordanpaille.deckgame.dto.responses.*;
import com.jordanpaille.deckgame.utils.ShuffleUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class GameService {

    // Fields
    private final AtomicLong incrementalGameID = new AtomicLong(0L);

    private final GameDao gameDao;
    private final DeckDao deckDao;
    private final PlayerDao playerDao;

    // Constructor
    public GameService(GameDao gameDao, DeckDao deckDao, PlayerDao playerDao) {
        this.gameDao = gameDao;
        this.deckDao = deckDao;
        this.playerDao = playerDao;
    }


    public Game createGame() {
        long newGameId = incrementalGameID.getAndIncrement();
        Game game = new Game(newGameId);
        gameDao.addGame(game);
        return game;
    }

    public GetGameResponse getGame(long gameId) {
        Game game = gameDao.getGame(gameId);
        return new GetGameResponse(
                null != game,
                game
        );
    }

    public DeleteGameResponse deleteGame(long gameId) {
        return new DeleteGameResponse(
                null != gameDao.deleteGame(gameId),
                gameId);
    }

    public AddDeckToGameResponse addDeckToGame(long gameId, long deckId) {
        Game game = gameDao.getGame(gameId);
        if (null == game) {
            return new AddDeckToGameResponse(false, "Game with id " + gameId + " does not exist", gameId, deckId);
        }

        // If the deck is present, we delete it so it can't be used in 2 games
        Deck deck = deckDao.deleteDeckByDeckId(deckId);
        if (null == deck) {
            return new AddDeckToGameResponse(false, "Deck with id " + deckId + " does not exist", gameId, deckId);
        }

        // Shuffle the cards
        List<Card> cards = deck.getCardDeck();
        ShuffleUtils.shuffle(cards);

        // Add the cards to the game
        game.addCards(cards);

        return new AddDeckToGameResponse(true, null, gameId, deckId);
    }

    public synchronized AddPlayerToGameResponse addPlayerToGame(long gameId, String username) {
        // Verifications
        Game game = gameDao.getGame(gameId);
        if (null == game) {
            return new AddPlayerToGameResponse(false, "Game #" + gameId + " does not exist", gameId, username);
        }

        Player player = playerDao.getPlayer(username);
        if (null == player) {
            return new AddPlayerToGameResponse(false, "Player with username " + username + " does not exist", gameId, username);
        }

        if (player.isCurrentlyPlaying()) {
            return new AddPlayerToGameResponse(false, "Player with username " + username + " is already playing in game #" + gameId + ". They cannot join this game.", gameId, username);
        }
        player.setGameId(gameId);
        game.addPlayer(player);
        return new AddPlayerToGameResponse(true, null, gameId, username);
    }

    public RemovePlayerFromGameResponse removePlayerFromGame(long gameId, String username) {
        // Verifications
        Game game = gameDao.getGame(gameId);
        if (null == game) {
            return new RemovePlayerFromGameResponse(false, "Game #" + gameId + " does not exist", gameId, username);
        }

        Player player = playerDao.getPlayer(username);
        if (null == player) {
            return new RemovePlayerFromGameResponse(false, "Player with username " + username + " does not exist", gameId, username);
        }

        if (gameId != player.getGameId()) {
            return new RemovePlayerFromGameResponse(false, "Player with username " + username + " is not playing at Game #" + gameId, gameId, username);
        }
        player.leaveGame();

        // It is expected that the body of this if-block will never run.
        if (!game.removePlayer(player)) {
            return new RemovePlayerFromGameResponse(false, "Something wrong happened, even though Player with username " + username + " is part of Game #" + gameId + ". We weren't able to remove them...", gameId, username);
        }

        return new RemovePlayerFromGameResponse(true, null, gameId, username);
    }

    public DealCardResponse dealCard(long gameId, String username) {
        // Verifications
        Game game = gameDao.getGame(gameId);
        if (null == game) {
            return new DealCardResponse(false, "Game #" + gameId + " does not exist", gameId, username);
        }

        Player player = playerDao.getPlayer(username);
        if (null == player) {
            return new DealCardResponse(false, "Player with username " + username + " does not exist", gameId, username);
        }

        if (player.getGameId() != gameId) {
            return new DealCardResponse(false, "Player with username " + username + " is not playin at Game #" + gameId, gameId, username);
        }

        if (game.getCards().isEmpty()) {
            return new DealCardResponse(false, "They aren't any cards in the deck of Game #" + gameId, gameId, username);
        }
        Card card = game.getCards().removeFirst();
        player.addCardToHand(card);
        return new DealCardResponse(true, null, gameId, username);
    }

    public GetPlayersResponse getPlayers(long gameId) {
        Game game = gameDao.getGame(gameId);
        if (null == game) {
            return new GetPlayersResponse(false, "Game #" + gameId + " is not found!", null);
        }

        List<PlayerWithScore> playersWithScore =
                game.getPlayers()
                        .stream()
                        .map(p -> getPlayerWithScoreFromPlayer(p, gameId))
                        .sorted()
                        .toList();

        return new GetPlayersResponse(true, null, playersWithScore);
    }

    private PlayerWithScore getPlayerWithScoreFromPlayer(Player p, long gameId) {
        int score = p.getHand()
                .stream()
                .mapToInt(Card::getRankValue)
                .sum();
        return new PlayerWithScore(gameId, p.getUserName(), p.getHand(), score);
    }

    public GetCountPerSuitResponse getCountPerSuit(long gameId) {
        Game game = gameDao.getGame(gameId);
        if (null == game) {
            return new GetCountPerSuitResponse(false, "Game #" + gameId + " is not found!", -1, -1, -1, -1);
        }

        List<Card> cards = game.getCards();
        int[] arr = new int[4];
        for (Card card : cards) {
            switch (card.suit()) {
                case HEARTS -> arr[0]++;
                case DIAMONDS -> arr[1]++;
                case SPADES -> arr[2]++;
                case CLUBS -> arr[3]++;
            }
        }
        return new GetCountPerSuitResponse(true, null, arr[0], arr[1], arr[2], arr[3]);
    }
}
