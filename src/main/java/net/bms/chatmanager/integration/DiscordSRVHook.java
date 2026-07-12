package net.bms.chatmanager.integration;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessagePreProcessEvent;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import net.bms.chatmanager.BMSChatManager;
import net.bms.chatmanager.manager.ChannelManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DiscordSRVHook {

    private final BMSChatManager plugin;
    private boolean enabled;

    public DiscordSRVHook(BMSChatManager plugin) {
        this.plugin = plugin;
        this.enabled = Bukkit.getPluginManager().isPluginEnabled("DiscordSRV");

        if (this.enabled) {
            DiscordSRV.api.subscribe(this);
            plugin.getLogger().info("Successfully hooked into DiscordSRV!");
        }
    }

    public void unregister() {
        if (enabled) {
            DiscordSRV.api.unsubscribe(this);
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    // Process messages coming from Discord to Minecraft
    @Subscribe
    public void onDiscordChat(DiscordGuildMessagePreProcessEvent event) {
        if (event.getAuthor().isBot()) return;

        // Apply our chat filter to the Discord message before it goes to MC
        if (plugin.getConfigManager().getMain().getConfig().getBoolean("filter.enabled", true)) {
            String originalMessage = event.getMessage().getContentDisplay();
            // We can pass a null player for discord users for filtering (or handle safely in FilterManager)
            String filteredMessage = plugin.getFilterManager().filterMessage(null, originalMessage);

            // Update the message if it was filtered
            // Since we can't easily modify the discord message content directly in this pre-process event 
            // without complex reflection or dropping it and recreating, we'll cancel if it contains bad words
            if (!originalMessage.equals(filteredMessage)) {
                event.setCancelled(true);
                // Optionally log to discord or webhook that the message was blocked
                plugin.getWebhookManager().sendEventEmbed("Message Blocked", "A message from " + event.getAuthor().getName() + " was blocked by the profanity filter.", 16711680); // Red
            }
        }
    }

    // Process messages from Minecraft to Discord
    public void sendToDiscord(Player player, String message, ChannelManager.ChatChannel channel) {
        if (!enabled) return;
        if (!plugin.getConfigManager().getMain().getConfig().getBoolean("discord.sync_chat", true)) return;

        // Find the mapped Discord channel for this in-game channel
        String discordChannelName = plugin.getConfigManager().getMain().getConfig().getString("discord.channels." + channel.getName().toLowerCase(), "");
        if (discordChannelName.isEmpty()) return;

        TextChannel textChannel = DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(discordChannelName);
        if (textChannel != null) {
            // Strip minimessage tags and colors for discord plain text
            String plainMessage = message.replaceAll("<[^>]+>", "").replaceAll("&[0-9a-fk-or]", "").replaceAll("&#[a-f0-9]{6}", "");
            
            // Or send via webhook for rich embeds
            plugin.getWebhookManager().sendChatEmbed(player, plainMessage, channel.getName());
            
            // Also send plain text via DiscordSRV directly so it appears native
            DiscordSRV.getPlugin().processChatMessage(player, plainMessage, discordChannelName, false);
        } else {
            plugin.getLogger().warning("Could not find DiscordSRV destination channel for: " + discordChannelName + ". Is it configured in DiscordSRV's config.yml?");
        }
    }
}
