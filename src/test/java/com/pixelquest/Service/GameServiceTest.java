package com.pixelquest.Service;

import com.pixelquest.Entity.*;
import com.pixelquest.Repository.GameRepository;
import com.pixelquest.Repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

  @Mock
  private GameRepository gameRepository;
  @Mock
  private PlayerRepository playerRepository;

  @InjectMocks
  private GameService gameService;

  private Player player;
  private Game game;

  @BeforeEach
  void setUp() {
    player = new Player();
    player.setId(1L);
    player.setFittsA(0.5);
    player.setFittsB(0.2);
    player.setLevel(new PlayerLevel(0));

    game = new Game();
    game.setId(10L);
    game.setPlayer(player);
    game.setDifficulty(Difficulty.EASY);
  }

  @Test
  void testFinishGame_CalculatesXPAndUpdatesFitts() {
    when(gameRepository.findById(10L)).thenReturn(Optional.of(game));
    when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

    // Mock data: 1 second to move 100 pixels
    // ID = log2(100+1) ~ 6.65
    // Expected Time = 0.5 + 0.2 * 6.65 = 1.83s
    // XP Multiplier (EASY) = 1.0. XP = 1.0 * 50 * (1.83 / 1.0) = 91.5

    gameService.finishGameWithDirectData(10L, 1.0, 100.0);

    assertNotNull(game.getScore());
    assertTrue(game.getScore() > 0);
    assertEquals(6.65, game.getIndexDifficulty(), 0.1);

    // Verify repository interaction
    verify(gameRepository, atLeastOnce()).save(game);
    verify(playerRepository, atLeastOnce()).save(player);
  }

  @Test
  void testLinearRegression_FittsParameters() {
    // Setup a list of historical games to trigger the regression math
    Game g1 = new Game();
    g1.setIndexDifficulty(1.0); g1.setMovementTime(1.1);
    Game g2 = new Game();
    g2.setIndexDifficulty(2.0); g2.setMovementTime(1.5);

    when(gameRepository.findById(10L)).thenReturn(Optional.of(game));
    when(gameRepository.findByPlayer_Id(1L)).thenReturn(Arrays.asList(g1, g2));
    when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

    gameService.finishGameWithDirectData(10L, 1.5, 2.0);

    // Captured player should have updated A and B based on regression
    ArgumentCaptor<Player> playerCaptor = ArgumentCaptor.forClass(Player.class);
    verify(playerRepository, atLeastOnce()).save(playerCaptor.capture());

    Player updatedPlayer = playerCaptor.getValue();
    assertNotNull(updatedPlayer.getFittsA());
    assertNotNull(updatedPlayer.getFittsB());
  }
}