package net.bms.chatmanager.command;

import net.bms.chatmanager.BMSChatManager;
import net.bms.chatmanager.manager.ChannelManager.ChatChannel;
import net.bms.chatmanager.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import org.bukkit.command.TabCompleter;
import java.util.ArrayList;
import java.util.List;

public class ChatCommands implements CommandExecutor, TabCompleter {
    
    private final BMSChatManager plugin;

    public ChatCommands(BMSChatManager plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String cmd = command.getName().toLowerCase();
        
        switch (cmd) {
            case "chatmanager":
                if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                    if (!sender.hasPermission("chatmanager.reload")) {
                        plugin.getAdventureManager().sendMessage(sender, CC.parse(plugin.getConfigManager().getMain().getConfig().getString("messages.no_permission")));
                        return true;
                    }
                    plugin.getConfigManager().loadAll();
                    plugin.getChannelManager().loadChannels();
                    plugin.getAdventureManager().sendMessage(sender, CC.parse(plugin.getConfigManager().getMain().getConfig().getString("messages.reload_success")));
                    return true;
                }
                plugin.getAdventureManager().sendMessage(sender, CC.parse("\n<gradient:#00c6ff:#0072ff><bold>BMS-ChatManager Commands</bold></gradient> <gray>(v" + plugin.getDescription().getVersion() + ")</gray>"));
                plugin.getAdventureManager().sendMessage(sender, CC.parse("<gray><i>Hover for info, click to auto-fill!</i></gray>"));
                
                String[] helpCommands = {
                    "/chatmanager reload: Reloads the configuration.",
                    "/staffchat: Toggle staff chat.",
                    "/adminchat: Toggle admin chat.",
                    "/channel <name>: Switch chat channels.",
                    "/msg <player> <message>: Send a private message.",
                    "/reply <message>: Reply to a private message.",
                    "/ignore <player>: Ignore a player.",
                    "/unignore <player>: Unignore a player.",
                    "/clearchat: Clear the global chat.",
                    "/mutechat: Mute the global chat.",
                    "/unmutechat: Unmute the global chat.",
                    "/slowchat <seconds>: Slow down global chat.",
                    "/unslowchat: Remove slow mode.",
                    "/announce <message>: Send a global announcement."
                };
                
                for (String helpCmd : helpCommands) {
                    String[] parts = helpCmd.split(":", 2);
                    String cmdPart = parts[0];
                    String descPart = parts[1].trim();
                    String baseCmd = cmdPart.split(" ")[0];
                    
                    plugin.getAdventureManager().sendMessage(sender, CC.parse(
                        "  <dark_gray>»</dark_gray> <hover:show_text:'<gray>Click to use </gray><white>" + baseCmd + "</white>'><click:suggest_command:'" + baseCmd + " '><aqua>" + cmdPart + "</aqua></click></hover> <dark_gray>-</dark_gray> <gray>" + descPart + "</gray>"
                    ));
                }
                plugin.getAdventureManager().sendMessage(sender, CC.parse(""));
                return true;

            case "staffchat":
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    if (!p.hasPermission("chatmanager.staff")) {
                        plugin.getAdventureManager().sendMessage(p, CC.parse(plugin.getConfigManager().getMain().getConfig().getString("messages.no_permission")));
                        return true;
                    }
                    ChatChannel current = plugin.getChannelManager().getPlayerChannel(p);
                    if (current != null && current.getName().equalsIgnoreCase("staff")) {
                        plugin.getChannelManager().setPlayerChannel(p, "global");
                        plugin.getAdventureManager().sendMessage(p, CC.parse(plugin.getConfigManager().getMain().getConfig().getString("messages.channel_leave").replace("%channel%", "Staff")));
                    } else {
                        plugin.getChannelManager().setPlayerChannel(p, "staff");
                        plugin.getAdventureManager().sendMessage(p, CC.parse(plugin.getConfigManager().getMain().getConfig().getString("messages.channel_join").replace("%channel%", "Staff")));
                    }
                }
                return true;

