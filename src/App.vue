<script setup>
import { ref, watch } from 'vue';
import PlayerList from './components/PlayerList.vue';
import FittsChart from './components/FittsChart.vue';
import api from './services/api';

const selectedPlayer = ref(null);
const fittsData = ref([]);
const stats = ref({ interceptA: 0, slopeB: 0 });
const isLoading = ref(false);

// Fetch data when a player is selected
const loadPlayerData = async () => {
  if (!selectedPlayer.value) return;
  
  isLoading.value = true;
  try {
    const [points, constants] = await Promise.all([
      api.getFittsData(selectedPlayer.value.id),
      api.getConstants(selectedPlayer.value.id)
    ]);
    fittsData.value = points;
    stats.value = constants;
  } catch (e) {
    console.error(e);
  } finally {
    isLoading.value = false;
  }
};

const handleSelect = (player) => {
  selectedPlayer.value = player;
  loadPlayerData();
};

// "Extra Idea": Auto-refresh every 5 seconds to see live game updates
setInterval(() => {
  if (selectedPlayer.value) loadPlayerData();
}, 5000);
</script>

<template>
  <div class="app-container">
    <PlayerList @select-player="handleSelect" />

    <main class="dashboard">
      <div v-if="!selectedPlayer" class="empty-state">
        <h2>Welcome to PixelQuest Analytics</h2>
        <p>Select a player to view Fitts's Law performance.</p>
      </div>

      <div v-else class="content">
        <header>
          <h1>{{ selectedPlayer.username }}'s Dashboard</h1>
          <button @click="loadPlayerData" class="refresh-btn">Refresh Data</button>
        </header>

        <div class="stats-grid">
          <div class="card">
            <h3>Reaction Time (a)</h3>
            <div class="value">{{ stats.interceptA.toFixed(3) }}s</div>
          </div>
          <div class="card">
            <h3>Processing Speed (b)</h3>
            <div class="value">{{ stats.slopeB.toFixed(3) }}s/bit</div>
          </div>
          <div class="card info">
            <p><strong>Interpretation:</strong> Lower 'b' means the player processes difficult targets faster.</p>
          </div>
        </div>

        <div class="chart-section" v-if="fittsData.length > 0">
           <FittsChart 
             :rawPoints="fittsData" 
             :slope="stats.slopeB" 
             :intercept="stats.interceptA" 
           />
        </div>
        <div v-else class="no-data">
           <p>No games played yet. Start a party on the mobile app!</p>
        </div>
      </div>
    </main>
  </div>
</template>

<style>
body { margin: 0; font-family: 'Inter', sans-serif; background: #f4f7f6; }
.app-container { display: flex; }
.dashboard { flex: 1; padding: 40px; }
.empty-state { display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100%; color: #7f8c8d; }

.stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 20px; margin-bottom: 30px; }
.card { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.05); text-align: center; }
.card.info { background: #e8f6f3; color: #16a085; text-align: left; font-size: 0.9rem; display: flex; align-items: center; }
.value { font-size: 2rem; font-weight: bold; color: #2c3e50; }

.refresh-btn { margin-left: auto; padding: 10px 20px; background: #3498db; color: white; border: none; border-radius: 5px; cursor: pointer; }
.refresh-btn:hover { background: #2980b9; }
</style>