<script setup>
import { ref, onMounted } from "vue";
import api from "../services/api";

const props = defineProps({
  activePlayerId: Number
});

const players = ref([]);
const newUsername = ref("");
const errorMsg = ref(null);
const isCreating = ref(false);

const emit = defineEmits(["select-player"]);

// Load players immediately when the page opens
onMounted(async () => {
  try {
    players.value = await api.getPlayers();
  } catch (e) {
    errorMsg.value = "Server unreachable. Is the backend running?";
    console.error(e);
  }
});

const addPlayer = async () => {
  if (!newUsername.value.trim()) return;

  isCreating.value = true;
  errorMsg.value = null;

  try {
    // Only sending username now
    const player = await api.createPlayer(newUsername.value);

    // Add to list and clear input
    players.value.push(player);
    newUsername.value = "";

    // Automatically select the new player
    emit("select-player", player);
  } catch (e) {
    errorMsg.value = "Error creating player.";
  } finally {
    isCreating.value = false;
  }
};

  const removePlayer = async (id) => {
  if (!confirm("Delete this player and all their game history?")) return;
  
  try {
    await api.deletePlayer(id); 
    players.value = players.value.filter(p => p.id !== id);
    
   
    if (props.activePlayerId === id) {
      emit("select-player", null);
    }
  } catch (e) {
    errorMsg.value = "Could not delete player.";
  }
};
</script>

<template>
  <div class="sidebar">
    <div class="header">
      <h3>PixelQuest Users</h3>
    </div>

    <div class="add-player-box">
      <input
        v-model="newUsername"
        @keyup.enter="addPlayer"
        placeholder="Enter username..."
        :disabled="isCreating"
      />
      <button @click="addPlayer" :disabled="isCreating || !newUsername">
        {{ isCreating ? "..." : "+" }}
      </button>
    </div>

    <div v-if="errorMsg" class="error">{{ errorMsg }}</div>

    <ul class="player-list">
      <li
        v-for="player in players"
        :key="player.id"
        @click="$emit('select-player', player)"
      >
        <span class="name">{{ player.username }}</span>
        <span class="rank">{{
          player.level ? player.level.level : "Beginner"
        }}</span>
        <button class="delete-btn" @click.stop="removePlayer(player.id)">
          &times;
        </button>
      </li>
    </ul>
  </div>
</template>

<style scoped>
.sidebar {
  width: 260px;
  background: #1e293b;
  color: #f1f5f9;
  display: flex;
  flex-direction: column;
  height: 100vh;
  border-right: 1px solid #334155;
}

.header {
  padding: 20px;
  border-bottom: 1px solid #334155;
}
h3 {
  margin: 0;
  font-size: 1.2rem;
  color: #38bdf8;
}

.add-player-box {
  padding: 15px;
  display: flex;
  gap: 8px;
  background: #0f172a;
}

input {
  flex: 1;
  padding: 8px;
  border-radius: 4px;
  border: 1px solid #334155;
  background: #1e293b;
  color: white;
}

button {
  background: #38bdf8;
  color: #0f172a;
  border: none;
  border-radius: 4px;
  font-weight: bold;
  cursor: pointer;
  width: 30px;
}
button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.error {
  color: #f87171;
  padding: 0 15px;
  font-size: 0.8rem;
  margin-top: 5px;
}

.player-list {
  list-style: none;
  padding: 0;
  margin: 0;
  overflow-y: auto;
  
}

.player-list li {
  padding: 15px 20px;
  cursor: pointer;
  border-bottom: 1px solid #334155;
  display: flex;
  justify-content: space-between;
  align-items: center;
  transition: background 0.2s;
  border-left: 4px solid transparent;
}

.player-list li:hover {
  background: #334155;
}

.name {
  font-weight: 500;
}
.rank {
  font-size: 0.75rem;
  background: #334155;
  padding: 2px 8px;
  border-radius: 12px;
  color: #94a3b8;
}

.player-list li.active {
  background: #334155;
  border-left: 4px solid #38bdf8;
}

.player-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.delete-btn {
  background: transparent;
  color: #64748b;
  font-size: 1.5rem;
  width: auto;
  padding: 0 10px;
}

.delete-btn:hover {
  color: #f87171;
  background: transparent;
}

.player-list li:hover .delete-btn {
  opacity: 1;
}
</style>
