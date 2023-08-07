package pk.ajneb97.util;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import pk.ajneb97.PlayerKits;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MessageHandler {

    private final PlayerKits plugin;
    private final PluginManager pluginManager;

    public MessageHandler(PlayerKits plugin) {
        this.plugin = plugin;
        this.pluginManager = plugin.getServer().getPluginManager();
    }

    public String intercept(CommandSender sender, String message, List<Placeholder> placeholders) {
        message = intercept(sender, message);

        // Replace placeholders.
        if (!placeholders.isEmpty()) {
            message = StringUtils.replace(message, placeholders);
        }

        return MessageUtils.translateColor(message);
    }

    public String intercept(CommandSender sender, String message) {
        // Replace PlaceholderAPI's placeholders if found.
        if (pluginManager.getPlugin("PlaceholderAPI") != null) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                message = PlaceholderAPI.setPlaceholders(player, message);
            }
        }

        // Replace prefix if any.
        if (message.contains("%prefix%")) {
            FileConfiguration messages = plugin.getMessages();
            String prefix = messages.getString("prefix");
            if (prefix != null) {
                if (!prefix.isEmpty()) {
                    message = StringUtils.replace(message, new Placeholder("%prefix%", prefix));
                }
            }
        }

        return MessageUtils.translateColor(message);
    }

    public String getRawMessage(String path) {
        FileConfiguration messages = plugin.getMessages();
        String message = messages.getString(path);
        if (message == null) {
            return String.format("Message was not found in path %s", path);
        }

        return message;
    }

    public String getRawMessage(String path, Placeholder... placeholders) {
        return getRawMessage(path, Arrays.asList(placeholders));
    }

    public String getRawMessage(String path, Object... placeholders) {
        return String.format(getRawMessage(path), placeholders);
    }

    public String getRawMessage(String path, List<Placeholder> placeholders) {
        return StringUtils.replace(getRawMessage(path), placeholders);
    }

    public String getMessage(CommandSender sender, String path, Placeholder... placeholders) {
        return getMessage(sender, path, Arrays.asList(placeholders));
    }

    public String getMessage(CommandSender sender, String path, List<Placeholder> placeholders) {
        String message = getRawMessage(path, placeholders);
        return intercept(sender, message, placeholders);
    }

    public List<String> getMessages(CommandSender sender, String path, List<Placeholder> placeholders) {
        FileConfiguration messages = plugin.getMessages();
        if (!messages.isList(path)) {
            return Collections.singletonList(getMessage(sender, path, placeholders));
        }

        List<String> list = new ArrayList<>();
        for (String message : messages.getStringList(path)) {
            list.add(intercept(sender, message, placeholders));
        }

        return list;
    }

    public void sendManualMessage(CommandSender sender, String message, Object... placeholders) {
        sender.sendMessage(String.format(intercept(sender, message), placeholders));
    }

    public void sendManualMessage(CommandSender sender, String message, Placeholder... placeholders) {
        this.sendManualMessage(sender, message, Arrays.asList(placeholders));
    }

    public void sendManualMessage(CommandSender sender, String message, List<Placeholder> placeholders) {
        sender.sendMessage(intercept(sender, message, placeholders));
    }

    public void sendMessage(CommandSender sender, String path, Placeholder... placeholders) {
        this.sendMessage(sender, path, Arrays.asList(placeholders));
    }

    public void sendMessage(CommandSender sender, String path, List<Placeholder> placeholders) {
        sender.sendMessage(getMessage(sender, path, placeholders));
    }

    public List<String> getRawStringList(String path, Placeholder... placeholders) {
        return getRawStringList(path, Arrays.asList(placeholders));
    }

    public List<String> getRawStringList(String path, List<Placeholder> placeholders) {
        FileConfiguration messages = plugin.getMessages();
        if (!messages.isList(path)) {
            return Collections.singletonList(getRawMessage(path, placeholders));
        }

        List<String> list = new ArrayList<>();
        for (String message : messages.getStringList(path)) {
            list.add(StringUtils.replace(message, placeholders));
        }

        return list;
    }

    public void sendListMessage(CommandSender sender, String path, Placeholder... placeholders) {
        this.sendListMessage(sender, path, Arrays.asList(placeholders));
    }

    public void sendListMessage(CommandSender sender, String path, List<Placeholder> placeholders) {
        getMessages(sender, path, placeholders).forEach(sender::sendMessage);
    }
}
