# Disable Villager Trade

[![Build](https://github.com/dodoflix/DisableVillagerTrade/actions/workflows/test.yml/badge.svg)](https://github.com/dodoflix/DisableVillagerTrade/actions/workflows/test.yml)
[![codecov](https://codecov.io/gh/dodoflix/DisableVillagerTrade/graph/badge.svg)](https://codecov.io/gh/dodoflix/DisableVillagerTrade)
[![GitHub Release](https://img.shields.io/github/v/release/dodoflix/DisableVillagerTrade?label=release)](https://github.com/dodoflix/DisableVillagerTrade/releases)
[![Modrinth Downloads](https://img.shields.io/modrinth/dt/disable-villager-trade?logo=modrinth&label=modrinth)](https://modrinth.com/plugin/disable-villager-trade)
[![License](https://img.shields.io/github/license/dodoflix/DisableVillagerTrade)](LICENSE)

Disable villager trade on your Minecraft server! Now supports **multiple platforms**.

## 🎮 Supported Platforms

| Platform | Minecraft Version | Status |
|----------|-------------------|--------|
| **Bukkit/Spigot/Paper** | 1.14 - 1.21.11+ | ✅ Full Support |
| **Fabric** | 1.21.11 | ✅ Full Support |
| **Forge** | 1.21.11 | ✅ Full Support |
| **NeoForge** | 1.21.11 | ✅ Full Support |
| **Quilt** | 1.21.11 | ✅ Use Fabric version |

> **Note:** Quilt is compatible with Fabric mods. Simply use the Fabric version on Quilt servers/clients.

## ✨ Features

- 🚫 Prevent players from trading with villagers
- 💬 Configurable message when trading is blocked
- 🔓 Bypass permission for staff members
- 🌍 Per-world/dimension exclusion configuration
- 👨‍🌾 Villagers with no profession (unemployed) can still be interacted with
- 🔔 Automatic update checker with notifications
- ⚙️ Admin commands for management (Bukkit only)

## 📦 Installation

### Bukkit/Spigot/Paper
1. Download `DisableVillagerTrade-Bukkit-x.x.x.jar`
2. Place the JAR file in your server's `plugins` folder
3. Restart your server
4. Configure in `plugins/DisableVillagerTrade/config.yml`

### Fabric
1. Download `DisableVillagerTrade-Fabric-x.x.x.jar`
2. Ensure you have [Fabric Loader](https://fabricmc.net/) and [Fabric API](https://modrinth.com/mod/fabric-api) installed
3. Place the JAR file in your `.minecraft/mods` folder (or `mods` for servers)
4. Start the game/server
5. Configure in `config/disablevillagertrade.json`

### Forge
1. Download `DisableVillagerTrade-Forge-x.x.x.jar`
2. Ensure you have [Forge](https://files.minecraftforge.net/) installed
3. Place the JAR file in your `.minecraft/mods` folder (or `mods` for servers)
4. Start the game/server
5. Configure in `config/disablevillagertrade-server.toml`

### NeoForge
1. Download `DisableVillagerTrade-NeoForge-x.x.x.jar`
2. Ensure you have [NeoForge](https://neoforged.net/) installed
3. Place the JAR file in your `.minecraft/mods` folder (or `mods` for servers)
4. Start the game/server
5. Configure in `config/disablevillagertrade-server.toml`

## ⚙️ Configuration

### Bukkit (config.yml)
```yaml
# Message settings
message:
  enabled: true
  text: "&cYou can't trade with villagers on this server."

# Worlds where villager trading is ALLOWED (not blocked)
disabled-worlds:
  - example-world

# Update checker settings
update-checker:
  enabled: true
  check-interval: 24
  notify-on-join: true
```

### Fabric (disablevillagertrade.json)
```json
{
  "messageEnabled": true,
  "message": "§cYou can't trade with villagers on this server.",
  "disabledDimensions": [],
  "updateCheckerEnabled": true,
  "updateCheckInterval": 24,
  "notifyOnJoin": true
}
```

### Forge/NeoForge (disablevillagertrade-server.toml)
```toml
[message]
enabled = true
text = "§cYou can't trade with villagers on this server."

[dimensions]
disabled_dimensions = []

[update_checker]
enabled = true
check_interval = 24
notify_on_join = true
```

## 🔧 Commands (Bukkit Only)

| Command | Description | Permission |
|---------|-------------|------------|
| `/disabletrade reload` | Reload the plugin configuration | `disabletrade.admin` |
| `/disabletrade status` | Show plugin status and settings | `disabletrade.admin` |
| `/disabletrade toggle [player]` | Check bypass permission status | `disabletrade.admin` |
| `/disabletrade help` | Show help message | `disabletrade.admin` |

**Aliases:** `/dvt`, `/tradetoggle`

## 🔐 Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `disabletrade.admin` | Access to all admin commands | OP |
| `disabletrade.bypass` | Allows the player to bypass trade block | OP |
| `disabletrade.update` | Receives update notifications on join | OP |

### Permission Support by Platform

| Platform | Permission System |
|----------|-------------------|
| Bukkit | Native + LuckPerms, etc. |
| Fabric | [Fabric Permissions API](https://github.com/lucko/fabric-permissions-api) (optional), falls back to OP level |
| Forge | OP level 2+ |
| NeoForge | OP level 2+ |

## 📥 Downloads

| Platform | Link |
|----------|------|
| GitHub Releases | [Download](https://github.com/dodoflix/DisableVillagerTrade/releases) |
| Modrinth | [Download](https://modrinth.com/plugin/disable-villager-trade) |

## 🏗️ Building from Source

This is a composite multi-module Gradle project. Each platform has its own Gradle wrapper.

```bash
# Clone the repository
git clone https://github.com/dodoflix/DisableVillagerTrade.git
cd DisableVillagerTrade

# Build all platforms
cd bukkit && ./gradlew shadowJar --no-daemon && cd ..
cd fabric && ./gradlew build --no-daemon && cd ..
cd forge && ./gradlew shadowJar --no-daemon && cd ..
cd neoforge && ./gradlew build --no-daemon && cd ..
```

Build outputs will be in:
- `bukkit/build/libs/`
- `fabric/build/libs/`
- `forge/build/libs/`
- `neoforge/build/libs/`

## 🤝 Contributing

Contributions are welcome! Please read [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines on:

- Commit message conventions
- Pull request process
- Development setup

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
