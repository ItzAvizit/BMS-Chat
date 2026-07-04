package net.bms.chatmanager.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CC {
    
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final Pattern HEX_PATTERN = Pattern.compile("(?i)[&§]x([&§][0-9a-f]){6}");
    private static final Pattern HEX_STANDARD_PATTERN = Pattern.compile("(?i)&#([0-9a-f]{6})");
    
    public static String convertLegacyToMiniMessage(String text) {
        if (text == null) return null;
        
        // Convert BungeeCord hex format (&#RRGGBB)
        Matcher matcher2 = HEX_STANDARD_PATTERN.matcher(text);
        StringBuffer sb2 = new StringBuffer();
        while (matcher2.find()) {
            matcher2.appendReplacement(sb2, "<#" + matcher2.group(1) + ">");
        }
        matcher2.appendTail(sb2);
        text = sb2.toString();

        // Convert section symbol hex format (§x§R§R§G§G§B§B)
        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String hex = matcher.group().replaceAll("[&§xX]", "");
            matcher.appendReplacement(sb, "<#" + hex + ">");
        }
        matcher.appendTail(sb);
        text = sb.toString();
        
        text = text.replaceAll("(?i)[&§]0", "<black>")
                   .replaceAll("(?i)[&§]1", "<dark_blue>")
                   .replaceAll("(?i)[&§]2", "<dark_green>")
                   .replaceAll("(?i)[&§]3", "<dark_aqua>")
                   .replaceAll("(?i)[&§]4", "<dark_red>")
                   .replaceAll("(?i)[&§]5", "<dark_purple>")
                   .replaceAll("(?i)[&§]6", "<gold>")
                   .replaceAll("(?i)[&§]7", "<gray>")
                   .replaceAll("(?i)[&§]8", "<dark_gray>")
                   .replaceAll("(?i)[&§]9", "<blue>")
                   .replaceAll("(?i)[&§]a", "<green>")
                   .replaceAll("(?i)[&§]b", "<aqua>")
                   .replaceAll("(?i)[&§]c", "<red>")
                   .replaceAll("(?i)[&§]d", "<light_purple>")
                   .replaceAll("(?i)[&§]e", "<yellow>")
                   .replaceAll("(?i)[&§]f", "<white>")
                   .replaceAll("(?i)[&§]l", "<bold>")
                   .replaceAll("(?i)[&§]m", "<strikethrough>")
                   .replaceAll("(?i)[&§]n", "<underlined>")
                   .replaceAll("(?i)[&§]o", "<italic>")
                   .replaceAll("(?i)[&§]r", "<reset>");
                   
        // Strip any remaining section symbols so MiniMessage doesn't crash
        text = text.replace("§", "");
        
        return text;
    }

    /**
     * Parses a string containing either MiniMessage tags or legacy '&'/'§' codes.
     */
    public static Component parse(String text) {
        if (text == null) return Component.empty();
        return MINI_MESSAGE.deserialize(convertLegacyToMiniMessage(text));
    }
    
    public static String stripColor(String text) {
        return MINI_MESSAGE.stripTags(text);
    }
}
