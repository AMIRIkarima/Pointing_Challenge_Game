<template>
  <div class="history-container">
    <div class="history-header">
      <h3>Mission History</h3>
    </div>
    
    <div v-if="games.length === 0" class="empty-state">
      No games recorded yet. Start a session from the mobile app!
    </div>

    <table v-else>
      <thead>
        <tr>
          <th>Date</th>
          <th>Difficulty</th>
          <th>Time (s)</th>
          <th>Dist (px)</th>
          <th>Score</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="game in games" :key="game.id">
          <td class="date-cell">{{ formatDate(game.creationDate) }}</td>
          <td>
            <span :class="['difficulty-tag', (game.difficulty || '').toLowerCase()]">
              {{ game.difficulty || '-' }}
            </span>
          </td>
          <td class="numeric">{{ formatTime(game.movementTime) }}</td>
          <td class="numeric">{{ formatDistance(game) }}</td>
          <td class="score-cell">{{ formatScore(game.score) }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
defineProps(['games']);

const formatDate = (dateString) => {
  if (!dateString) return "-";
  const date = new Date(dateString);
  return date.toLocaleDateString() + ' ' + date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
};

const isFiniteNumber = (v) => typeof v === 'number' && Number.isFinite(v);

const formatTime = (t) => {
  // handle null/undefined/empty
  if (t === null || t === undefined || t === '') return '-';
  const n = Number(t);
  if (!Number.isFinite(n)) return '-';
  return `${n.toFixed(3)}s`;
};

const computeDistanceFromCoords = (g) => {
  if (g == null) return null;
  const sx = g.startX ?? g.sx ?? g.x1;
  const sy = g.startY ?? g.sy ?? g.y1;
  const ex = g.endX ?? g.ex ?? g.x2;
  const ey = g.endY ?? g.ey ?? g.y2;
  if (sx != null && sy != null && ex != null && ey != null) {
    const dx = Number(ex) - Number(sx);
    const dy = Number(ey) - Number(sy);
    const d = Math.sqrt(dx * dx + dy * dy);
    if (Number.isFinite(d)) return Math.round(d);
  }
  return null;
};

const formatDistance = (g) => {
  if (!g) return '-';
  const d = g.distance;
  if (d !== undefined && d !== null && d !== '') {
    const n = Number(d);
    if (Number.isFinite(n)) return Math.round(n);
  }
  const computed = computeDistanceFromCoords(g);
  if (computed != null) return computed;
  return '-';
};

const formatScore = (s) => {
  if (s === null || s === undefined || s === '') return '-';
  const n = Number(s);
  if (!Number.isFinite(n)) return '-';
  return `${n.toFixed(2)} XP`;
};
</script>

<style scoped>
.history-container {
  background: #ffffff;
  border-radius: 8px;
  padding: 20px;
  margin-top: 20px;
  box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1);
}

h3 {
  margin: 0 0 15px 0;
  color: #1e293b;
  font-size: 1.1rem;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th {
  color: #64748b;
  font-size: 0.75rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  text-align: left;
  padding: 12px 8px;
  border-bottom: 2px solid #f1f5f9;
}

td {
  padding: 12px 8px;
  border-bottom: 1px solid #f1f5f9;
  font-size: 0.9rem;
  color: #334155;
}

.date-cell {
  color: #94a3b8;
  font-size: 0.8rem;
}

.numeric {
  font-family: 'Courier New', Courier, monospace;
}

.score-cell {
  font-weight: bold;
  color: #10b981; 
}

.difficulty-tag {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 0.7rem;
  font-weight: bold;
  text-transform: uppercase;
}

.easy { background: #dcfce7; color: #166534; }
.medium { background: #fef9c3; color: #854d0e; }
.hard { background: #fee2e2; color: #991b1b; }

.empty-state {
  text-align: center;
  padding: 20px;
  color: #94a3b8;
  font-style: italic;
}
</style>