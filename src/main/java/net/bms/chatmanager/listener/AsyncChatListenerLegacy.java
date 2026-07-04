package net.bms.chatmanager.listener;

import net.bms.chatmanager.BMSChatManager;
import net.bms.chatmanager.manager.*;
import net.bms.chatmanager.util.CC;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

public class AsyncChatListenerLegacy implements Listener {

    private final BMSChatManager plugin;

    public AsyncChatListenerLegacy(BMSChatManager plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        // 1. Mute Check
        if (plugin.isChatMuted() && !player.hasPermission("chatmanager.bypassmute")) {
            plugin.getAdventureManager().sendMessage(player, CC.parse(plugin.getConfigManager().getMain().getConfig().getString("messages.chat_muted")));
            event.setCancelled(true);
            return;
        }

        // 2. Cooldown Check
        long cd = plugin.getCooldownManager().getRemainingCooldown(player);
        if (cd > 0) {
            String msg = plugin.getConfigManager().getMain().getConfig().getString("messages.cooldown_active").replace("%time%", String.valueOf(cd));
            plugin.getAdventureManager().sendMessage(player, CC.parse(msg));
            event.setCancelled(true);
            return;
        }

        // 3. Spam & Ad Check
        if (plugin.getFilterManager().containsAd(player, message)) {
            plugin.getAdventureManager().sendMessage(player, CC.parse("<red>Advertising is not allowed!</red>"));
            event.setCancelled(true);
            return;
        }
        if (plugin.getFilterManager().isSpam(player, message)) {
            plugin.getAdventureManager().sendMessage(player, CC.parse("<red>Please do not spam!</red>"));
            event.setCancelled(true);
            return;
        }

        // Filter profanity
        message = plugin.getFilterManager().filterMessage(player, message);
        plugin.getFilterManager().logMessage(player, message);
        plugin.getCooldownManager().updateChatTime(player);

        // 4. Channel Check
        ChannelManager.ChatChannel channel = plugin.getChannelManager().getPlayerChannel(player);
        
        if (!channel.getName().equalsIgnoreCase("global")) {
             // Only send to people with permission
             String perm = channel.getPermission();
             event.getRecipients().removeIf(p -> !p.hasPermission(perm));
        }

        // Remove ignored players from recipients
        event.getRecipients().removeIf(p -> plugin.getPlayerDataManager().isIgnoring(p, player));

        // Process Mentions
        message = plugin.getMentionManager().processMentions(player, message, event.getRecipients());

        // Format message
        Component finalMessage = plugin.getFormatManager().formatMessage(player, message, channel.getFormat());

        // Cancel standard chat and send via Adventure
        event.setCancelled(true);

        for (Player recipient : event.getRecipients()) {
            plugin.getAdventureManager().sendMessage(recipient, finalMessage);
        }

        // Send to console
        plugin.getAdventureManager().sendMessage(Bukkit.getConsoleSender(), finalMessage);

        // Send to spies if channel is private (staff/admin)
        if (!channel.getName().equalsIgnoreCase("global")) {
            for (UUID spyId : plugin.getPlayerDataManager().getSpyingPlayers()) {
                Player spy = Bukkit.getPlayer(spyId);
                if (spy != null && !event.getRecipients().contains(spy)) {
                    plugin.getAdventureManager().sendMessage(spy, CC.parse("<gray>[Spy]</gray> ").append(finalMessage));
                }
            }
        }
    }
}
