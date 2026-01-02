# Jeu de Points (Android)

This is the Android client for the PixelQuest backend. It lets a player start a game, hands control to an ESP32 to play it, and then shows the finished result once the backend records the score.

## Requirements
- Android Studio (Giraffe+), JDK 17
- Android emulator or device
- Backend running locally on port 8080

Install links:
- Android Studio: https://developer.android.com/studio
- OpenJDK 17: https://adoptium.net/temurin/releases/?version=17
- Android Emulator (part of Android Studio install)

## Backend connection
The app points to `http://10.0.2.2:8080/` (emulator loopback) and `ws://10.0.2.2:8080/ws/game`. Change these in `app/src/main/java/com/achraf/jeudepoints/utils/Constants.kt` if your backend runs elsewhere.

## How it works
1) From the app, enter a username on the first screen; the app creates/ensures a player on the backend.
2) Start a game and pick a difficulty; the app calls `/games/start`.
3) The ESP32 polls `/games/active/{playerId}`, plays the mission, and posts results to `/games/{gameId}/direct-finish`.
4) The app polls the player payload and shows the latest game; when the backend sets `score`, the UI marks it completed.

## Setup
1) Start the backend on your machine: `./gradlew bootRun` (in the backend project).
2) In this Android project, let Gradle sync in Android Studio.
3) Verify the base URL in `Constants.kt` matches your backend host/IP.
4) Build: `./gradlew :app:assembleDebug` or use Android Studio’s Build > Make Project.

## Run in Android Studio
1) Use the device/emulator target you prefer.
2) In the Run/Debug configuration bar, change the Activity drop-down from the default to `Specified Activity`, then choose `com.achraf.jeudepoints.MainActivity`.
3) Click Run; the app should launch into the username screen.

## Basic usage
- Home shows your profile snapshot and any active game.
- Start Game lets you pick difficulty and signals the backend/ESP32.
- History shows past games from the backend.
- Leaderboard is derived from backend player scores.
- Profile lets you edit your username (subject to backend support).

If game status does not update, ensure the backend returns the player’s games list and that the ESP32 has posted results to `/games/{gameId}/direct-finish`.
