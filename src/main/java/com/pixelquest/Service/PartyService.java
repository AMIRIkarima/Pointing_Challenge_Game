package com.pixelquest.Service;

import com.pixelquest.Entity.*;
import com.pixelquest.Repository.PartyRepository;
import com.pixelquest.Repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PartyService {

    @Autowired
    private PartyRepository partyRepository;

    @Autowired
    private PlayerRepository playerRepository;

    // Default Fitts parameters used if a player has no history
    private final double DEFAULT_A = 0.2;
    private final double DEFAULT_B = 0.1;

    /**
     * MOBILE APP: Starts a new party experiment.
     * Generates a random target based on the selected difficulty.
     */
    public Party startParty(Long playerId, Difficulty difficulty) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));

        Party party = new Party();
        party.setPlayer(player);
        party.setDifficulty(difficulty);
        party.setCreationDate(LocalDateTime.now());

        // Setup points: Start at (0,0), Target distance scaled by difficulty multiplier
        int range = (int) (difficulty.getMultiplier() * 100);
        party.setStartPoint(new Point(0, 0));
        party.setTargetPoint(new Point(20 + (int) (Math.random() * range), 20 + (int) (Math.random() * range)));

        return partyRepository.save(party);
    }

    /**
     * ESP32: Polls for the active mission started by the mobile app.
     */
    public Party getActivePartyForPlayer(Long playerId) {
        return partyRepository.findByPlayerId(playerId).stream()
                .filter(p -> p.getScore() == null)
                .sorted((p1, p2) -> p2.getCreationDate().compareTo(p1.getCreationDate()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No active party found for this player"));
    }

    /**
     * BACKEND: Fitts's Law Calculation and XP reward.
     * Called by ESP32 providing the direct movement time and distance achieved.
     */
    public Party finishPartyWithDirectData(Long partyId, double timeSec, double distance) {
        Party party = partyRepository.findById(partyId)
                .orElseThrow(() -> new RuntimeException("Party not found"));

        // Store direct metrics from hardware
        party.setMovementTime(timeSec);

        // Calculate Index of Difficulty (ID) -> ID = log2(D + 1)
        double id = Math.log(distance + 1) / Math.log(2);
        party.setIndexDifficulty(id);

        // Calculate Expected Time using Player's personal a & b parameters (or defaults)
        double a = (party.getPlayer().getFittsA() != null) ? party.getPlayer().getFittsA() : DEFAULT_A;
        double b = (party.getPlayer().getFittsB() != null) ? party.getPlayer().getFittsB() : DEFAULT_B;
        double expectedTime = a + b * id;

        // XP computation: Difficulty Multiplier * Base XP * Performance Ratio
        double xp = party.getDifficulty().getMultiplier() * 50 * (expectedTime / timeSec);
        xp = Math.max(0, Math.min(500, xp)); // Cap XP gain per party

        party.setScore(xp);
        partyRepository.save(party);

        // Update player's global progress
        updatePlayerLevel(party.getPlayer().getId(), (int) xp);
        updatePlayerFittsParameters(party.getPlayer());

        return party;
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
     * using Linear Regression on their party history.
     */
    private void updatePlayerFittsParameters(Player player) {
        List<Party> history = partyRepository.findByPlayerId(player.getId()).stream()
                .filter(p -> p.getScore() != null && p.getMovementTime() != null && p.getIndexDifficulty() != null)
                .toList();

        if (history.size() < 2) return; // Need at least two data points for a trend line

        double sumID = 0, sumMT = 0, sumIDMT = 0, sumIDSq = 0;
        int n = history.size();

        for (Party p : history) {
            double ID = p.getIndexDifficulty();
            double MT = p.getMovementTime();
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

    public List<Party> getPartiesByPlayer(Long playerId) {
        return partyRepository.findByPlayerId(playerId);
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