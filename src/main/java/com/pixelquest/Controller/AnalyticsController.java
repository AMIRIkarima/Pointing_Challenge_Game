package com.pixelquest.Controller;

import com.pixelquest.Service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

  @Autowired
  private GameService gameService;

  // Fetch data for the Scatter Plot (ID vs MT)
  @GetMapping("/fitts-data/{playerId}")
  public List<Map<String, Double>> getFittsRawData(@PathVariable Long playerId) {
        return gameService.getGamesByPlayer(playerId).stream()
          .filter(p -> p.getScore() != null && p.getMovementTime() != null) // Only completed games
          .map(p -> Map.of(
            "id", p.getIndexDifficulty(), // Retrieved from Game field
            "mt", p.getMovementTime()      // Retrieved from Game field
          )).toList();
  }

  // Fetch the current Player's Fitts Law coefficients (a and b)
  @GetMapping("/constants/{playerId}")
  public Map<String, Double> getPlayerConstants(@PathVariable Long playerId) {
    return Map.of(
                "interceptA", gameService.getInterceptA(playerId),
                "slopeB", gameService.getSlopeB(playerId)
    );
  }
}