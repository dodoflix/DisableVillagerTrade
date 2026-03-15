# Disable Villager Trade

## Overview

DisableVillagerTrade is a lightweight mod/plugin crafted to block trading interactions exclusively with villagers while preserving interactions with other non-player characters (NPCs). This ensures a balanced and immersive gameplay experience on your server.

**Now available for multiple platforms!**

## 🎮 Supported Platforms

| Platform | Minecraft Version | Status |
|----------|-------------------|--------|
| **Bukkit/Spigot/Paper** | 1.14 - 1.21.11+ | ✅ Full Support |
| **Fabric** | 1.21.11 | ✅ Full Support |
| **Forge** | 1.21.11 | ✅ Full Support |
| **NeoForge** | 1.21.11 | ✅ Full Support |
| **Quilt** | 1.21.11 | ✅ Use Fabric version |

## ✨ Key Features

- **Custom Interaction Messages** - Personalize interaction messages to match your server's tone and style, enhancing player engagement.
- **Toggle Interaction Messages** - Easily enable or disable interaction messages according to server preferences.
- **World/Dimension-Specific Disablement** - Fine-tune gameplay dynamics by enabling or disabling the plugin on a per-world/dimension basis, granting precise control over trading functionalities.
- **Bypass Permission** - Allow staff to trade with `disabletrade.bypass` permission.
- **Smart Profession Detection** - Villagers with no profession (NONE) can still be interacted with.
- **Automatic Update Checker** - Get notified when a new version is available.
- **Admin Commands** - Manage the mod with simple commands on all platforms (`/dvt reload|status|help`).

## 📦 Installation

### Bukkit/Spigot/Paper
1. Download `DisableVillagerTrade-Bukkit-x.x.x.jar`
2. Place in your server's `plugins` folder
3. Restart your server
4. Configure in `plugins/DisableVillagerTrade/config.yml`

### Fabric
1. Download `DisableVillagerTrade-Fabric-x.x.x.jar`
2. Ensure you have Fabric Loader and Fabric API installed
3. Place in your `mods` folder
4. Configure in `config/disablevillagertrade.json`

### Forge / NeoForge
1. Download the appropriate JAR for your mod loader
2. Place in your `mods` folder
3. Configure in `config/disablevillagertrade-server.toml`

## ⚙️ Configuration (Bukkit)

```yaml
# Message settings
message:
  enabled: true
  text: "&cYou can't trade with villagers on this server."

# Worlds where trading is ALLOWED
disabled-worlds:
  - example-world
```

## 🎮 Commands

All platforms support the `/dvt` command. Bukkit additionally registers `/disabletrade` and `/tradetoggle` as aliases.

### Bukkit

| Command | Description | Permission |
|---------|-------------|------------|
| `/disabletrade reload` | Reload the plugin configuration | `disabletrade.admin` |
| `/disabletrade status` | Show plugin status and settings | `disabletrade.admin` |
| `/disabletrade toggle [player]` | Check bypass permission status | `disabletrade.admin` |
| `/disabletrade help` | Show help message | `disabletrade.admin` |

**Aliases:** `/dvt`, `/tradetoggle`

### Fabric / Forge / NeoForge

| Command | Description | Required Permission |
|---------|-------------|---------------------|
| `/dvt reload` | Reload the mod configuration | OP level 2 |
| `/dvt status` | Show mod status and settings | OP level 2 |
| `/dvt help` | Show help message | OP level 2 |

## 🔑 Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `disabletrade.admin` | Access to all admin commands | OP |
| `disabletrade.bypass` | Allows the player to bypass trade block | OP |
| `disabletrade.update` | Receives update notifications on join | OP |

> **Note:** On Fabric, permissions work with [Fabric Permissions API](https://github.com/lucko/fabric-permissions-api) or fall back to OP level 2+. On Forge/NeoForge, OP level 2+ is required.

## 💻 Supported Versions

- **Minecraft**: 1.14 - 1.21.11+ (varies by platform)
- **Java**: 21+

## 🔗 Links

- [Source Code](https://github.com/dodoflix/DisableVillagerTrade)
- [Issue Tracker](https://github.com/dodoflix/DisableVillagerTrade/issues)
- [Changelog](https://github.com/dodoflix/DisableVillagerTrade/releases)

## 📄 License

This project is open source under the [MIT License](https://github.com/dodoflix/DisableVillagerTrade/blob/master/LICENSE).
