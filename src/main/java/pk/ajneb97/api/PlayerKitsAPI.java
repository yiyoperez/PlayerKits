package pk.ajneb97.api;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import pk.ajneb97.PlayerKits;
import pk.ajneb97.managers.PlayerManager;
import pk.ajneb97.models.PlayerData;
import pk.ajneb97.utils.Cooldown;

public class PlayerKitsAPI {

    private static PlayerKits plugin;

    public PlayerKitsAPI(PlayerKits plugin) {
        PlayerKitsAPI.plugin = plugin;
    }

    public static boolean hasCooldown(Player player, String kit) {
        PlayerManager playerManager = plugin.getPlayerManager();
        PlayerData playerData = playerManager.getOrCreatePlayer(player);

        return playerData.hasCooldown(kit);
    }

    //TODO
    public static String getCooldown(Player player, String kit) {
        PlayerManager playerManager = plugin.getPlayerManager();
        PlayerData playerData = playerManager.getOrCreatePlayer(player);

        Cooldown cooldown = playerData.getCooldown(kit);
        if (cooldown == null){
            return null;
        }

        return cooldown.getTimeLeftPlainSeconds();
    }

    public static String getNBTSeparationCharacter() {
        FileConfiguration config = plugin.getConfig();
        return config.getBoolean("nbt_alternative_data_save") ? "|" : ";";
    }
}
