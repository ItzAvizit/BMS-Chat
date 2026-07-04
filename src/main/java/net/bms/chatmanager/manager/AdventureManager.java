package net.bms.chatmanager.manager;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.Plugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.kyori.adventure.text.Component;

public class AdventureManager {

    private BukkitAudiences audiences;

    public AdventureManager(Plugin plugin) {
        this.audiences = BukkitAudiences.create(plugin);
    }

    public BukkitAudiences getAudiences() {
        return audiences;
    }

    public void sendMessage(CommandSender sender, Component component) {
        audiences.sender(sender).sendMessage(component);
    }

    public void sendMessage(Player player, Component component) {
        audiences.player(player).sendMessage(component);
    }

    public void close() {
        if (audiences != null) {
            audiences.close();
            audiences = null;
        }
    }
}
