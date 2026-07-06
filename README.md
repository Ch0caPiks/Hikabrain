# 🌉 Hikabrain / TheBridge - Minecraft Plugin

![Java](https://img.shields.io/badge/Language-Java-orange.svg)
![Minecraft](https://img.shields.io/badge/Minecraft-Plugin-brightgreen.svg)
![Open Source](https://img.shields.io/badge/Open%20Source-Yes-blue.svg)

## Why?

**Hikabrain** on Funcraft, and later **The Bridge** on Hypixel, were my favorite Minecraft minigames and contributed to my start in plugin development. This led me to realize my initial idea from when I first learned JAVA.
The main goal of this project is to **demonstrate my development skills** while recreating a game I particularly love, and doing so without using AI to write the code in order to distance myself from a certain dependency.

> I would like to clarify that I completed this project *alone*, with **no affiliation** to the Funcraft or Hypixel servers, and therefore the concept is their property. This project was made out of pure passion, with no intent of monetization. It is completely open-source and everyone is free to use, modify, and share it.

---

## 🛠️ Map Installation and Configuration

For the plugin to load the map, it uses a schematic system.

1. Open the `schems/` folder at the root of the plugin directory.
2. Drag and drop your map in `.schem` format into it (for example `hika1.schem`).
3. Make sure to specify the exact name of this file in the `config.yml` configuration detailed below.

---

## ⚙️ Configuration (`config.yml`)

```yaml
game:
  minPlayers: 2
  maxPlayers: 2
  pointsToWin: 5
  timerHitDisplay: 1 // Display time for damage taken
  timerStart: 3 // How many seconds before the game starts
  mapSchem: "hika1.schem"

teams:
  - BLUE
  - RED

locations:
  - world: world
    x: 13.5
    y: 12.0
    z: 14.5
    yaw: -90.0
    pitch: 0.0
  - world: world
    x: 85.5
    y: 11.0
    z: 14.5
    yaw: 90.0
    pitch: 0.0
```
