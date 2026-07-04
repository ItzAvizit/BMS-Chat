package net.bms.chatmanager.manager;

import net.bms.chatmanager.util.ConfigWrapper;
import org.bukkit.plugin.Plugin;

public class ConfigManager {

    private final Plugin plugin;
    
    private ConfigWrapper mainConfig;

    public ConfigManager(Plugin plugin) {
        this.plugin = plugin;
        loadAll();
    }

    public void loadAll() {
        mainConfig = new ConfigWrapper(plugin, "config.yml");
        mainConfig.reloadConfig();
    }

    public ConfigWrapper getMain() { return mainConfig; }
}
