package net.bms.chatmanager.integration;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class LuckPermsHook {

    private LuckPerms api;
    private final boolean enabled;

    public LuckPermsHook(Plugin plugin) {
        if (plugin.getServer().getPluginManager().getPlugin("LuckPerms") != null) {
            this.api = LuckPermsProvider.get();
            this.enabled = true;
            plugin.getLogger().info("Hooked into LuckPerms.");
        } else {
            this.enabled = false;
            plugin.getLogger().warning("LuckPerms not found! Group-based formats will not work properly.");
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getPrefix(Player player) {
        if (!enabled) return "";
        User user = api.getUserManager().getUser(player.getUniqueId());
        if (user == null) return "";
        String prefix = user.getCachedData().getMetaData().getPrefix();
        return prefix != null ? prefix : "";
    }

    public String getSuffix(Player player) {
        if (!enabled) return "";
        User user = api.getUserManager().getUser(player.getUniqueId());
        if (user == null) return "";
        String suffix = user.getCachedData().getMetaData().getSuffix();
        return suffix != null ? suffix : "";
    }

    public String getPrimaryGroup(Player player) {
        if (!enabled) return "default";
        User user = api.getUserManager().getUser(player.getUniqueId());
        if (user == null) return "default";
        return user.getPrimaryGroup();
    }
}
