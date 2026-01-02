# PixelQuest_esp32
Toolbox project, embedded part

# ESP32 Game Controller

This project is an **ESP32-based game controller** that communicates with a backend server and a mobile or web application. It allows selecting a player, starting a game, and tracking movements using potentiometers. Results are automatically sent back to the backend.

---

## Project Overview

* **ESP32**: reads input from potentiometers and buttons, displays UI on OLED, controls RGB LED.
* **Backend Server**: stores player list, starts games, receives results.
* **Mobile/Web App**: creates players and initiates games.

The ESP32 does not control the game logic; it interacts with the backend and the user.

---

## Hardware Requirements

* 1 × ESP32 Dev Board
* 1 × OLED display with **integrated buttons** (128×64, I2C, SH1107)
* 2 × Potentiometers (10kΩ recommended)
* 1 × NeoPixel RGB LED (1 LED)
* Breadboard + jumper wires

<img width="1233" height="560" alt="image" src="https://github.com/user-attachments/assets/81122ae8-4dbb-4000-95ba-0755c425e2e7" />
<img width="2072" height="719" alt="image" src="https://github.com/user-attachments/assets/6f840a72-afa3-464a-a03c-cbf2d58b6fe6" />
<img width="507" height="674" alt="image" src="https://github.com/user-attachments/assets/e6995257-bde9-46bb-b8d0-0d14a1161a0f" />
<img width="512" height="692" alt="image" src="https://github.com/user-attachments/assets/2096aaba-05e8-4679-b2e3-d7d06b3e0912" />



### OLED (I2C + Buttons)

| OLED Pin | ESP32 Pin                               |
| -------- | --------------------------------------- |
| VCC      | 3.3V                                    |
| GND      | GND                                     |
| SCL      | GPIO 22                                 |
| SDA      | GPIO 23                                 |
| Buttons  | Integrated on OLED (no separate wiring) |

### Potentiometers

| Axis | ESP32 Pin |
| ---- | --------- |
| X    | GPIO 34   |
| Y    | GPIO 36   |

### NeoPixel RGB LED

| LED Pin | ESP32 Pin  |
| ------- | ---------- |
| DIN     | GPIO 27    |
| VCC     | 3.3V or 5V |
| GND     | GND        |

---

## Software Requirements

* **ESP32**: MicroPython firmware
* **Libraries**: `sh1107`, `neopixel`, `urequests`
* **Backend Server**: Node.js, Python, or any server that exposes the required endpoints
* **Network**: ESP32 and PC must be on the same Wi-Fi network

### ESP32 Game Setup with Ampy

This guide explains how to upload the **driver** and the **main program (`main.py`)** to your ESP32 board using **ampy**.

---

## 1️⃣ Prerequisites

Before starting, make sure you have:

- ESP32 board connected via USB
- MicroPython firmware installed on the ESP32
- Python installed on your computer

---

## 2️⃣ Install Ampy

Install ampy using pip
Open **PowerShell** and run: `pip install adafruit-ampy`

## 🔎 Find your ESP32 COM Port (Windows)

To use `ampy`, you need to know which **COM port** your ESP32 is connected to.

1. Open **Device Manager**  
   - Press `Win + X` → **Device Manager**

2. Expand the section **Ports (COM & LPT)**

3. Look for a device like: USB Serial Device (COM3)
   
4. The number in parentheses (e.g., `COM3`) is your **ESP32 COM port**.

> You will use this COM port in all `ampy` commands.

## Upload Driver and main.py
### SH1107 Driver

This project uses the **sh1107 driver** for the OLED.

* Check if the driver is already on the ESP32:

```python
import sh1107
```

If there is **no error**, the driver is installed.

* If not installed:

Replace COM_PORT with your ESP32 COM port (e.g., COM3).
Download the driver `sh1107.py` from the repository and go th its location on the command line.
Upload the driver first (example: sh1107.py) with the command : `ampy --port COM_PORT put sh1107.py`
 
### Upload the main program:

Download the 'main.py' file and open it with Visual studio code
#### Configuration

##### Wi-Fi

Modify these lines in the code to match your network:

```python
SSID = "YourWiFiName"
PASSWORD = "YourWiFiPassword"
```

##### Backend IP

Modify `API_BASE` with your PC's local IP:

```python
API_BASE = "http://192.168.1.42:8080"
```

Find your IP with `ipconfig` (Windows) or `ifconfig`/`ip a` (Linux/macOS).

#### Upload
Go to the "main.py" file location on the command line
Put the main.py file in the esp32 with the command : `ampy --port COM_PORT put main.py`

### Run the main program (optional, ESP32 usually runs main.py automatically):

`ampy --port COM_PORT run main.py`

### Check uploaded files:

`ampy --port COM_PORT ls`




---

## Code Structure

1. **API Functions**

   * `fetch_players()` → retrieve player list
   * `check_for_active_game(player_id)` → check if game started
   * `send_final_results(game_id, elapsed, dist)` → send results

2. **Hardware Setup**

   * Initialize OLED (with buttons), potentiometers, and NeoPixel

3. **Menu System**

   * Displays players
   * Auto-refresh every 3 seconds

4. **Game Loop / State Machine**

   * `STATE_SELECT` → player selection
   * `STATE_READY` → wait for game
   * `STATE_PLAY` → active game and movement tracking
   * `STATE_END` → end screen and replay options

---

## How to Use

1. Power ESP32
2. Wait for Wi-Fi connection
3. Player list appears
4. Select player using OLED buttons (UP/DOWN/OK integrated)
5. Press OK
6. Start a game from the mobile/web app
7. Move potentiometers to reach target on OLED
8. Results are sent automatically to backend

---

## Troubleshooting

* **ESP32 not connecting**: check Wi-Fi credentials, ensure 2.4GHz network
* **No players displayed**: check backend running, IP address, firewall
* **Game never starts**: mobile app must initiate a game, ensure correct player selected
* **OLED not displaying**: verify `sh1107.py` driver is on the ESP32

---

## Safe Modifications

* Wi-Fi SSID & password
* `API_BASE` IP
* Player refresh interval
* Screen text

Do **not** change GPIO pins without rewiring hardware.

---

## Notes

* Designed to be modular and backend-driven
* Can be extended with sounds, multiple players, or real-time updates using WebSockets

---

**End of README**
