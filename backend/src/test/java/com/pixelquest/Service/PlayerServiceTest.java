package com.pixelquest.Service;

import com.pixelquest.Entity.Player;
import com.pixelquest.Repository.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

  @Mock
  private PlayerRepository playerRepository;

  @InjectMocks
  private PlayerService playerService;

  @Test
  void testGetPlayer_Success() {
    Player player = new Player();
    player.setId(1L);
    player.setUsername("Gamer1");

    when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

    Player found = playerService.getPlayer(1L);
    assertEquals("Gamer1", found.getUsername());
  }

  @Test
  void testGetPlayer_NotFound() {
    when(playerRepository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(RuntimeException.class, () -> playerService.getPlayer(1L));
  }
}