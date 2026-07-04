package net.bms.chatmanager.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class ConfigWrapper {
    
    private final Plugin plugin;
    private final String fileName;
    private File file;
    private FileConfiguration config;

    public ConfigWrapper(Plugin plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
        this.file = new File(plugin.getDataFolder(), fileName);
        saveDefaultConfig();
    }

    public void reloadConfig() {
        if (!file.exists()) {
            saveDefaultConfig();
        }
        config = YamlConfiguration.loadConfiguration(file);
        
        try (Reader defConfigStream = new InputStreamReader(plugin.getResource(fileName), StandardCharsets.UTF_8)) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            config.setDefaults(defConfig);
        } catch (Exception e) {
            // Ignore if default doesn't exist in jar
        }
    }

    public FileConfiguration getConfig() {
        if (config == null) {
            reloadConfig();
        }
        return config;
    }

    public void saveConfig() {
        if (config == null || file == null) {
            return;
        }
        try {
            getConfig().save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save config to " + file);
        }
    }

    public void saveDefaultConfig() {
        if (!file.exists()) {
            plugin.saveResource(fileName, false);
        }
    }
}
