package pk.ajneb97.api;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import pk.ajneb97.PlayerKits;
import pk.ajneb97.managers.JugadorManager;
import pk.ajneb97.utils.Utils;

public class PlayerKitsAPI {

    private static PlayerKits plugin;

    public PlayerKitsAPI(PlayerKits plugin) {
        this.plugin = plugin;
    }

    public static String getCooldown(Player player, String kit) {
        FileConfiguration config = plugin.getConfig();
        FileConfiguration configKits = plugin.getKits();
        JugadorManager jManager = plugin.getJugadorManager();
        String cooldown = Utils.getCooldown(kit, player, configKits, config, jManager);
        if (cooldown.equals("ready")) {
            return ChatColor.translateAlternateColorCodes('&', config.getString("Messages.cooldownPlaceholderReady"));
        } else if (cooldown.equals("no_existe")) {
            return null;
        } else {
            return cooldown;
        }
    }

    public static String getNBTSeparationCharacter() {
        FileConfiguration config = plugin.getConfig();
        return config.getBoolean("nbt_alternative_data_save") ? "|" : ";";
    }
}
