package net.bms.chatmanager.manager;

import net.bms.chatmanager.BMSChatManager;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class FilterManager {
    
    private final ConfigManager configManager;
    private final Map<UUID, String> lastMessages = new HashMap<>();
    private final Map<UUID, Long> lastMessageTimes = new HashMap<>();

    public FilterManager(BMSChatManager plugin, ConfigManager configManager) {
        this.configManager = configManager;
    }

    public boolean isSpam(Player player, String message) {
        if (player.hasPermission("chatmanager.bypass.spam")) return false;

        boolean blockIdentical = configManager.getMain().getConfig().getBoolean("spam.prevent_identical", true);
        if (blockIdentical) {
            String lastMsg = lastMessages.get(player.getUniqueId());
            if (lastMsg != null && lastMsg.equalsIgnoreCase(message)) {
                return true;
            }
        }
        
        int maxCaps = configManager.getMain().getConfig().getInt("spam.max_caps_percentage", 60);
        int minLength = configManager.getMain().getConfig().getInt("spam.min_message_length_for_caps", 5);
        if (message.length() >= minLength) {
            long upperCount = message.chars().filter(Character::isUpperCase).count();
            if ((upperCount * 100 / message.length()) > maxCaps) {
                return true; // Or we can lowercase the message instead of blocking
            }
        }

        return false;
    }

    public String filterMessage(Player player, String message) {
        if (player.hasPermission("chatmanager.bypass.filter")) return message;

        List<String> blockedWords = configManager.getMain().getConfig().getStringList("filter.blocked_words");
        String replacement = configManager.getMain().getConfig().getString("filter.replace_with", "****");
        
        for (String word : blockedWords) {
            message = message.replaceAll("(?i)\\b" + Pattern.quote(word) + "\\b", replacement);
        }

        List<String> regexBlocked = configManager.getMain().getConfig().getStringList("regex_blocked");
        for (String regex : regexBlocked) {
            try {
                message = message.replaceAll(regex, replacement);
            } catch (Exception e) {
                // Ignore invalid regex
            }
        }

        return message;
    }
    
    public boolean containsAd(Player player, String message) {
        if (player.hasPermission("chatmanager.bypass.ads")) return false;
        
        boolean blockDomains = configManager.getMain().getConfig().getBoolean("ads.block_domains", true);
        boolean blockIps = configManager.getMain().getConfig().getBoolean("ads.block_ips", true);
        List<String> whitelist = configManager.getMain().getConfig().getStringList("ads.whitelist");
        
        String ipPattern = "\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b";
        String domainPattern = "\\b[a-zA-Z0-9\\-]+(?:\\s*\\.\\s*|\\s*\\(dot\\)\\s*|\\s+dot\\s+)(com|net|org|io|gg|co|uk|me|ru|de)\\b";
        
        if (blockIps && message.matches(".*" + ipPattern + ".*")) return true;
        
        if (blockDomains) {
            java.util.regex.Matcher m = Pattern.compile(domainPattern, Pattern.CASE_INSENSITIVE).matcher(message);
            while (m.find()) {
                String domain = m.group().toLowerCase();
                boolean whitelisted = false;
                for (String w : whitelist) {
                    if (domain.contains(w.toLowerCase())) {
                        whitelisted = true;
                        break;
                    }
                }
                if (!whitelisted) return true;
            }
        }
        return false;
    }

    public void logMessage(Player player, String message) {
        lastMessages.put(player.getUniqueId(), message);
        lastMessageTimes.put(player.getUniqueId(), System.currentTimeMillis());
    }
}
