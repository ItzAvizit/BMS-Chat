package net.bms.chatmanager.manager;

import org.bukkit.entity.Player;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

public class PlayerDataManager {
    
    // Simplistic storage in memory for now. In a real scenario, this would sync to YAML/SQL.
    private final Set<UUID> spyingPlayers = new HashSet<>();
    private final Set<UUID> socialSpies = new HashSet<>();
    private final Map<UUID, Set<UUID>> ignoredPlayers = new HashMap<>();
    private final Map<UUID, UUID> lastConversations = new HashMap<>();

    public PlayerDataManager() {}

    public void toggleSpy(Player player) {
        if (spyingPlayers.contains(player.getUniqueId())) {
            spyingPlayers.remove(player.getUniqueId());
        } else {
            spyingPlayers.add(player.getUniqueId());
        }
    }

    public boolean isSpying(Player player) {
        return spyingPlayers.contains(player.getUniqueId());
    }
    
    public Set<UUID> getSpyingPlayers() {
        return spyingPlayers;
    }

    public boolean toggleSocialSpy(Player player) {
        if (socialSpies.contains(player.getUniqueId())) {
            socialSpies.remove(player.getUniqueId());
            return false;
        } else {
            socialSpies.add(player.getUniqueId());
            return true;
        }
    }

    public boolean isSocialSpy(Player player) {
        return socialSpies.contains(player.getUniqueId());
    }
    
    public Set<UUID> getSocialSpies() {
        return socialSpies;
    }

    public void ignorePlayer(Player player, Player target) {
        ignoredPlayers.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>()).add(target.getUniqueId());
    }
    
    public void unignorePlayer(Player player, Player target) {
        if (ignoredPlayers.containsKey(player.getUniqueId())) {
            ignoredPlayers.get(player.getUniqueId()).remove(target.getUniqueId());
        }
    }

    public boolean isIgnoring(Player player, Player target) {
        return ignoredPlayers.containsKey(player.getUniqueId()) && ignoredPlayers.get(player.getUniqueId()).contains(target.getUniqueId());
    }

    public void setReplyTarget(Player player, Player target) {
        lastConversations.put(player.getUniqueId(), target.getUniqueId());
        lastConversations.put(target.getUniqueId(), player.getUniqueId());
    }

    public UUID getReplyTarget(Player player) {
        return lastConversations.get(player.getUniqueId());
    }
}
