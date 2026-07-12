package net.bms.chatmanager.manager;

import net.bms.chatmanager.BMSChatManager;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogManager {

    private final BMSChatManager plugin;
    private boolean enabled;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public LogManager(BMSChatManager plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        this.enabled = plugin.getConfigManager().getMain().getConfig().getBoolean("logs.enabled", true);
        if (enabled) {
            File logDir = new File(plugin.getDataFolder(), "logs");
            if (!logDir.exists()) {
                logDir.mkdirs();
            }
        }
    }

    public void logChat(String type, Player sender, String message) {
        if (!enabled) return;
        
        String senderName = sender != null ? sender.getName() : "Discord";

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                LocalDateTime now = LocalDateTime.now();
                String fileName = "chat-" + dateFormatter.format(now) + ".txt";
                File logFile = new File(plugin.getDataFolder() + File.separator + "logs", fileName);

                if (!logFile.exists()) {
                    logFile.createNewFile();
                }

                try (PrintWriter writer = new PrintWriter(new FileWriter(logFile, true))) {
                    String logLine = "[" + timeFormatter.format(now) + "] [" + type + "] " + senderName + ": " + message;
                    writer.println(logLine);
                }
            } catch (IOException e) {
                plugin.getLogger().warning("Could not write to chat log file: " + e.getMessage());
            }
        });
    }
}
