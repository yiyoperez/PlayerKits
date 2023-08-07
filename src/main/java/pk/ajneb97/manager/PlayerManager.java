package pk.ajneb97.manager;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import pk.ajneb97.PlayerKits;
import pk.ajneb97.model.PlayerData;
import pk.ajneb97.model.PlayerKit;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlayerManager {

    private final PlayerKits plugin;
    private final Map<UUID, PlayerData> playerDataMap = new HashMap<>();

    public PlayerManager(PlayerKits plugin) {
        this.plugin = plugin;
    }

    public void loadPlayer(Player player) {
        FileConfiguration players = plugin.getPlayers();

        // If player doesn't exists in players.yml
        if (!players.contains(player.getUniqueId().toString())) {
            // create a new playerdata object
            playerDataMap.putIfAbsent(player.getUniqueId(), new PlayerData(player));
            return;
        }

        // If it does exists, load data.
        playerDataMap.put(player.getUniqueId(), new PlayerData(players.getConfigurationSection(player.getUniqueId().toString()).getValues(false)));
        plugin.savePlayers();
        plugin.reloadPlayers();
    }


    public void saveServerPlayerData() {
        Bukkit.getOnlinePlayers().forEach(this::savePlayer);
    }

    public void savePlayer(Player player) {
        FileConfiguration players = plugin.getPlayers();

        PlayerData playerData = getOrCreatePlayer(player);
        players.set(player.getUniqueId().toString(), playerData.serialize());

        plugin.savePlayers();
        plugin.reloadPlayers();
    }

    public void removePlayer(Player player) {
        playerDataMap.remove(player.getUniqueId());
    }

    public PlayerKit getOrCreatePlayerKit(Player player, String kit) {
        PlayerData playerData = getOrCreatePlayer(player);
        Set<PlayerKit> kits = playerData.getKits();

        return kits.stream()
                .filter(data -> data.getName().equalsIgnoreCase(kit))
                .findFirst()
                .orElseGet(() -> {
                    PlayerKit newData = new PlayerKit(kit, false, false);
                    kits.add(newData);
                    return newData;
                });
    }

    public PlayerData getOrCreatePlayer(Player player) {
        return playerDataMap
                .values()
                .stream()
                .filter(data -> data.getUuid().equals(player.getUniqueId()))
                .findFirst()
                .orElseGet(() -> {
                    // If somehow player data doesn't exist add it into map.
                    return playerDataMap.putIfAbsent(player.getUniqueId(), new PlayerData(player));
                });
    }
}
