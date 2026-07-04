package net.bms.chatmanager.integration;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PlaceholderAPIHook {

    private final boolean enabled;

    public PlaceholderAPIHook(Plugin plugin) {
        if (plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            this.enabled = true;
            plugin.getLogger().info("Hooked into PlaceholderAPI.");
        } else {
            this.enabled = false;
            plugin.getLogger().warning("PlaceholderAPI not found! Placeholders will not be parsed.");
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String setPlaceholders(Player player, String text) {
        if (!enabled || text == null) return text;
        return PlaceholderAPI.setPlaceholders(player, text);
    }
}