            case "adminchat":
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    if (!p.hasPermission("chatmanager.admin")) {
                        plugin.getAdventureManager().sendMessage(p, CC.parse(plugin.getConfigManager().getMain().getConfig().getString("messages.no_permission")));
                        return true;
                    }
                    ChatChannel current = plugin.getChannelManager().getPlayerChannel(p);
                    if (current != null && current.getName().equalsIgnoreCase("admin")) {
                        plugin.getChannelManager().setPlayerChannel(p, "global");
                        plugin.getAdventureManager().sendMessage(p, CC.parse(plugin.getConfigManager().getMain().getConfig().getString("messages.channel_leave").replace("%channel%", "Admin")));
                    } else {
                        plugin.getChannelManager().setPlayerChannel(p, "admin");
                        plugin.getAdventureManager().sendMessage(p, CC.parse(plugin.getConfigManager().getMain().getConfig().getString("messages.channel_join").replace("%channel%", "Admin")));
                    }
                }
                return true;
                
            case "channel":
                if (sender instanceof Player && args.length > 0) {
                    Player p = (Player) sender;
                    ChatChannel target = plugin.getChannelManager().getChannel(args[0]);
                    if (target == null) {
                        plugin.getAdventureManager().sendMessage(p, CC.parse("<red>Channel not found.</red>"));
                        return true;
                    }
                    if (!target.getPermission().isEmpty() && !p.hasPermission(target.getPermission())) {
                        plugin.getAdventureManager().sendMessage(p, CC.parse(plugin.getConfigManager().getMain().getConfig().getString("messages.no_permission")));
                        return true;
                    }
                    plugin.getChannelManager().setPlayerChannel(p, target.getName());
                    plugin.getAdventureManager().sendMessage(p, CC.parse(plugin.getConfigManager().getMain().getConfig().getString("messages.channel_join").replace("%channel%", target.getName())));
                }
                return true;

            case "clearchat":
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (!p.hasPermission("chatmanager.bypassclear")) {
                        for (int i = 0; i < 100; i++) {
                            p.sendMessage("");
                        }
                    }
                }
                String msg = plugin.getConfigManager().getMain().getConfig().getString("messages.chat_cleared").replace("%player_name%", sender.getName());
                for (Player p : Bukkit.getOnlinePlayers()) {
                    plugin.getAdventureManager().sendMessage(p, CC.parse(msg));
                }
                return true;
                
            case "mutechat":
                plugin.setChatMuted(true);
                plugin.getAdventureManager().sendMessage(Bukkit.getConsoleSender(), CC.parse("<green>Chat has been muted by " + sender.getName() + ".</green>"));
                for (Player p : Bukkit.getOnlinePlayers()) {
                    plugin.getAdventureManager().sendMessage(p, CC.parse("<red>Global chat has been muted by " + sender.getName() + ".</red>"));
                }
                return true;

            case "unmutechat":
                plugin.setChatMuted(false);
                for (Player p : Bukkit.getOnlinePlayers()) {
                    plugin.getAdventureManager().sendMessage(p, CC.parse("<green>Global chat has been unmuted.</green>"));
                }
                return true;

            case "announce":
                if (args.length > 0) {
                    String announcement = String.join(" ", args);
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        plugin.getAdventureManager().sendMessage(p, CC.parse("<dark_red>[Announce] <white>" + announcement));
                    }
                }
                return true;

            case "msg":
                if (sender instanceof Player && args.length > 1) {
                    Player p = (Player) sender;
                    Player target = Bukkit.getPlayer(args[0]);
                    if (target == null) {
                        plugin.getAdventureManager().sendMessage(p, CC.parse(plugin.getConfigManager().getMain().getConfig().getString("messages.player_not_found")));
                        return true;
                    }
                    if (plugin.getPlayerDataManager().isIgnoring(target, p)) {
                        plugin.getAdventureManager().sendMessage(p, CC.parse("<red>This player is ignoring you.</red>"));
                        return true;
                    }
                    String[] msgArgs = new String[args.length - 1];
                    System.arraycopy(args, 1, msgArgs, 0, args.length - 1);
                    String pmsg = String.join(" ", msgArgs);
                    
                    plugin.getPlayerDataManager().setReplyTarget(p, target);
                    
                    String sFormat = plugin.getConfigManager().getMain().getConfig().getString("messages.msg_format_send")
                        .replace("%player%", target.getName())
                        .replace("%message%", pmsg);
                    String rFormat = plugin.getConfigManager().getMain().getConfig().getString("messages.msg_format_receive")
                        .replace("%player%", p.getName())
                        .replace("%message%", pmsg);
                        
                    plugin.getAdventureManager().sendMessage(p, CC.parse(sFormat));
                    plugin.getAdventureManager().sendMessage(target, CC.parse(rFormat));
                }
                return true;

            case "reply":
                if (sender instanceof Player && args.length > 0) {
                    Player p = (Player) sender;
                    UUID targetId = plugin.getPlayerDataManager().getReplyTarget(p);
                    if (targetId == null) {
                        plugin.getAdventureManager().sendMessage(p, CC.parse("<red>You have nobody to reply to.</red>"));
                        return true;
                    }
                    Player target = Bukkit.getPlayer(targetId);
                    if (target == null) {
                        plugin.getAdventureManager().sendMessage(p, CC.parse(plugin.getConfigManager().getMain().getConfig().getString("messages.player_not_found")));
                        return true;
                    }
                    
                    String pmsg = String.join(" ", args);
                    plugin.getPlayerDataManager().setReplyTarget(p, target);
                    
                    String sFormat = plugin.getConfigManager().getMain().getConfig().getString("messages.msg_format_send")
                        .replace("%player%", target.getName())
                        .replace("%message%", pmsg);
                    String rFormat = plugin.getConfigManager().getMain().getConfig().getString("messages.msg_format_receive")
                        .replace("%player%", p.getName())
                        .replace("%message%", pmsg);
                        
                    plugin.getAdventureManager().sendMessage(p, CC.parse(sFormat));
                    plugin.getAdventureManager().sendMessage(target, CC.parse(rFormat));
                }
                return true;
                
            case "ignore":
                if (sender instanceof Player && args.length > 0) {
                    Player p = (Player) sender;
                    Player target = Bukkit.getPlayer(args[0]);
                    if (target == null) {
                        plugin.getAdventureManager().sendMessage(p, CC.parse(plugin.getConfigManager().getMain().getConfig().getString("messages.player_not_found")));
                        return true;
                    }
                    if (target.equals(p)) {
                        plugin.getAdventureManager().sendMessage(p, CC.parse(plugin.getConfigManager().getMain().getConfig().getString("messages.cannot_ignore_self")));
                        return true;
                    }
                    plugin.getPlayerDataManager().ignorePlayer(p, target);
                    plugin.getAdventureManager().sendMessage(p, CC.parse(plugin.getConfigManager().getMain().getConfig().getString("messages.ignore_add").replace("%player%", target.getName())));
                }
                return true;
                
            case "unignore":
                if (sender instanceof Player && args.length > 0) {
                    Player p = (Player) sender;
                    Player target = Bukkit.getPlayer(args[0]);
                    if (target == null) {
                        plugin.getAdventureManager().sendMessage(p, CC.parse(plugin.getConfigManager().getMain().getConfig().getString("messages.player_not_found")));
                        return true;
                    }
                    plugin.getPlayerDataManager().unignorePlayer(p, target);
                    plugin.getAdventureManager().sendMessage(p, CC.parse(plugin.getConfigManager().getMain().getConfig().getString("messages.ignore_remove").replace("%player%", target.getName())));
                }
                return true;
            case "slowchat":
                if (!sender.hasPermission("chatmanager.admin")) {
                    plugin.getAdventureManager().sendMessage(sender, CC.parse(plugin.getConfigManager().getMain().getConfig().getString("messages.no_permission")));
                    return true;
                }
                if (args.length == 0) {
                    plugin.getAdventureManager().sendMessage(sender, CC.parse("<red>Usage: /slowchat <seconds></red>"));
                    return true;
                }
                try {
                    int delay = Integer.parseInt(args[0]);
                    plugin.getCooldownManager().setGlobalSlowchat(delay);
                    plugin.getAdventureManager().sendMessage(sender, CC.parse("<green>Global chat has been slowed to " + delay + " seconds.</green>"));
                } catch (NumberFormatException ex) {
                    plugin.getAdventureManager().sendMessage(sender, CC.parse("<red>Invalid number of seconds.</red>"));
                }
                return true;

            case "unslowchat":
                if (!sender.hasPermission("chatmanager.admin")) {
                    plugin.getAdventureManager().sendMessage(sender, CC.parse(plugin.getConfigManager().getMain().getConfig().getString("messages.no_permission")));
                    return true;
                }
                plugin.getCooldownManager().setGlobalSlowchat(0);
                plugin.getAdventureManager().sendMessage(sender, CC.parse("<green>Global chat slow mode has been removed.</green>"));
                return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (command.getName().equalsIgnoreCase("channel")) {
            if (args.length == 1) {
                String partial = args[0].toLowerCase();
                for (String ch : plugin.getChannelManager().getChannels().keySet()) {
                    if (ch.toLowerCase().startsWith(partial)) {
                        completions.add(ch);
                    }
                }
            }
        } else if (command.getName().equalsIgnoreCase("chatmanager")) {
            if (args.length == 1 && "reload".startsWith(args[0].toLowerCase())) {
                completions.add("reload");
            }
        }
        return completions;
    }
}
