**GraveChest**
A Minecraft plugin that creates a grave (chest) at a player’s death location, stores their items safely, and gives them a “Grave Compass” to locate it.

---

## Table of Contents

1. [Features](#features)
2. [Requirements](#requirements)
3. [Installation](#installation)
4. [Configuration](#configuration)
5. [Permissions](#permissions)
6. [Usage](#usage)
7. [Commands](#commands)
8. [Metrics & Dependencies](#metrics--dependencies)
9. [Contributing](#contributing)
10. [License](#license)

---

## Features

* On‐death grave chest placement (single or double) storing all dropped items.
* Automatically gives a “§6Grave Compass” pointing to the grave’s block coordinates.
* Prevents moving or dropping the Grave Compass (in‐inventory, shift‐click, drag, Q, etc.).
* Sends the player a chat message with exact world and X/Y/Z coordinates at death (or on respawn).
* Configurable chest type: auto (single/double based on drop count), forced single, or forced double.
* Option to name the chest (e.g., “PlayerName’s Grave”).
* Handles inventory overflow (drop overflow items on ground or keep them in chest).
* Configurable grave expiration: automatically removes the chest after a set number of minutes.
* Permission‐based usage.

---

## Requirements

* **Minecraft Server**: Paper 1.21 (or newer; tested on Paper 1.21)
* **Java**: Java 17+ (as required by modern Paper builds)
* **Other Plugins**: None (standalone)

---

## Installation

1. Download the latest `GraveChest.jar` from the [Releases](#) page (or compile it yourself).
2. Place `GraveChest.jar` into your server’s `plugins/` folder.
3. Start (or restart) the server to generate default configuration files.
4. Customize `config.yml` as desired (see [Configuration](#configuration) below).
5. Reload or restart the server again if you made changes to `config.yml`.

---

## Configuration

When you first run the plugin, a `config.yml` will be generated in `plugins/GraveChest/`. Below is a breakdown of each option and its default value:

```yaml
# GraveChest Configuration

# Only players with this permission can use the plugin’s features.
require-permission: true

# The type of chest to place at death.
# Options:
#   auto   → place a double if drop count > 27, otherwise single.
#   single → always place a single chest.
#   double → always place a double chest (if space allows).
chest-type: "auto"

# Whether to give each grave chest a custom name ("PlayerName's Grave").
use-custom-name: true

# When a chest’s inventory is full, should overflow items be dropped on the ground?
#   true  → drop overflow items at chest location.
#   false → keep all items inside the chest (may cause items to disappear if no space).
drop-overflow-items: true

# After how many minutes should a grave (chest) disappear?
#   0 → never expire (chest remains until manually broken).
#   >0 → number of minutes until chest is removed.
expire-time-minutes: 0
```

* **require-permission**

    * `true` → Only players with `gravechest.use` can trigger graves.
    * `false` → All players’ deaths spawn a grave.

* **chest-type**

    * `auto` → If the number of dropped items exceeds 27, attempts a double-chest (space permitting). Otherwise places a single chest.
    * `single` → Always places a single chest.
    * `double` → Always attempts a double chest (if there are adjacent air blocks). If not enough space, falls back to single.

* **use-custom-name**

    * `true` → Chest is named “PlayerName’s Grave” in GUI and on hover.
    * `false` → Chest has the default container name.

* **drop-overflow-items**

    * `true` → If too many items to fit in chest, leftover items are dropped on the ground.
    * `false` → All dropped items are forced into the chest inventory (may vanish if no space).

* **expire-time-minutes**

    * `0` → Chest never expires.
    * `N (minutes)` → Chest will be removed `N` minutes after creation.

---

## Permissions

| Permission          | Description                                | Default |
| ------------------- | ------------------------------------------ | ------- |
| `gravechest.use`    | Allows a player’s death to create a grave. | `true`  |
| `gravechest.bypass` | (Future use) Bypass certain restrictions.  | `false` |

> **Note**: By default, only players with `gravechest.use` (or OPs) will spawn graves on death if `require-permission: true`.

---

## Usage

1. **Death**

    * When a player with permission dies, their inventory is cleared from the drop list.
    * The plugin searches up to 5 blocks above the death location for a pair of empty blocks to place a double chest (if applicable). Otherwise, it places a single chest at the first available block.
    * The plugin transfers all dropped items into that chest’s inventory. If the chest is full and `drop-overflow-items: true`, any overflow will drop on the ground.
    * The player instantly receives a new “§6Grave Compass” in their inventory, which is locked (cannot be dropped, moved into chests, or otherwise lost).
    * The player sees a chat message:

      ```
      You died! Your grave has been placed at:
       World: world_name
       X: 123  Y: 64  Z: -45
      ```

2. **Finding the Grave**

    * Right‐clicking the Grave Compass will point to the grave’s lodestone location.
    * The compass cannot be placed into any container, chest, or dropped outside the player’s inventory.
    * If the player dies again before using the old compass, the old compass is removed from drops and is gone (so they never find an “old” grave).

3. **Grave Expiration**

    * If `expire-time-minutes` > 0, the chest (single or double) will be automatically removed (set to air) after that many minutes.
    * Once expired, items that were never taken will vanish when the chest is gone.

---

## Commands

Currently, GraveChest does not register any commands. All behavior is automatic on death. Future versions may include manual retrieve commands or admin tools.

---

## Metrics & Dependencies

* **Metrics**: The plugin does not collect any metrics or telemetry.
* **Dependencies**: Only the Paper API (1.21+). No external libraries are required.

---

## Contributing

1. Fork the repository.
2. Create a new branch (`git checkout -b feature/YourFeature`).
3. Make your changes, ensuring you follow the existing code style (Java 17+, Tab‐indented, Javadoc where appropriate).
4. Test thoroughly on Paper 1.21.
5. Submit a pull request with a clear description of your change.

Please open issues for bug reports or feature requests.

---

## License

This project is licensed under the Apache 2.0 License. See [`LICENSE`](LICENSE) for details.

---

*Thank you for using GraveChest! If you have any feedback or encounter issues, feel free to open an issue on the GitHub repository.*
