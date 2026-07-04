package net.bms.chatmanager;

import net.bms.chatmanager.scheduler.BukkitSchedulerAdapter;
import net.bms.chatmanager.scheduler.FoliaSchedulerAdapter;
import net.bms.chatmanager.scheduler.SchedulerAdapter;
import net.bms.chatmanager.manager.*;
import net.bms.chatmanager.integration.*;
import org.bukkit.plugin.java.JavaPlugin;

public class BMSChatManager extends JavaPlugin {

    private static BMSChatManager instance;
    private SchedulerAdapter schedulerAdapter;
    private AdventureManager adventureManager;
    private ConfigManager configManager;
    private FormatManager formatManager;
    private ChannelManager channelManager;
    private FilterManager filterManager;
    private CooldownManager cooldownManager;
    private PlayerDataManager playerDataManager;
    private MentionManager mentionManager;

    private LuckPermsHook luckPermsHook;
    private PlaceholderAPIHook placeholderAPIHook;

    private boolean chatMuted = false;

    @Override
    public void onEnable() {
        instance = this;
        
        // Setup Scheduler Adapter
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            schedulerAdapter = new FoliaSchedulerAdapter(this);
            getLogger().info("Folia detected! Using FoliaSchedulerAdapter.");
        } catch (ClassNotFoundException e) {
            schedulerAdapter = new BukkitSchedulerAdapter(this);
            getLogger().info("Bukkit/Spigot/Paper detected! Using BukkitSchedulerAdapter.");
        }

        // Initialize Integrations
        luckPermsHook = new LuckPermsHook(this);
        placeholderAPIHook = new PlaceholderAPIHook(this);

        // Initialize Managers
        adventureManager = new AdventureManager(this);
        configManager = new ConfigManager(this);
        formatManager = new FormatManager(this, configManager, luckPermsHook, placeholderAPIHook);
        channelManager = new ChannelManager(this, configManager);
        filterManager = new FilterManager(this, configManager);
        cooldownManager = new CooldownManager(this, configManager);
        playerDataManager = new PlayerDataManager();
        mentionManager = new MentionManager(this);

        // Register Listeners
        getServer().getPluginManager().registerEvents(new net.bms.chatmanager.listener.AsyncChatListenerLegacy(this), this);
        getServer().getPluginManager().registerEvents(new net.bms.chatmanager.listener.PlayerJoinQuitListener(this), this);

        // Register Commands
        net.bms.chatmanager.command.ChatCommands cmdExecutor = new net.bms.chatmanager.command.ChatCommands(this);
        for (String cmd : new String[]{"chatmanager", "staffchat", "adminchat", "channel", "msg", "reply", "ignore", "unignore", "clearchat", "mutechat", "unmutechat", "slowchat", "unslowchat", "announce"}) {
            if (getCommand(cmd) != null) {
                getCommand(cmd).setExecutor(cmdExecutor);
                if (cmd.equals("channel") || cmd.equals("chatmanager")) {
                    getCommand(cmd).setTabCompleter(cmdExecutor);
                }
            }
        }
        
        getLogger().info("BMS-ChatManager has been enabled successfully!");
    }

    @Override
    public void onDisable() {
        if (schedulerAdapter != null) {
            schedulerAdapter.cancelTasks(this);
        }
        if (adventureManager != null) {
            adventureManager.close();
        }
        getLogger().info("BMS-ChatManager has been disabled!");
    }

    public static BMSChatManager getInstance() {
        return instance;
    }

    public SchedulerAdapter getSchedulerAdapter() { return schedulerAdapter; }
    public AdventureManager getAdventureManager() { return adventureManager; }
    public ConfigManager getConfigManager() { return configManager; }
    public FormatManager getFormatManager() { return formatManager; }
    public ChannelManager getChannelManager() { return channelManager; }
    public FilterManager getFilterManager() { return filterManager; }
    public CooldownManager getCooldownManager() { return cooldownManager; }
    public PlayerDataManager getPlayerDataManager() { return playerDataManager; }
    public MentionManager getMentionManager() { return mentionManager; }

    public boolean isChatMuted() { return chatMuted; }
    public void setChatMuted(boolean chatMuted) { this.chatMuted = chatMuted; }
}
