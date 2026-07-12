package net.bms.chatmanager.manager;

import net.bms.chatmanager.BMSChatManager;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {
    
    private final ConfigManager configManager;
    private final Map<UUID, Long> lastChatTimes = new HashMap<>();
    
    public CooldownManager(BMSChatManager plugin, ConfigManager configManager) {
        this.configManager = configManager;
    }

    private int globalSlowchat = 0;

    public void setGlobalSlowchat(int seconds) {
        this.globalSlowchat = seconds;
    }

    public int getGlobalSlowchat() {
        return this.globalSlowchat;
    }

    public int getCooldownForPlayer(Player player) {
        if (!configManager.getMain().getConfig().getBoolean("cooldowns.enabled", true)) return 0;
        if (player == null) return 0; // Discord users don't have in-game cooldowns applied here

        if (player.hasPermission(configManager.getMain().getConfig().getString("permissions.bypasscooldown", "chatmanager.bypasscooldown"))) {
            return 0;
        }

        int defaultCooldown = configManager.getMain().getConfig().getInt("cooldowns.default", 3);
        int vipCooldown = configManager.getMain().getConfig().getInt("cooldowns.vip", 1);
        
        int bestCooldown = defaultCooldown;
        if (player.hasPermission("chatmanager.vip")) {
            bestCooldown = vipCooldown;
        }
        
        // If slowchat is active, it overrides normal cooldowns unless bypassed
        if (globalSlowchat > 0) {
            return globalSlowchat;
        }
        
        return bestCooldown;
    }

    public long getRemainingCooldown(Player player) {
        if (player == null) return 0;

        int cd = getCooldownForPlayer(player);
        if (cd <= 0) return 0;

        long lastTime = lastChatTimes.getOrDefault(player.getUniqueId(), 0L);
        long timeSince = (System.currentTimeMillis() - lastTime) / 1000;
        
        if (timeSince < cd) {
            return cd - timeSince;
        }
        return 0;
    }

    public void updateChatTime(Player player) {
        if (player != null) {
            lastChatTimes.put(player.getUniqueId(), System.currentTimeMillis());
        }
    }
}
