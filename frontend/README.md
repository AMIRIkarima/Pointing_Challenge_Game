# PixelQuest Analytics Dashboard

A Vue 3 + Vite analytics dashboard for tracking player performance metrics using Fitts's Law analysis. This application provides real-time visualization of player game statistics, movement time analysis, and performance regression calculations.

## Project Overview

PixelQuest Analytics is a professional dashboard that connects to a backend API to track player gameplay metrics. It analyzes movement times and target distances using Fitts's Law to measure player performance and cognitive load. The application demonstrates modern Vue 3 practices with a focus on user experience and data visualization.

##  Project Requirements Met

### VueJS Framework Implementation
-  **v-for directive**: Renders lists of players and game history
  - [PlayerList.vue](src/components/PlayerList.vue) - Renders player list with `v-for="player in players"`
  - [GameHistory.vue](src/components/GameHistory.vue) - Renders game history table rows with `v-for="game in games"`

-  **v-if/v-show conditionals**: Empty states and conditional content display
  - [App.vue](src/App.vue) - Shows welcome card `v-if="!selectedPlayer"`
  - [GameHistory.vue](src/components/GameHistory.vue) - Empty state `v-if="games.length === 0"`
  - [FittsChart.vue](src/components/FittsChart.vue) - Conditional chart display

-  **Modular architecture**: 5+ subcomponents with single responsibility principle
  - `PlayerList.vue` - Player sidebar with add/delete functionality
  - `GameHistory.vue` - Game records table with formatting
  - `FittsChart.vue` - Scatter plot with regression line
  - `StatCard.vue` - Reusable statistics display card
  - `App.vue` - Parent coordinator component
  - Centralized `api.js` service for all backend communication

-  **AJAX communication**: Native `fetch()` API with 6 endpoints
  - `getPlayers()` - Fetch all players
  - `createPlayer()` - Create new player
  - `deletePlayer()` - Remove player
  - `getPlayerHistory()` - Get game history
  - `getFittsData()` - Get Fitts law points
  - `getConstants()` - Get calculated constants (a, b)

-  **v-model directive**: Two-way data binding on user input
  - [PlayerList.vue](src/components/PlayerList.vue) - Username input with `v-model="newUsername"`
  - Bidirectional binding for form submissions

### UX Best Practices
-  **Input validation**: Username validation before player creation
  - Checks for non-empty input with `.trim()`
  - Button disabled state when input is empty
  
-  **Error messages**: Coherent error displays for all failure scenarios
  - Server unreachable message
  - Player creation failure feedback
  - Player deletion failure handling
  
-  **Empty states**: Informative messages when no data available
  - Welcome message when no player selected
  - "No games recorded" message
  - "No gameplay samples" message for charts
  
-  **Loading states**: Visual feedback during async operations
  - Button text changes to "Refreshing..." during data fetch
  - Loading state disables interaction
  
-  **Confirmation dialogs**: Safety prompt before destructive actions
  - Confirmation before player deletion with custom message
  
-  **Natural interface**: Intuitive and easy-to-use design
  - Dark sidebar for player list, light dashboard for content
  - Color-coded difficulty tags (Easy/Medium/Hard)
  - Responsive two-column layout
  - Auto-refresh data every 5 seconds
  - Formatted dates and numbers for readability

##  Getting Started

### Prerequisites
- Node.js 20.19.0 or higher (>=22.12.0 recommended)
- A running backend API server on `http://localhost:8080`

### Installation

```sh
npm install
```

### Development Server

Start the Vite development server with hot module replacement:

```sh
npm run dev
```

The application will be available at `http://localhost:5173` (or the port Vite assigns).

**Important**: Ensure your backend API server is running on `http://localhost:8080` before starting the dashboard.

### Production Build

Build the application for production:

```sh
npm run build
```

Generated files will be in the `dist/` directory.

### Preview Production Build

Preview the production build locally:

```sh
npm run preview
```

##  Project Structure

```
FittsDashboard/
├── src/
│   ├── App.vue                 # Main application component & state management
│   ├── main.js                 # Vue app entry point
│   ├── components/
│   │   ├── PlayerList.vue      # Player sidebar with add/delete functionality
│   │   ├── GameHistory.vue     # Game records table with formatting
│   │   ├── FittsChart.vue      # Scatter plot with regression line
│   │   ├── StatCard.vue        # Reusable statistics display card
│   │   └── icons/              # Icon components
│   ├── services/
│   │   └── api.js              # Centralized API client
│   └── assets/
│       ├── main.css            # Global styles
│       └── base.css            # Base styles
├── index.html                  # HTML entry point
├── vite.config.js              # Vite configuration
└── package.json                # Project dependencies
```

##  Technology Stack

- **Vue 3**: Progressive JavaScript framework with Composition API (`<script setup>`)
- **Vite**: Next-generation frontend build tool with hot module replacement
- **Chart.js + vue-chartjs**: Data visualization for Fitts's Law regression analysis
- **CSS3**: Responsive styling with Grid and Flexbox layouts

##  API Integration

The application communicates with a backend API at `http://localhost:8080`. Key endpoints:

| Method | Endpoint | Purpose |
|--------|----------|---------|
| GET | `/players` | Fetch all players |
| POST | `/players` | Create new player |
| DELETE | `/players/{id}` | Delete player |
| GET | `/analytics/history/{playerId}` | Get game history |
| GET | `/analytics/fitts-data/{playerId}` | Get Fitts law data points |
| GET | `/analytics/constants/{playerId}` | Get calculated constants (a, b) |

### Example Response Handling
The application includes robust data normalization to handle inconsistent API response formats:
- Supports multiple field naming conventions (camelCase, snake_case)
- Handles nested object structures for coordinate data
- Provides fallback values for missing fields

##  Core Features

### Player Management
- View all registered players in an organized sidebar
- Create new players with username input (with validation)
- Delete players with confirmation dialog
- Auto-select newly created players
- Visual feedback for active player selection

### Game Analytics
- Real-time game history table with formatted dates and scores
- Color-coded difficulty indicators (Easy/Medium/Hard)
- Movement time and score tracking with precision formatting
- Difficulty level display per game

### Fitts's Law Analysis
- Scatter plot visualization of gameplay data points
- Regression line calculation and display
- Constants calculation: reaction time (a) and processing speed (b)
- Live data refresh every 5 seconds when player is selected
- Graceful handling of missing data

### User Experience
- Intuitive two-column layout (analytics dashboard + game history sidebar)
- Responsive grid system for stat cards
- Loading indicators during data fetch operations
- Graceful error handling with user-friendly messages
- Empty states guiding users to actions
- Hover effects and visual feedback on interactive elements

##  Development

### IDE Setup
- **VS Code** + [Vue (Official)](https://marketplace.visualstudio.com/items?itemName=Vue.volar) extension
- Disable Vetur if installed for best Vue 3 support

### Browser DevTools
- **Chrome/Edge**: [Vue.js devtools](https://chromewebstore.google.com/detail/vuejs-devtools/nhdogjmejiglipccpnnnanhbledajbpd)
- **Firefox**: [Vue.js devtools](https://addons.mozilla.org/en-US/firefox/addon/vue-js-devtools/)

### Debugging
- Use Vue DevTools to inspect component hierarchy and reactive state
- Check Network tab for API requests and responses
- Browser Console for error messages and debugging output

