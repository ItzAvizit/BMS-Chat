package net.bms.chatmanager.manager;

import net.bms.chatmanager.BMSChatManager;

public class GrammarManager {

    private final BMSChatManager plugin;
    private boolean enabled;
    private boolean capitalize;
    private boolean punctuation;

    public GrammarManager(BMSChatManager plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        this.enabled = plugin.getConfigManager().getMain().getConfig().getBoolean("grammar.enabled", true);
        this.capitalize = plugin.getConfigManager().getMain().getConfig().getBoolean("grammar.capitalize", true);
        this.punctuation = plugin.getConfigManager().getMain().getConfig().getBoolean("grammar.punctuation", true);
    }

    public String applyGrammar(String message) {
        if (!enabled || message == null || message.trim().isEmpty()) {
            return message;
        }

        String processed = message.trim();

        if (capitalize) {
            // Find the first letter to capitalize (skipping colors/tags ideally, but simple version for now)
            if (processed.length() > 0) {
                processed = processed.substring(0, 1).toUpperCase() + processed.substring(1);
            }
        }

        if (punctuation) {
            char lastChar = processed.charAt(processed.length() - 1);
            if (lastChar != '.' && lastChar != '!' && lastChar != '?') {
                processed += ".";
            }
        }

        return processed;
    }
}
