package net.bms.chatmanager.manager;

import net.bms.chatmanager.BMSChatManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MentionManager {
    private final BMSChatManager plugin;
    private final Pattern mentionPattern = Pattern.compile("@([a-zA-Z0-9_]{3,16})");

    public MentionManager(BMSChatManager plugin) {
        this.plugin = plugin;
    }

    public String processMentions(Player sender, String message, Set<Player> recipients) {
        if (!plugin.getConfigManager().getMain().getConfig().getBoolean("features.mentions_enabled", true)) return message;

        Matcher matcher = mentionPattern.matcher(message);
        StringBuffer sb = new StringBuffer();
        
        while (matcher.find()) {
            String name = matcher.group(1);
            Player target = Bukkit.getPlayerExact(name);
            if (target != null && recipients.contains(target)) {
                // Highlight - injecting minimessage tags
                matcher.appendReplacement(sb, "<yellow>@" + name + "</yellow>");
                // Play sound
                playMentionSound(target);
            } else {
                matcher.appendReplacement(sb, "@" + name);
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private void playMentionSound(Player player) {
        if (plugin.getConfigManager().getMain().getConfig().getBoolean("sounds.mentions.enabled", true)) {
            String soundName = plugin.getConfigManager().getMain().getConfig().getString("sounds.mentions.sound", "ENTITY_EXPERIENCE_ORB_PICKUP");
            try {
                player.playSound(player.getLocation(), Sound.valueOf(soundName), 
                    (float) plugin.getConfigManager().getMain().getConfig().getDouble("sounds.mentions.volume", 1.0), 
                    (float) plugin.getConfigManager().getMain().getConfig().getDouble("sounds.mentions.pitch", 1.0));
            } catch (Exception e) {
                // Ignore invalid sounds for legacy versions
            }
        }
    }
}
