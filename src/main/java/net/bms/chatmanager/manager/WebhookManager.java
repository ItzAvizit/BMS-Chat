package net.bms.chatmanager.manager;

import net.bms.chatmanager.BMSChatManager;
import org.bukkit.entity.Player;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class WebhookManager {

    private final BMSChatManager plugin;
    private String webhookUrl;
    private boolean enabled;

    public WebhookManager(BMSChatManager plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        this.enabled = plugin.getConfigManager().getMain().getConfig().getBoolean("discord.enabled", false);
        this.webhookUrl = plugin.getConfigManager().getMain().getConfig().getString("discord.webhook_url", "");
    }

    public void sendChatEmbed(Player player, String message, String channelName) {
        if (!enabled || webhookUrl == null || webhookUrl.isEmpty()) {
            return;
        }

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                java.net.URL url = java.net.URI.create(webhookUrl).toURL();
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("User-Agent", "BMS-ChatManager");
                connection.setDoOutput(true);

                // Construct JSON payload manually to avoid heavy dependencies
                String jsonPayload = "{"
                        + "\"username\": \"" + player.getName() + " [" + channelName + "]\","
                        + "\"avatar_url\": \"https://crafatar.com/avatars/" + player.getUniqueId() + "?overlay\","
                        + "\"content\": \"" + escapeJson(message) + "\""
                        + "}";

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = connection.getResponseCode();
                if (responseCode < 200 || responseCode >= 300) {
                    plugin.getLogger().warning("Failed to send webhook to Discord! Response Code: " + responseCode);
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Exception while sending webhook: " + e.getMessage());
            }
        });
    }

    public void sendEventEmbed(String title, String description, int color) {
        if (!enabled || webhookUrl == null || webhookUrl.isEmpty()) {
            return;
        }

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                java.net.URL url = java.net.URI.create(webhookUrl).toURL();
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("User-Agent", "BMS-ChatManager");
                connection.setDoOutput(true);

                String jsonPayload = "{"
                        + "\"embeds\": [{"
                        + "\"title\": \"" + escapeJson(title) + "\","
                        + "\"description\": \"" + escapeJson(description) + "\","
                        + "\"color\": " + color
                        + "}]"
                        + "}";

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                connection.getResponseCode();
            } catch (Exception e) {
                plugin.getLogger().warning("Exception while sending event webhook: " + e.getMessage());
            }
        });
    }

    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\b", "\\b")
                   .replace("\f", "\\f")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}
