const API_URL = "http://localhost:8080"; 

export default {
  // Fetch all players to display in the list
  async getPlayers() {
    const res = await fetch(`${API_URL}/players`);
    if (!res.ok) throw new Error("Failed to fetch players");
    return res.json();
  },

  // Create a new player with JUST a username
  async createPlayer(username) {
    const res = await fetch(`${API_URL}/players`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username: username }) 
    });
    if (!res.ok) throw new Error("Failed to create player");
    return res.json();
  },

  async getFittsData(playerId) {
    const res = await fetch(`${API_URL}/analytics/fitts-data/${playerId}`);
    if (!res.ok) throw new Error('Failed to fetch Fitts data');
    return res.json();
  },

  async getConstants(playerId) {
    const res = await fetch(`${API_URL}/analytics/constants/${playerId}`);
    if (!res.ok) throw new Error('Failed to fetch constants');
    return res.json();
  }
};