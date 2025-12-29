package com.pixelquest;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.pixelquest.Entity.*;
import com.pixelquest.Repository.*;
import com.pixelquest.Service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

//@ExtendWith(MockitoExtension.class)
public class GameServiceTest {

//  @Mock private GameRepository gameRepository;
//  @Mock private PlayerRepository playerRepository;
//  @Mock private PointSampleRepository pointSampleRepository;
//
//  @InjectMocks private GameService gameService;
//
//  private Player player;
//  private Game game;
//
//  @BeforeEach
//  void setUp() {
//    player = new Player();
//    player.setId(1L);
//    player.setLevel(new PlayerLevel(0));
//
//    game = new Game();
//    game.setId(100L);
//    game.setPlayer(player);
//    game.setDifficulty(Difficulty.EASY);
//    game.setStartPoint(new Point(0, 0));
//    game.setTargetPoint(new Point(100, 0)); // Distance = 100
//  }
//
//  @Test
//  void testFinishGameCalculatesScore() {
//    // Arrange: Mock two samples (0ms and 1000ms)
//    PointSample s1 = new PointSample(); s1.setTimestamp(0L);
//    PointSample s2 = new PointSample(); s2.setTimestamp(1000L); // 1 second movement
//
//    when(gameRepository.findById(100L)).thenReturn(Optional.of(game));
//    when(pointSampleRepository.findByGameId(100L)).thenReturn(Arrays.asList(s1, s2));
//    when(playerRepository.findById(1L)).thenReturn(Optional.of(player));
//    when(playerRepository.save(any())).thenReturn(player);
//
//    // Act
//    Game finishedGame = gameService.finishGameWithDirectData(100L);
//
//    // Assert
//    assertNotNull(finishedGame.getScore());
//    assertTrue(finishedGame.getScore() > 0, "Score should be positive");
//    verify(gameRepository, times(1)).save(any(Game.class)); // Once in finish, once for player
//  }
}
