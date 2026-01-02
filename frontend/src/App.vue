<script setup>
import { ref, onMounted, onUnmounted, watch } from "vue";
import PlayerList from "./components/PlayerList.vue";
import GameHistory from "./components/GameHistory.vue";
import FittsChart from "./components/FittsChart.vue";
import StatCard from "./components/StatCard.vue";
import api from "./services/api";


const selectedPlayer = ref(null);
const playerGames = ref([]);
const fittsData = ref([]);
const stats = ref({ fittsA: 0, fittsB: 0 });
const isLoading = ref(false);
let refreshInterval = null;


const fetchAllPlayerData = async (playerId) => {
  if (!playerId) return;

  isLoading.value = true;
  try {
    
    const [history, points, constants] = await Promise.all([
      api.getPlayerHistory(playerId),
      api.getFittsData(playerId),
      api.getConstants(playerId),
    ]);

    
    const normalizeGame = (g) => {
      if (!g) return g;
      const get = (keys) => {
        for (const k of keys) {
          if (g[k] !== undefined && g[k] !== null && g[k] !== "") return g[k];
        }
        return undefined;
      };

      const movementTime = get(["movementTime", "movement_time", "time", "mt", "movementTimeMs", "ms"]);
      const score = get(["score", "xp", "points"]);
      const difficulty = get(["difficulty", "diff", "level", "indexDifficulty", "index_difficulty"]);
      const creationDate = get(["creationDate", "createdAt", "creation_date", "timestamp", "timeStamp"]);
      const id = get(["id", "_id", "gameId"]);

      // accept nested point objects like { startPoint: { x, y }, targetPoint: { x, y } }
      const startX = get(["startX", "start_x", "sx", "x1"]) ?? (g.startPoint?.x ?? g.start_point?.x);
      const startY = get(["startY", "start_y", "sy", "y1"]) ?? (g.startPoint?.y ?? g.start_point?.y);
      const endX = get(["endX", "target_x", "ex", "x2"]) ?? (g.targetPoint?.x ?? g.target_point?.x ?? g.target?.x);
      const endY = get(["endY", "target_y", "ey", "y2"]) ?? (g.targetPoint?.y ?? g.target_point?.y ?? g.target?.y);

      return {
        ...g,
        id,
        creationDate,
        difficulty,
        movementTime,
        score,
        startX,
        startY,
        endX,
        endY,
      };
    };

    playerGames.value = Array.isArray(history) ? history.map(normalizeGame) : history;
    fittsData.value = points;

   
    console.debug('fetchAllPlayerData: raw history sample', history && history[0]);
    console.debug('fetchAllPlayerData: normalized history sample', playerGames.value && playerGames.value[0]);
    console.debug('fetchAllPlayerData: points sample', points && points[0]);
    console.debug('fetchAllPlayerData: constants', constants);


    stats.value = {
      fittsA: constants?.fittsA ?? constants?.interceptA ?? 0,
      fittsB: constants?.fittsB ?? constants?.slopeB ?? 0,
    };
  } catch (e) {
    console.error("Error synchronization failed:", e);
  } finally {
    isLoading.value = false;
  }
};

const handleSelectPlayer = (player) => {
  selectedPlayer.value = player;
  if (!player) {
    playerGames.value = [];
    fittsData.value = [];
  }
};


watch(selectedPlayer, (newPlayer) => {
  if (newPlayer) fetchAllPlayerData(newPlayer.id);
});


onMounted(() => {
  
  refreshInterval = setInterval(() => {
    if (selectedPlayer.value && !isLoading.value) {
      fetchAllPlayerData(selectedPlayer.value.id);
    }
  }, 5000);
});

onUnmounted(() => {
  
  if (refreshInterval) clearInterval(refreshInterval);
});
</script>

<template>
  <div class="app-container">
    <PlayerList :activePlayerId="selectedPlayer?.id" @select-player="handleSelectPlayer" />

    <main class="dashboard">
      <div v-if="!selectedPlayer" class="empty-state">
        <div class="welcome-card">
          <h2>Welcome to PixelQuest Analytics</h2>
          <p>
            Select a player from the sidebar to view Fitts's Law performance and
            game history.
          </p>
        </div>
      </div>

      <div v-else class="content">
        <header class="dashboard-header">
          <h1>{{ selectedPlayer.username }}'s Dashboard</h1>
          <button @click="fetchAllPlayerData(selectedPlayer.id)" class="refresh-btn" :disabled="isLoading">
            {{ isLoading ? "Refreshing..." : "Refresh Data" }}
          </button>
        </header>

        <div class="main-grid">
          <div class="left-column">
            <div class="stats-grid">
              <StatCard title="Reaction Time (a)" :value="stats.fittsA" unit="s" description="Base movement delay" />
              <StatCard title="Processing Speed (b)" :value="stats.fittsB" unit="s/bit"
                description="Time per unit of difficulty" />

            </div>

            <section class="chart-section">
              <div v-if="fittsData.length > 0">
                <FittsChart :rawPoints="fittsData" :slope="stats.fittsB" :intercept="stats.fittsA" />
              </div>
              <div v-else class="no-data-placeholder">
                <p>
                  No gameplay samples found. Start a game on the mobile app to see
                  the regression line!
                </p>
              </div>
            </section>
          </div>

          <aside class="right-column">
            <GameHistory :games="playerGames" />
          </aside>
        </div>
      </div>
    </main>
  </div>
</template>

<style>
body {
  margin: 0;
  font-family: "Inter", sans-serif;
  background: #f4f7f6;
}

.app-container {
  display: flex;
}

.dashboard {
  flex: 1;
  padding: 40px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #7f8c8d;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.main-grid {
  display: grid;
  grid-template-columns: 1fr 420px;
  gap: 24px;
  align-items: start;
}

.left-column {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.right-column {
  min-width: 320px;
  max-width: 420px;
  height: calc(100vh - 160px);
  overflow: auto;
}

.card {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
  text-align: center;
}

.card.info {
  background: #e8f6f3;
  color: #16a085;
  text-align: left;
  font-size: 0.9rem;
  display: flex;
  align-items: center;
}

.value {
  font-size: 2rem;
  font-weight: bold;
  color: #2c3e50;
}

.refresh-btn {
  margin-left: auto;
  padding: 10px 20px;
  background: #3498db;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.refresh-btn:hover {
  background: #2980b9;
}
</style>
