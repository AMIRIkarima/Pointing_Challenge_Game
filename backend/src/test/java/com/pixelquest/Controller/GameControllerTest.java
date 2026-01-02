package com.pixelquest.Controller;

import com.pixelquest.Entity.Difficulty;
import com.pixelquest.Entity.Game;
import com.pixelquest.Service.GameService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GameController.class)
class GameControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private GameService gameService;

  @Test
  void testStartGameEndpoint() throws Exception {
    // Prepare mock data
    Game mockGame = new Game();
    mockGame.setId(100L);
    mockGame.setDifficulty(Difficulty.HARD);

    // Stub the service method
    when(gameService.startGame(eq(1L), any(Difficulty.class))).thenReturn(mockGame);

    // Execute and Verify
    mockMvc.perform(post("/games/start")
                    .param("playerId", "1")
                    .param("difficulty", "HARD")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(100L))
            .andExpect(jsonPath("$.difficulty").value("HARD"));
  }
}