# PixelQuest - A Pointing Challenge Game

## Project Description

**PixelQuest** is a fun and engaging pointing challenge game that combines mobile and frontend technology with embedded systems (ESP32). Players use their mobile devices to start the game, aiming to reach target coordinates as accurately as possible.

The game tracks player performance using Fitts's Law metrics to measure pointing accuracy and speed.These resuls are displayed in the web application.



---

## Requirements

Before you can run this project, you'll need to have the following installed on your computer:

### What You Need to Install (Easy Steps)

#### 1. **Java 21** (Required)
   - **Download from**: https://www.oracle.com/java/technologies/downloads/#java21
   - **Installation**: Just download and click "Next" through the installer
   - **Verify it worked**: Open Command Prompt and type:
     ```
     java -version
     ```
     You should see "java version 21" or similar

#### 2. **Git** 
   - **Download from**: https://git-scm.com/download/win
   - **Installation**: Click "Next" through the installer
   - If you skip this, you can still download the project manually

#### 3. **A Text Editor or IDE** (Optional)
   - **VS Code** (Free & Easy): https://code.visualstudio.com
   - **IntelliJ IDEA** (Free Community Edition): https://www.jetbrains.com/idea/download/
   - If you skip this, you can still run everything from Command Prompt

#### 4. **Database** (Automatic)
   - H2 Database is automatically included - **no manual installation needed!**

---

##  How to Run the Project

### Step 1: Get the Project Files

**Option A - Using Git (Recommended if you installed it):**
```
Open Command Prompt and type:
git clone https://github.com/your-username/pixelquest.git
cd pixelquest
```

**Option B - Manual Download:**
1. Download the project as a ZIP file
2. Right-click and select "Extract All"
3. Open Command Prompt and navigate to the folder:
   ```
   cd C:\path\to\pixelquest
   ```

### Step 2: Start the Application

1. **Open Command Prompt** (Windows key + R, type `cmd`, press Enter)

2. **Navigate to your project folder**:
   ```
   cd C:\path\to\pixelquest
   ```

3. **Run the application**:
   ```
   gradlew.bat bootRun
   ```
   
   **Note**: On your first run, this might take 2-3 minutes as it downloads libraries. 

4. **Look for this message** (means it's working):
   ```
   Tomcat started on port(s): 8080 (http)
   ```

### Step 3: Access the Application

Once you see the "Tomcat started" message:

- **Game API**: Open your browser and go to:
  ```
  http://localhost:8080
  ```

---

##  API Endpoints (For Developers & Testers)

### Player Management

#### Get All Players
```
GET http://localhost:8080/players
```
Returns: List of all registered players

#### Create a New Player
```
POST http://localhost:8080/players
Body (JSON):
{
  "username": "PlayerName"
}
```

#### Get Player Profile
```
GET http://localhost:8080/players/{playerId}
```

#### Get Player Level
```
GET http://localhost:8080/players/{playerId}/level
```

#### Delete Player
```
DELETE http://localhost:8080/players/{playerId}
```

### Game Management

#### Start a New Game
```
POST http://localhost:8080/games/start?playerId=1&difficulty=EASY
```
- **Difficulty options**: EASY, MEDIUM, HARD

#### Get Active Game
```
GET http://localhost:8080/games/active/{playerId}
```

#### Finish Game with Results
```
POST http://localhost:8080/games/{gameId}/direct-finish
Body (JSON):
{
  "time_sec": 5.2,
  "distance": 15.5
}
```

---

##  Project Structure

```
pixelquest/
├── src/main/java/com/pixelquest/     # Main code
│   ├── Controller/                     # API endpoints
│   │   ├── PlayerController.java      # Player management
│   │   ├── GameController.java        # Game operations
│   │   └── AnalyticsController.java   # Statistics
│   ├── Entity/                         # Database models
│   │   ├── Player.java
│   │   ├── Game.java
│   │   ├── PlayerLevel.java
│   │   ├── Point.java
│   │   └── Difficulty.java
│   ├── Service/                        # Business logic
│   │   ├── PlayerService.java
│   │   └── GameService.java
│   └── Repository/                     # Database access
├── src/main/resources/
│   └── application.properties          # Configuration
├── src/test/java/com/pixelquest/       # Tests
│                              
├── build.gradle                        # Project setup file
├── gradlew                            # Linux/Mac launcher
├── gradlew.bat                        # Windows launcher
└── README.md                          # This file
```

---

##  Troubleshooting

### Problem: "gradlew.bat is not recognized"
**Solution**: Make sure you're in the correct folder where `gradlew.bat` is located

### Problem: "Java not found"
**Solution**: 
- Java wasn't installed correctly
- Restart your computer after installing Java
- Go back to Step 1 and reinstall Java 21

### Problem: "Port 8080 already in use"
**Solution**: Another application is using port 8080
- Close other Java applications
- Or change the port in `application.properties`:
  ```
  server.port=8081
  ```

### Problem: Application starts but won't connect to database
**Solution**: 
- Check that `./data/` folder exists in your project
- If not, create it manually
- The database will be created automatically on first run

### Problem: "Getting errors about annotations"
**Solution**: This is normal on first run. The application builds automatically. Just wait and it should work.

---

##  For Developers: Building & Testing

### Run Tests
```
gradlew.bat test
```

### Clean Previous Build
```
gradlew.bat clean
```

### Build Without Running
```
gradlew.bat build
```


---

##  Data & Database

The application uses **H2 Database**, an in-memory database that:
-  Requires NO installation
-  Stores data in `./data/pixeldb.mv.db` folder
-  Automatically creates tables on first run
-  Can be reset by deleting the `./data/` folder

---

##  How the Game Works

1. **Player starts app**: Opens mobile application
2. **Selects difficulty**: EASY, MEDIUM, or HARD
3. **Receives target**: App sends target coordinates to ESP32 device
4. **Completes challenge**: Player aims the device at the target
5. **Records performance**: Time taken and accuracy measured
6. **Analyzes results**: Fitts's Law metrics calculate performance
7. **Updates profile**: Player level and stats updated

---

##  Technologies Used

- **Spring Boot 4.0.1**: Web framework
- **Java 21**: Programming language
- **Hibernate & JPA**: Database access
- **H2 Database**: Embedded database
- **Gradle**: Build tool
- **JUnit 5**: Testing framework

---

##  Quick Start Checklist

- [ ] Downloaded and installed Java 21
- [ ] Verified Java installation (`java -version` works)
- [ ] Downloaded/cloned the project
- [ ] Opened Command Prompt in project folder
- [ ] Ran `gradlew.bat bootRun`
- [ ] Waited for "Tomcat started" message
- [ ] Opened http://localhost:8080 in browser
- [ ] Successfully created a player
- [ ] Started a game



---

##  License & Credits

PixelQuest - A Pointing Challenge Game
Build with ❤️ using Spring Boot


---

**Enjoy the game! 🎮**
