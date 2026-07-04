package net.bms.chatmanager.manager;

import net.bms.chatmanager.BMSChatManager;
import net.bms.chatmanager.integration.LuckPermsHook;
import net.bms.chatmanager.integration.PlaceholderAPIHook;
import net.bms.chatmanager.util.CC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class FormatManager {

    private final ConfigManager configManager;
    private final LuckPermsHook luckPermsHook;
    private final PlaceholderAPIHook papiHook;

    public FormatManager(BMSChatManager plugin, ConfigManager configManager, LuckPermsHook luckPermsHook, PlaceholderAPIHook papiHook) {
        this.configManager = configManager;
        this.luckPermsHook = luckPermsHook;
        this.papiHook = papiHook;
    }

    public String getFormatString(Player player, String formatPath) {
        if (formatPath != null && formatPath.startsWith("formats.")) {
            String formatName = formatPath.substring(8);
            return configManager.getMain().getConfig().getString("formats." + formatName + ".format", "<player> <gray>»</gray> <message>");
        }
        
        if (formatPath != null && !formatPath.isEmpty() && !formatPath.startsWith("formats.")) {
            return formatPath;
        }

        ConfigurationSection formats = configManager.getMain().getConfig().getConfigurationSection("formats");
        if (formats == null) return "<player> <gray>»</gray> <message>";

        String bestFormat = null;
        int highestPriority = -1;

        for (String key : formats.getKeys(false)) {
            ConfigurationSection section = formats.getConfigurationSection(key);
            if (section == null) continue;

            String permission = section.getString("permission", "");
            int priority = section.getInt("priority", 0);

            if (permission.isEmpty() || player.hasPermission(permission)) {
                if (priority > highestPriority) {
                    highestPriority = priority;
                    bestFormat = section.getString("format");
                }
            }
        }

        return bestFormat != null ? bestFormat : "<player> <gray>»</gray> <message>";
    }

    public Component formatMessage(Player player, String message, String specificFormat) {
        String format = getFormatString(player, specificFormat);

        // Sanitize the message if player lacks permissions
        String colorPerm = configManager.getMain().getConfig().getString("permissions.color", "chatmanager.color");
        String hexPerm = configManager.getMain().getConfig().getString("permissions.hex", "chatmanager.hex");
        String gradientPerm = configManager.getMain().getConfig().getString("permissions.gradient", "chatmanager.gradient");

        if (!player.hasPermission(colorPerm)) {
            message = message.replaceAll("(?i)&[0-9a-fk-or]", "");
            message = message.replaceAll("(?i)<[a-z_]+>", "");
        }
        if (!player.hasPermission(hexPerm)) {
            message = message.replaceAll("(?i)&#[a-f0-9]{6}", "");
            message = message.replaceAll("(?i)<#[a-f0-9]{6}>", "");
        }
        if (!player.hasPermission(gradientPerm)) {
            message = message.replaceAll("(?i)<gradient.*?>.*?</gradient>", "$0"); // This regex would be complex, better to strip tags.
            // Simplified stripping:
            if (!player.hasPermission(hexPerm)) {
                message = message.replaceAll("<gradient([^>]+)>", "");
                message = message.replaceAll("</gradient>", "");
            }
        }

        // Apply placeholders to the format
        if (luckPermsHook.isEnabled()) {
            format = format.replace("%luckperms_prefix%", luckPermsHook.getPrefix(player));
            format = format.replace("%luckperms_suffix%", luckPermsHook.getSuffix(player));
        }

        if (papiHook.isEnabled()) {
            format = papiHook.setPlaceholders(player, format);
        }

        format = format.replace("%player_name%", player.getName());

        // We replace <message> with a placeholder to avoid injection
        format = format.replace("<message>", "<message_placeholder>");
        format = format.replace("<player>", "<player_placeholder>");

        Component messageComponent = CC.parse(message);
        Component playerComponent = CC.parse(player.getName()); // Could include prefix later if needed

        // Re-parse the format string into a component, safely injecting the player and message
        String finalFormat = CC.convertLegacyToMiniMessage(format);
        
        // Actually, we use CC.parse to handle legacy colors in the format string, but MiniMessage resolver for placeholders
        return MiniMessage.miniMessage().deserialize(
                finalFormat, 
                Placeholder.component("message_placeholder", messageComponent),
                Placeholder.component("player_placeholder", playerComponent)
        );
    }
}
