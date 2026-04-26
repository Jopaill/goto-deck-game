package com.jordanpaille.deckgame.service;

import com.jordanpaille.deckgame.dao.DeckDao;
import com.jordanpaille.deckgame.dao.GameDao;
import com.jordanpaille.deckgame.dao.PlayerDao;
import com.jordanpaille.deckgame.dto.Card;
import com.jordanpaille.deckgame.dto.Deck;
import com.jordanpaille.deckgame.dto.Game;
import com.jordanpaille.deckgame.dto.Player;
import com.jordanpaille.deckgame.dto.responses.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private GameDao gameDao;

    @Mock
    private DeckDao deckDao;

    @Mock
    private PlayerDao playerDao;

    @InjectMocks
    private GameService gameService;

    @Test
    void createGame_shouldCreateGameAndPersistIt() {
        Game game = gameService.createGame();

        assertNotNull(game);
        assertEquals(0L, game.getGameId());
        verify(gameDao, times(1)).addGame(any(Game.class));
    }

    @Test
    void getGame_shouldReturnFoundTrueWhenGameExists() {
        Game game = new Game(10L);
        when(gameDao.getGame(10L)).thenReturn(game);

        GetGameResponse response = gameService.getGame(10L);

        assertTrue(response.success());
        assertEquals(game, response.game());
        verify(gameDao).getGame(10L);
    }

    @Test
    void getGame_shouldReturnFoundFalseWhenGameDoesNotExist() {
        when(gameDao.getGame(99L)).thenReturn(null);

        GetGameResponse response = gameService.getGame(99L);

        assertFalse(response.success());
        assertNull(response.game());
        verify(gameDao).getGame(99L);
    }

    @Test
    void deleteGame_shouldReturnSuccessTrueWhenDeleted() {
        Game deleted = new Game(5L);
        when(gameDao.deleteGame(5L)).thenReturn(deleted);

        DeleteGameResponse response = gameService.deleteGame(5L);

        assertTrue(response.success());
        assertEquals(5L, response.gameId());
        verify(gameDao).deleteGame(5L);
    }

    @Test
    void deleteGame_shouldReturnSuccessFalseWhenNothingDeleted() {
        when(gameDao.deleteGame(5L)).thenReturn(null);

        DeleteGameResponse response = gameService.deleteGame(5L);

        assertFalse(response.success());
        assertEquals(5L, response.gameId());
        verify(gameDao).deleteGame(5L);
    }

    @Test
    void addDeckToGame_shouldFailWhenGameDoesNotExist() {
        when(gameDao.getGame(1L)).thenReturn(null);

        AddDeckToGameResponse response = gameService.addDeckToGame(1L, 100L);

        assertFalse(response.success());
        assertEquals("Game with id 1 does not exist", response.errorMessage());
        assertEquals(1L, response.gameId());
        assertEquals(100L, response.deckId());
        verify(gameDao).getGame(1L);
        verifyNoInteractions(deckDao);
    }

    @Test
    void addDeckToGame_shouldFailWhenDeckDoesNotExist() {
        Game game = spy(new Game(1L));
        when(gameDao.getGame(1L)).thenReturn(game);
        when(deckDao.deleteDeckByDeckId(100L)).thenReturn(null);

        AddDeckToGameResponse response = gameService.addDeckToGame(1L, 100L);

        assertFalse(response.success());
        assertEquals("Deck with id 100 does not exist", response.errorMessage());
        verify(gameDao).getGame(1L);
        verify(deckDao).deleteDeckByDeckId(100L);
        verify(game, never()).addCards(anyList());
    }

    @Test
    void addDeckToGame_shouldAddDeckCardsToGameWhenGameAndDeckExist() {
        Game game = spy(new Game(1L));
        List<Card> cards = new LinkedList<>();
        cards.add(new Card(Card.Rank.ACE, Card.Suit.HEARTS));
        cards.add(new Card(Card.Rank.KING, Card.Suit.SPADES));
        Deck deck = mock(Deck.class);

        when(gameDao.getGame(1L)).thenReturn(game);
        when(deckDao.deleteDeckByDeckId(100L)).thenReturn(deck);
        when(deck.getCardDeck()).thenReturn(cards);

        AddDeckToGameResponse response = gameService.addDeckToGame(1L, 100L);

        assertTrue(response.success());
        assertNull(response.errorMessage());
        verify(gameDao).getGame(1L);
        verify(deckDao).deleteDeckByDeckId(100L);
        verify(deck).getCardDeck();
        verify(game).addCards(cards);
    }

    @Test
    void addPlayerToGame_shouldFailWhenGameDoesNotExist() {
        when(gameDao.getGame(1L)).thenReturn(null);

        AddPlayerToGameResponse response = gameService.addPlayerToGame(1L, "jordan");

        assertFalse(response.success());
        assertEquals("Game #1 does not exist", response.errorMessage());
        verify(gameDao).getGame(1L);
        verifyNoInteractions(playerDao);
    }

    @Test
    void addPlayerToGame_shouldFailWhenPlayerDoesNotExist() {
        Game game = new Game(1L);
        when(gameDao.getGame(1L)).thenReturn(game);
        when(playerDao.getPlayer("jordan")).thenReturn(null);

        AddPlayerToGameResponse response = gameService.addPlayerToGame(1L, "jordan");

        assertFalse(response.success());
        assertEquals("Player with username jordan does not exist", response.errorMessage());
        verify(gameDao).getGame(1L);
        verify(playerDao).getPlayer("jordan");
    }

    @Test
    void addPlayerToGame_shouldFailWhenPlayerAlreadyPlaying() {
        Game game = new Game(1L);
        Player player = mock(Player.class);

        when(gameDao.getGame(1L)).thenReturn(game);
        when(playerDao.getPlayer("jordan")).thenReturn(player);
        when(player.isCurrentlyPlaying()).thenReturn(true);

        AddPlayerToGameResponse response = gameService.addPlayerToGame(1L, "jordan");

        assertFalse(response.success());
        assertEquals(
                "Player with username jordan is already playing in game #1. They cannot join this game.",
                response.errorMessage()
        );
        verify(player, never()).setGameId(anyLong());
    }

    @Test
    void addPlayerToGame_shouldAddPlayerWhenValid() {
        Game game = spy(new Game(1L));
        Player player = mock(Player.class);

        when(gameDao.getGame(1L)).thenReturn(game);
        when(playerDao.getPlayer("jordan")).thenReturn(player);
        when(player.isCurrentlyPlaying()).thenReturn(false);

        AddPlayerToGameResponse response = gameService.addPlayerToGame(1L, "jordan");

        assertTrue(response.success());
        assertNull(response.errorMessage());
        verify(player).setGameId(1L);
        verify(game).addPlayer(player);
    }

    @Test
    void removePlayerFromGame_shouldFailWhenGameDoesNotExist() {
        when(gameDao.getGame(1L)).thenReturn(null);

        RemovePlayerFromGameResponse response = gameService.removePlayerFromGame(1L, "jordan");

        assertFalse(response.success());
        assertEquals("Game #1 does not exist", response.errorMessage());
        verifyNoInteractions(playerDao);
    }

    @Test
    void removePlayerFromGame_shouldFailWhenPlayerDoesNotExist() {
        Game game = new Game(1L);
        when(gameDao.getGame(1L)).thenReturn(game);
        when(playerDao.getPlayer("jordan")).thenReturn(null);

        RemovePlayerFromGameResponse response = gameService.removePlayerFromGame(1L, "jordan");

        assertFalse(response.success());
        assertEquals("Player with username jordan does not exist", response.errorMessage());
    }

    @Test
    void removePlayerFromGame_shouldFailWhenPlayerIsNotInThatGame() {
        Game game = new Game(1L);
        Player player = mock(Player.class);

        when(gameDao.getGame(1L)).thenReturn(game);
        when(playerDao.getPlayer("jordan")).thenReturn(player);
        when(player.getGameId()).thenReturn(99L);

        RemovePlayerFromGameResponse response = gameService.removePlayerFromGame(1L, "jordan");

        assertFalse(response.success());
        assertEquals("Player with username jordan is not playing at Game #1", response.errorMessage());
        verify(player, never()).leaveGame();
    }

    @Test
    void removePlayerFromGame_shouldFailWhenGameRemovePlayerReturnsFalse() {
        Game game = spy(new Game(1L));
        Player player = mock(Player.class);

        when(gameDao.getGame(1L)).thenReturn(game);
        when(playerDao.getPlayer("jordan")).thenReturn(player);
        when(player.getGameId()).thenReturn(1L);
        doReturn(false).when(game).removePlayer(player);

        RemovePlayerFromGameResponse response = gameService.removePlayerFromGame(1L, "jordan");

        assertFalse(response.success());
        assertTrue(response.errorMessage().contains("We weren't able to remove them"));
        verify(player).leaveGame();
        verify(game).removePlayer(player);
    }

    @Test
    void removePlayerFromGame_shouldSucceedWhenPlayerRemoved() {
        Game game = spy(new Game(1L));
        Player player = mock(Player.class);

        when(gameDao.getGame(1L)).thenReturn(game);
        when(playerDao.getPlayer("jordan")).thenReturn(player);
        when(player.getGameId()).thenReturn(1L);
        doReturn(true).when(game).removePlayer(player);

        RemovePlayerFromGameResponse response = gameService.removePlayerFromGame(1L, "jordan");

        assertTrue(response.success());
        assertNull(response.errorMessage());
        verify(player).leaveGame();
        verify(game).removePlayer(player);
    }

    @Test
    void dealCard_shouldFailWhenGameDoesNotExist() {
        when(gameDao.getGame(1L)).thenReturn(null);

        DealCardResponse response = gameService.dealCard(1L, "jordan");

        assertFalse(response.success());
        assertEquals("Game #1 does not exist", response.errorMessage());
        verifyNoInteractions(playerDao);
    }

    @Test
    void dealCard_shouldFailWhenPlayerDoesNotExist() {
        Game game = mock(Game.class);
        when(gameDao.getGame(1L)).thenReturn(game);
        when(playerDao.getPlayer("jordan")).thenReturn(null);

        DealCardResponse response = gameService.dealCard(1L, "jordan");

        assertFalse(response.success());
        assertEquals("Player with username jordan does not exist", response.errorMessage());
    }

    @Test
    void dealCard_shouldFailWhenPlayerNotInGame() {
        Game game = mock(Game.class);
        Player player = mock(Player.class);

        when(gameDao.getGame(1L)).thenReturn(game);
        when(playerDao.getPlayer("jordan")).thenReturn(player);
        when(player.getGameId()).thenReturn(2L);

        DealCardResponse response = gameService.dealCard(1L, "jordan");

        assertFalse(response.success());
        assertEquals("Player with username jordan is not playin at Game #1", response.errorMessage());
    }

    @Test
    void dealCard_shouldFailWhenGameHasNoCards() {
        Game game = mock(Game.class);
        Player player = mock(Player.class);

        when(gameDao.getGame(1L)).thenReturn(game);
        when(playerDao.getPlayer("jordan")).thenReturn(player);
        when(player.getGameId()).thenReturn(1L);
        when(game.getCards()).thenReturn(new LinkedList<>());

        DealCardResponse response = gameService.dealCard(1L, "jordan");

        assertFalse(response.success());
        assertEquals("They aren't any cards in the deck of Game #1", response.errorMessage());
    }

    @Test
    void dealCard_shouldGiveFirstCardToPlayerWhenValid() {
        Game game = mock(Game.class);
        Player player = mock(Player.class);
        Card card = new Card(Card.Rank.ACE, Card.Suit.HEARTS);
        LinkedList<Card> cards = new LinkedList<>(List.of(card));

        when(gameDao.getGame(1L)).thenReturn(game);
        when(playerDao.getPlayer("jordan")).thenReturn(player);
        when(player.getGameId()).thenReturn(1L);
        when(game.getCards()).thenReturn(cards);

        DealCardResponse response = gameService.dealCard(1L, "jordan");

        assertNotNull(response); // current implementation returns null on success
        verify(player).addCardToHand(card);
        assertTrue(cards.isEmpty());
    }

    @Test
    void getPlayers_shouldFailWhenGameDoesNotExist() {
        when(gameDao.getGame(1L)).thenReturn(null);

        GetPlayersResponse response = gameService.getPlayers(1L);

        assertFalse(response.success());
        assertEquals("Game #1 is not found!", response.errorMessage());
        assertNull(response.playersWithScore());
    }

    @Test
    void getPlayers_shouldReturnPlayersWithScoresSorted() {
        Game game = mock(Game.class);

        Player p1 = mock(Player.class);
        Player p2 = mock(Player.class);

        List<Card> p1Hand = List.of(
                new Card(Card.Rank.TEN, Card.Suit.HEARTS),
                new Card(Card.Rank.FIVE, Card.Suit.SPADES)
        );
        List<Card> p2Hand = List.of(
                new Card(Card.Rank.ACE, Card.Suit.CLUBS)
        );

        when(gameDao.getGame(1L)).thenReturn(game);
        when(game.getPlayers()).thenReturn(List.of(p1, p2));

        when(p1.getUserName()).thenReturn("alice");
        when(p1.getHand()).thenReturn(p1Hand);

        when(p2.getUserName()).thenReturn("bob");
        when(p2.getHand()).thenReturn(p2Hand);

        GetPlayersResponse response = gameService.getPlayers(1L);

        assertTrue(response.success());
        assertNotNull(response.playersWithScore());
        assertEquals(2, response.playersWithScore().size());
    }

    @Test
    void getCountPerSuit_shouldFailWhenGameDoesNotExist() {
        when(gameDao.getGame(1L)).thenReturn(null);

        GetCountPerSuitResponse response = gameService.getCountPerSuit(1L);

        assertFalse(response.success());
        assertEquals("Game #1 is not found!", response.errorMessage());
        assertEquals(-1, response.numberOfHearts());
        assertEquals(-1, response.numberOfDiamonds());
        assertEquals(-1, response.numberOfSpades());
        assertEquals(-1, response.numberOfClubs());
    }

    @Test
    void getCountPerSuit_shouldReturnCountsPerSuit() {
        Game game = mock(Game.class);
        List<Card> cards = List.of(
                new Card(Card.Rank.ACE, Card.Suit.HEARTS),
                new Card(Card.Rank.KING, Card.Suit.HEARTS),
                new Card(Card.Rank.TWO, Card.Suit.DIAMONDS),
                new Card(Card.Rank.THREE, Card.Suit.SPADES),
                new Card(Card.Rank.FOUR, Card.Suit.CLUBS),
                new Card(Card.Rank.FIVE, Card.Suit.CLUBS)
        );

        when(gameDao.getGame(1L)).thenReturn(game);
        when(game.getCards()).thenReturn(cards);

        GetCountPerSuitResponse response = gameService.getCountPerSuit(1L);

        assertTrue(response.success());
        assertNull(response.errorMessage());
        assertEquals(2, response.numberOfHearts());
        assertEquals(1, response.numberOfDiamonds());
        assertEquals(1, response.numberOfSpades());
        assertEquals(2, response.numberOfClubs());
    }
}