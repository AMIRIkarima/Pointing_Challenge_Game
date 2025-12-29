package com.pixelquest.Service;

import com.pixelquest.Entity.*;
import com.pixelquest.Repository.GameRepository;
import com.pixelquest.Repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlayerRepository playerRepository;

    // Defaults match the ESP32 OLED in your attachment (128x64)
    @Value("${pixelquest.screen.width:128}")
    private int screenWidth;

    @Value("${pixelquest.screen.height:64}")
    private int screenHeight;

    // Default Fitts parameters used if a player has no history
    private final double DEFAULT_A = 0.2;
    private final double DEFAULT_B = 0.1;

    /**
     * MOBILE APP: Starts a new game experiment.
     * Generates a random target based on the selected difficulty.
     */
    public Game startGame(Long playerId, Difficulty difficulty) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));

        Game game = new Game();
        game.setPlayer(player);
        game.setDifficulty(difficulty);
        game.setCreationDate(LocalDateTime.now());

        // Setup points: Start fixed to center of the device screen
        int range = (int) (difficulty.getMultiplier() * 100);
        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;
        game.setStartPoint(new Point(centerX, centerY));
        // Target randomized around center to match ESP32 behavior (offsets from center)
        int offsetX = (int) ((Math.random() - 0.5) * range);
        int offsetY = (int) ((Math.random() - 0.5) * range);
        int tx = Math.max(0, Math.min(screenWidth - 1, centerX + offsetX));
        int ty = Math.max(0, Math.min(screenHeight - 1, centerY + offsetY));
        game.setTargetPoint(new Point(tx, ty));

        return gameRepository.save(game);
    }

    /**
     * ESP32: Polls for the active mission started by the mobile app.
     */
    public Game getActiveGameForPlayer(Long playerId) {
        return gameRepository.findByPlayerId(playerId).stream()
                .filter(p -> p.getScore() == null)
                .sorted((p1, p2) -> p2.getCreationDate().compareTo(p1.getCreationDate()))
                .findFirst()
            .orElseThrow(() -> new RuntimeException("No active game found for this player"));
    }

    /**
     * BACKEND: Fitts's Law Calculation and XP reward.
     * Called by ESP32 providing the direct movement time and distance achieved.
     */
    public Game finishGameWithDirectData(Long gameId, double timeSec, double distance) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        // Store direct metrics from hardware
        game.setMovementTime(timeSec);

        // Calculate Index of Difficulty (ID) -> ID = log2(D + 1)
        double id = Math.log(distance + 1) / Math.log(2);
        game.setIndexDifficulty(id);

        // Calculate Expected Time using Player's personal a & b parameters (or defaults)
        double a = (game.getPlayer().getFittsA() != null) ? game.getPlayer().getFittsA() : DEFAULT_A;
        double b = (game.getPlayer().getFittsB() != null) ? game.getPlayer().getFittsB() : DEFAULT_B;
        double expectedTime = a + b * id;

        // XP computation: Difficulty Multiplier * Base XP * Performance Ratio
        double xp = game.getDifficulty().getMultiplier() * 50 * (expectedTime / timeSec);
        xp = Math.max(0, Math.min(500, xp)); // Cap XP gain per game

        game.setScore(xp);
        gameRepository.save(game);

        // Update player's global progress
        updatePlayerLevel(game.getPlayer().getId(), (int) xp);
        updatePlayerFittsParameters(game.getPlayer());

        return game;
    }

    /**
     * Updates player score and rank.
     */
    public void updatePlayerLevel(Long playerId, int gainedScore) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));

        PlayerLevel level = player.getLevel();
        if (level == null) {
            level = new PlayerLevel(0);
        }

        level.addScore(gainedScore);
        player.setLevel(level);
        playerRepository.save(player);
    }

    /**
     * ANALYTICS: Updates the player's personal Fitts parameters (a and b)
     * using Linear Regression on their game history.
     */
    private void updatePlayerFittsParameters(Player player) {
        List<Game> history = gameRepository.findByPlayerId(player.getId()).stream()
                .filter(p -> p.getScore() != null && p.getMovementTime() != null && p.getIndexDifficulty() != null)
                .toList();

        if (history.size() < 2) return; // Need at least two data points for a trend line

        double sumID = 0, sumMT = 0, sumIDMT = 0, sumIDSq = 0;
        int n = history.size();

        for (Game g : history) {
            double ID = g.getIndexDifficulty();
            double MT = g.getMovementTime();
            sumID += ID;
            sumMT += MT;
            sumIDMT += (ID * MT);
            sumIDSq += (ID * ID);
        }

        // Least Squares formulas
        double slopeB = (n * sumIDMT - sumID * sumMT) / (n * sumIDSq - sumID * sumID);
        double interceptA = (sumMT - slopeB * sumID) / n;

        player.setFittsA(interceptA);
        player.setFittsB(slopeB);
        playerRepository.save(player);
    }

    // --- Analytics Helper Methods for Controllers ---

    public List<Game> getGamesByPlayer(Long playerId) {
        return gameRepository.findByPlayerId(playerId);
    }

    public Double getInterceptA(Long playerId) {
        return playerRepository.findById(playerId)
                .map(Player::getFittsA)
                .orElse(DEFAULT_A);
    }

    public Double getSlopeB(Long playerId) {
        return playerRepository.findById(playerId)
                .map(Player::getFittsB)
                .orElse(DEFAULT_B);
    }
}