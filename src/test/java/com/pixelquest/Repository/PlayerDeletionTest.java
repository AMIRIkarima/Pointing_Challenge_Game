package com.pixelquest.Repository;

import com.pixelquest.Entity.*;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.ArrayList;
import java.util.Optional;



@DataJpaTest
class PlayerDeletionTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private PlayerRepository playerRepository;

  @Autowired
  private GameRepository gameRepository;

  @Test
  void shouldDeleteGamesWhenPlayerIsDeleted() {
    //  Setup: Create a Player with 2 Games
    Player player = new Player();
    player.setUsername("Tester1");
    player.setLevel(new PlayerLevel(0));
    player.setGames(new ArrayList<>());

    Game game1 = new Game();
    game1.setDifficulty(Difficulty.EASY);
    game1.setPlayer(player);
    player.getGames().add(game1);

    Game game2 = new Game();
    game2.setDifficulty(Difficulty.HARD);
    game2.setPlayer(player);
    player.getGames().add(game2);

    // Save everything
    Player savedPlayer = entityManager.persistAndFlush(player);
    Long playerId = savedPlayer.getId();
    Long g1Id = savedPlayer.getGames().get(0).getId();
    Long g2Id = savedPlayer.getGames().get(1).getId();

    // Verify they exist before deletion
    assertThat(playerRepository.findById(playerId)).isPresent();
    assertThat(gameRepository.findById(g1Id)).isPresent();
    assertThat(gameRepository.findById(g2Id)).isPresent();

    // Action: Delete the Player
    playerRepository.deleteById(playerId);
    entityManager.flush(); // Force sync with DB
    entityManager.clear(); // Clear cache to ensure we read from DB

    // Assertion: Check that both Player AND Games are gone
    Optional<Player> deletedPlayer = playerRepository.findById(playerId);
    assertThat(deletedPlayer).isEmpty();

    // This checks the CascadeType.ALL / orphanRemoval logic
    assertThat(gameRepository.findById(g1Id)).isEmpty();
    assertThat(gameRepository.findById(g2Id)).isEmpty();
  }
}