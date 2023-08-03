package pk.ajneb97.utils;

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

    public String intercept(CommandSender sender, String message) {
        if (pluginManager.getPlugin("PlaceholderAPI") != null) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                return MessageUtils.getMensajeColor(PlaceholderAPI.setPlaceholders(player, message));
            }
        }

        // Replace prefix if any.
        if (message.contains("%prefix%")) {
            FileConfiguration messages = plugin.getMessages();
            String prefix = messages.getString("prefix");
            if (prefix != null || !prefix.isEmpty()) {
                message = StringUtils.replace(message, new Placeholder("%prefix%", prefix));
            }
        }

        return MessageUtils.getMensajeColor(message);
    }

    public String getMessage(CommandSender sender, String path, List<Placeholder> placeholders) {
        FileConfiguration messages = plugin.getMessages();
        String message = messages.getString(path);
        if (message == null) {
            return intercept(sender, String.format("Message was not found in path %s", path));
        }

        return StringUtils.replace(intercept(sender, message), placeholders);
    }

    public List<String> getMessages(CommandSender sender, String path, List<Placeholder> placeholders) {
        FileConfiguration messages = plugin.getMessages();
        if (!messages.isList(path)) {
            return Collections.singletonList(getMessage(sender, path, placeholders));
        }

        List<String> list = new ArrayList<>();

        for (String message : messages.getStringList(path)) {
            list.add(StringUtils.replace(intercept(sender, message), placeholders));
        }

        return list;
    }

    public void sendMessage(CommandSender sender, String path, Placeholder... placeholders) {
        sender.sendMessage(getMessage(sender, path, Arrays.asList(placeholders)));
    }

    public void sendMessage(CommandSender sender, String path, List<Placeholder> placeholders) {
        sender.sendMessage(getMessage(sender, path, placeholders));
    }

    public void sendListMessage(CommandSender sender, String path, Placeholder... placeholders) {
        getMessages(sender, path, Arrays.asList(placeholders)).forEach(sender::sendMessage);
    }

    public void sendListMessage(CommandSender sender, String path, List<Placeholder> placeholders) {
        getMessages(sender, path, placeholders).forEach(sender::sendMessage);
    }

}
