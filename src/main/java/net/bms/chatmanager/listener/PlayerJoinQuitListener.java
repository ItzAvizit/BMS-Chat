package net.bms.chatmanager.listener;

import net.bms.chatmanager.BMSChatManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinQuitListener implements Listener {
    private final BMSChatManager plugin;

    public PlayerJoinQuitListener(BMSChatManager plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        String format = plugin.getConfigManager().getMain().getConfig().getString("messages.join_message");
        if (format != null && !format.isEmpty()) {
            Component msg = plugin.getFormatManager().formatMessage(event.getPlayer(), "", format);
            event.setJoinMessage(null);
            for (Player p : Bukkit.getOnlinePlayers()) {
                plugin.getAdventureManager().sendMessage(p, msg);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        String format = plugin.getConfigManager().getMain().getConfig().getString("messages.quit_message");
        if (format != null && !format.isEmpty()) {
            Component msg = plugin.getFormatManager().formatMessage(event.getPlayer(), "", format);
            event.setQuitMessage(null);
            for (Player p : Bukkit.getOnlinePlayers()) {
                plugin.getAdventureManager().sendMessage(p, msg);
            }
        }
    }
}
