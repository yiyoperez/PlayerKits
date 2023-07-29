package pk.ajneb97.managers;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import pk.ajneb97.PlayerKits;
import pk.ajneb97.models.PlayerData;
import pk.ajneb97.models.PlayerKit;
import pk.ajneb97.utils.PluginLogger;

import java.util.HashSet;
import java.util.Set;

public class PlayerManager {

    private final PlayerKits plugin;
    private Set<PlayerData> playerDataSet = new HashSet<>();

    public PlayerManager(PlayerKits plugin) {
        this.plugin = plugin;
    }

    public void loadPlayer(Player player) {
        FileConfiguration players = plugin.getPlayers();

        // If player doesn't exists in players.yml
        if (!players.contains(player.getUniqueId().toString())) {
            // gets or create a new playerdata object
            getOrCreatePlayer(player);
            return;
        }


        // If it does exists, load data.
        playerDataSet.add(new PlayerData(players.getConfigurationSection(player.getUniqueId().toString()).getValues(false)));
        plugin.savePlayers();
    }


    public void saveServerPlayerData() {
        Bukkit.getOnlinePlayers().forEach(this::savePlayer);
    }

    public void savePlayer(Player player) {
        FileConfiguration players = plugin.getPlayers();

        PlayerData playerData = getOrCreatePlayer(player);

        playerDataSet.remove(playerData);
        players.set(player.getUniqueId().toString(), playerData.serialize());
        plugin.savePlayers();
        plugin.reloadPlayers();
    }

    public PlayerKit getOrCreatePlayerKit(Player player, String kit) {
        Set<PlayerKit> kits = getOrCreatePlayer(player).getKits();

        return kits
                .stream()
                .filter(playerKit -> playerKit.getName().equalsIgnoreCase(kit))
                .findFirst()
                .orElseGet(() -> {
                    PlayerKit pk = new PlayerKit(kit, false, false);
                    kits.add(pk);
                    return pk;
                });
    }

    public PlayerData getOrCreatePlayer(Player player) {
        for (PlayerData data : playerDataSet) {
            if (data.getUuid().equals(player.getUniqueId())) {
                PluginLogger.info("Loading current data.");
                return data;
            }
        }

        PluginLogger.info("Creating a new player data.");
        return new PlayerData(player.getUniqueId(), player.getName());
    }
}
