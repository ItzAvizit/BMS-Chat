<div align="center">
  <h1>💬 BMS-ChatManager</h1>
  <p>A lightweight, modern, and powerful all-in-one chat management plugin for Minecraft servers!</p>
</div>

---

## ✨ Features
* **Modern Formatting:** Supports standard color codes (`&a`) as well as advanced MiniMessage tags (RGB Gradients, Hover text, clickable links).
* **Smart Mentions:** Pings players when they are tagged in chat (e.g. `@PlayerName`) and plays a configurable notification sound.
* **Custom Channels:** Set up private staff channels, admin channels, or custom VIP channels!
* **Robust Chat Filters:** Automatically blocks swear words, domain IP advertisements, and spam (with duplicate and caps prevention).
* **Interactive Help Menu:** Players can use a sleek, clickable hover-menu to view plugin commands.
* **Global Slow Mode:** Easily throttle chat speed for all players during busy server events.
* **Private Messaging:** Built in `/msg`, `/reply`, and `/ignore` system!

---

## 🚀 Installation

### Prerequisites
* A Minecraft server running **Paper / Purpur / Spigot** (1.21.1+)
* **Java 21** or higher.
* *Optional but recommended:* **LuckPerms** (for `%luckperms_prefix%`) and **PlaceholderAPI**.

### Step-by-Step Setup
1. **Download or Compile the Plugin:**
   * If you have the source code, open your terminal in the project folder and run:
     ```bash
     ./gradlew.bat build shadowJar
     ```
   * Grab the compiled file from `build/libs/BMS-ChatManager.jar`.
2. **Install:** Drag and drop `BMS-ChatManager.jar` into your server's `plugins/` folder.
3. **Restart:** Start or restart your server.
4. **Configure:** A new `config.yml` file will generate in `plugins/BMS-ChatManager/`. Customize your chat formats, channels, and filtered words here!
5. **Reload:** Type `/chatmanager reload` in-game to apply any changes made to the config.

---

## 📜 Commands & Permissions

### General Commands
| Command | Description | Permission |
| :--- | :--- | :--- |
| `/chatmanager` | View the interactive help menu | *None* |
| `/chatmanager reload` | Reloads the configuration file | `chatmanager.reload` |
| `/msg <player> <msg>` | Send a private message to a player | *None* |
| `/reply <msg>` | Reply to the last person who messaged you | *None* |
| `/ignore <player>` | Ignore a player's messages | *None* |
| `/unignore <player>` | Unignore a player | *None* |

### Admin & Staff Commands
| Command | Description | Permission |
| :--- | :--- | :--- |
| `/clearchat` (or `/cc`) | Clears the global chat | `chatmanager.admin` |
| `/mutechat` | Mutes/Unmutes the global chat entirely | `chatmanager.admin` |
| `/slowchat <seconds>` | Adds a delay between player messages | `chatmanager.admin` |
| `/unslowchat` | Removes the chat delay | `chatmanager.admin` |
| `/announce <msg>` | Sends a global server broadcast | `chatmanager.admin` |
| `/staffchat` (or `/sc`) | Toggles the Staff-only channel | `chatmanager.staff` |
| `/adminchat` (or `/ac`) | Toggles the Admin-only channel | `chatmanager.admin` |

### Bypass Permissions
* `chatmanager.bypassmute` - Speak while chat is muted.
* `chatmanager.bypasscooldown` - Bypass the slowchat cooldown.
* `chatmanager.bypass.spam` - Bypass the anti-spam and caps filter.
* `chatmanager.bypass.filter` - Bypass the profanity filter.
* `chatmanager.bypass.ads` - Bypass the anti-advertising filter.

---

## 🎨 Chat Formatting (MiniMessage)

This plugin fully supports modern formatting. Instead of being limited to standard colors like `&a` or `&c`, you can use rich formatting!

**Examples:**
* `<red>This is red text!</red>`
* `<bold><gradient:#00c6ff:#0072ff>This is a custom blue gradient!</gradient></bold>`
* `<hover:show_text:'<gray>Hello!</gray>'>Hover over me!</hover>`

*Requires the permissions: `chatmanager.color`, `chatmanager.hex`, and `chatmanager.gradient` to use these in-game.*
