package net.bms.chatmanager.manager;

import net.bms.chatmanager.BMSChatManager;
import net.bms.chatmanager.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class AnnouncerManager {

    private final BMSChatManager plugin;
    private boolean enabled;
    private int interval;
    private String soundName;
    private List<String> messages;
    private int currentIndex = 0;
    private BukkitTask task;

    public AnnouncerManager(BMSChatManager plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        if (task != null) {
            task.cancel();
        }

        this.enabled = plugin.getConfigManager().getAnnouncements().getConfig().getBoolean("announcer.enabled", true);
        this.interval = plugin.getConfigManager().getAnnouncements().getConfig().getInt("announcer.interval", 300);
        this.soundName = plugin.getConfigManager().getAnnouncements().getConfig().getString("announcer.sound", "ENTITY_EXPERIENCE_ORB_PICKUP");
        this.messages = plugin.getConfigManager().getAnnouncements().getConfig().getStringList("announcer.messages");

        if (enabled && !messages.isEmpty() && interval > 0) {
            startTask();
        }
    }

    private void startTask() {
        task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            if (messages.isEmpty()) return;

            String message = messages.get(currentIndex);
            String prefix = plugin.getConfigManager().getMessages().getConfig().getString("messages.prefix", "");
            message = message.replace("<prefix>", prefix);

            net.kyori.adventure.text.Component component = CC.parse(message);

            for (Player player : Bukkit.getOnlinePlayers()) {
                plugin.getAdventureManager().sendMessage(player, component);
                try {
                    if (!soundName.isEmpty()) {
                        Sound sound = Sound.valueOf(soundName.toUpperCase());
                        player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
                    }
                } catch (IllegalArgumentException ignored) {
                }
            }

            currentIndex++;
            if (currentIndex >= messages.size()) {
                currentIndex = 0;
            }

        }, interval * 20L, interval * 20L);
    }
}
