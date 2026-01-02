package com.pixelquest.Entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlayerLevelTest {

  @Test
  @DisplayName("Should compute correct ranks based on score")
  void testComputeRank() {
    PlayerLevel level = new PlayerLevel(0);
    assertEquals("Beginner", level.getLevel());

    level.addScore(600);
    assertEquals("Intermediate", level.getLevel());

    level.addScore(2000); // Total 2600
    assertEquals("Advanced", level.getLevel());

    level.addScore(3000); // Total 5600
    assertEquals("Expert", level.getLevel());

    level.addScore(5000); // Total 10600
    assertEquals("Pro", level.getLevel());
  }
}