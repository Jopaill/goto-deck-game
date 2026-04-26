package com.jordanpaille.deckgame.service;

import com.jordanpaille.deckgame.dao.DeckDao;
import com.jordanpaille.deckgame.dao.GameDao;
import com.jordanpaille.deckgame.dao.PlayerDao;
import com.jordanpaille.deckgame.dto.*;
import com.jordanpaille.deckgame.dto.responses.*;
import com.jordanpaille.deckgame.utils.ShuffleUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
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
        Game deletedGame = gameDao.deleteGame(gameId);
        if (null == deletedGame) {
            return new DeleteGameResponse(false, gameId);
        }

        synchronized (deletedGame) {
            for (Player player : deletedGame.getPlayers()) {
                if (player.isCurrentlyPlaying() && player.getGameId() == gameId) {
                    player.leaveGame();
                    player.getHand().clear();
                }
            }
        }

        return new DeleteGameResponse(true, gameId);
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

        synchronized (game) {
            // Add the cards to the game
            game.addCards(cards);
        }

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

        if (!game.removePlayer(player)) {
            return new RemovePlayerFromGameResponse(false, "The removal of " + username + " failed at Game #" + gameId + ". Please try again.", gameId, username);
        }

        player.leaveGame();
        player.getHand().clear();

        return new RemovePlayerFromGameResponse(true, null, gameId, username);
    }

    public DealCardResponse dealCard(long gameId, String username, int count) {
        // Verifications
        Game game = gameDao.getGame(gameId);
        if (null == game) {
            return new DealCardResponse(false, "Game #" + gameId + " does not exist", gameId, username, count, 0);
        }

        Player player = playerDao.getPlayer(username);
        if (null == player) {
            return new DealCardResponse(false, "Player with username " + username + " does not exist", gameId, username, count, 0);
        }

        if (player.getGameId() != gameId) {
            return new DealCardResponse(false, "Player with username " + username + " is not playing at Game #" + gameId, gameId, username, count, 0);
        }

        if (count <= 0) {
            return new DealCardResponse(false, "Card count must be greater than 0", gameId, username, count, 0);
        }

        synchronized (game) {
            if (game.getCards().isEmpty()) {
                return new DealCardResponse(false, "There aren't any cards in the deck of Game #" + gameId, gameId, username, count, 0);
            }

            int dealtCardCount = 0;
            while (dealtCardCount < count && !game.getCards().isEmpty()) {
                Card card = game.getCards().removeFirst();
                player.addCardToHand(card);
                dealtCardCount++;
            }

            return new DealCardResponse(true, null, gameId, username, count, dealtCardCount);
        }
    }

    public DealCardResponse dealCard(long gameId, String username) {
        return dealCard(gameId, username, 1);
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

        int[] arr = new int[4];

        synchronized (game) {
            List<Card> cards = game.getCards();
            for (Card card : cards) {
                switch (card.suit()) {
                    case HEARTS -> arr[0]++;
                    case SPADES -> arr[1]++;
                    case CLUBS -> arr[2]++;
                    case DIAMONDS -> arr[3]++;
                }
            }
        }

        return new GetCountPerSuitResponse(true, null, arr[0], arr[1], arr[2], arr[3]);
    }

    public ShuffleGameDeckResponse shuffleGameDeck(long gameId) {
        Game game = gameDao.getGame(gameId);
        if (null == game) {
            return new ShuffleGameDeckResponse(false, "Game #" + gameId + " is not found!", gameId);
        }

        synchronized (game) {
            ShuffleUtils.shuffle(game.getCards());
        }

        return new ShuffleGameDeckResponse(true, null, gameId);
    }

    public GetRemainingCardCountsResponse getRemainingCardCounts(long gameId) {
        Game game = gameDao.getGame(gameId);
        if (null == game) {
            return new GetRemainingCardCountsResponse(false, "Game #" + gameId + " is not found!", null);
        }

        Map<Card.Suit, Map<Card.Rank, Integer>> counts = new EnumMap<>(Card.Suit.class);
        for (Card.Suit suit : Card.Suit.values()) {
            counts.put(suit, new EnumMap<>(Card.Rank.class));
            for (Card.Rank rank : Card.Rank.values()) {
                counts.get(suit).put(rank, 0);
            }
        }

        synchronized (game) {
            for (Card card : game.getCards()) {
                Map<Card.Rank, Integer> rankCounts = counts.get(card.suit());
                rankCounts.put(card.rank(), rankCounts.get(card.rank()) + 1);
            }
        }

        List<CardCount> cardCounts = new ArrayList<>();
        Card.Suit[] suitOrder = {
                Card.Suit.HEARTS,
                Card.Suit.SPADES,
                Card.Suit.CLUBS,
                Card.Suit.DIAMONDS
        };

        Card.Rank[] rankOrder = {
                Card.Rank.KING,
                Card.Rank.QUEEN,
                Card.Rank.JACK,
                Card.Rank.TEN,
                Card.Rank.NINE,
                Card.Rank.EIGHT,
                Card.Rank.SEVEN,
                Card.Rank.SIX,
                Card.Rank.FIVE,
                Card.Rank.FOUR,
                Card.Rank.THREE,
                Card.Rank.TWO,
                Card.Rank.ACE
        };

        for (Card.Suit suit : suitOrder) {
            for (Card.Rank rank : rankOrder) {
                cardCounts.add(new CardCount(suit, rank, counts.get(suit).get(rank)));
            }
        }

        return new GetRemainingCardCountsResponse(true, null, cardCounts);
    }
}
