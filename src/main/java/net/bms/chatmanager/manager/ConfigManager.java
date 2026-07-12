package net.bms.chatmanager.manager;

import net.bms.chatmanager.util.ConfigWrapper;
import org.bukkit.plugin.Plugin;

public class ConfigManager {

    private final Plugin plugin;
    
    private ConfigWrapper mainConfig;
    private ConfigWrapper messagesConfig;
    private ConfigWrapper swearWordsConfig;
    private ConfigWrapper announcementsConfig;

    public ConfigManager(Plugin plugin) {
        this.plugin = plugin;
        loadAll();
    }

    public void loadAll() {
        mainConfig = new ConfigWrapper(plugin, "config.yml");
        mainConfig.reloadConfig();

        messagesConfig = new ConfigWrapper(plugin, "messages.yml");
        messagesConfig.reloadConfig();

        swearWordsConfig = new ConfigWrapper(plugin, "swear_words.yml");
        swearWordsConfig.reloadConfig();

        announcementsConfig = new ConfigWrapper(plugin, "announcements.yml");
        announcementsConfig.reloadConfig();
    }

    public ConfigWrapper getMain() { return mainConfig; }
    public ConfigWrapper getMessages() { return messagesConfig; }
    public ConfigWrapper getSwearWords() { return swearWordsConfig; }
    public ConfigWrapper getAnnouncements() { return announcementsConfig; }
}
