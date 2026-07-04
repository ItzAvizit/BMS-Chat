package net.bms.chatmanager.manager;

import net.bms.chatmanager.BMSChatManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChannelManager {

    private final ConfigManager configManager;
    private final Map<String, ChatChannel> channels = new HashMap<>();
    private final Map<UUID, String> playerChannels = new HashMap<>();

    public ChannelManager(BMSChatManager plugin, ConfigManager configManager) {
        this.configManager = configManager;
        loadChannels();
    }

    public void loadChannels() {
        channels.clear();
        ConfigurationSection section = configManager.getMain().getConfig().getConfigurationSection("channels");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                ConfigurationSection chan = section.getConfigurationSection(key);
                if (chan != null) {
                    channels.put(key.toLowerCase(), new ChatChannel(
                            key,
                            chan.getString("prefix", ""),
                            chan.getString("format", "formats.default"),
                            chan.getString("permission", "")
                    ));
                }
            }
        }
    }

    public ChatChannel getChannel(String name) {
        return channels.get(name.toLowerCase());
    }
    
    public ChatChannel getChannelByPrefix(String prefix) {
        if (prefix == null || prefix.isEmpty()) return null;
        for (ChatChannel channel : channels.values()) {
            if (!channel.getPrefix().isEmpty() && prefix.startsWith(channel.getPrefix())) {
                return channel;
            }
        }
        return null;
    }

    public void setPlayerChannel(Player player, String channelName) {
        if (channelName == null) {
            playerChannels.remove(player.getUniqueId());
        } else {
            playerChannels.put(player.getUniqueId(), channelName.toLowerCase());
        }
    }

    public ChatChannel getPlayerChannel(Player player) {
        String chan = playerChannels.get(player.getUniqueId());
        if (chan != null && channels.containsKey(chan)) {
            return channels.get(chan);
        }
        return channels.get("global"); // Fallback
    }

    public Map<String, ChatChannel> getChannels() {
        return channels;
    }

    public static class ChatChannel {
        private final String name;
        private final String prefix;
        private final String format;
        private final String permission;

        public ChatChannel(String name, String prefix, String format, String permission) {
            this.name = name;
            this.prefix = prefix;
            this.format = format;
            this.permission = permission;
        }

        public String getName() { return name; }
        public String getPrefix() { return prefix; }
        public String getFormat() { return format; }
        public String getPermission() { return permission; }
    }
}
