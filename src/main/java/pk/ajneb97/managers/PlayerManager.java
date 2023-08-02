package pk.ajneb97.managers;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import pk.ajneb97.PlayerKits;
import pk.ajneb97.models.PlayerData;
import pk.ajneb97.models.PlayerKit;

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

        player.sendMessage(String.valueOf(players.contains(player.getUniqueId().toString())));

        if (!players.contains(player.getUniqueId().toString())) {
            // gets or create a new playerdata object
            getOrCreatePlayer(player);
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
        boolean anyMatch = playerDataMap
                .keySet()
                .stream()
                .anyMatch(uuid -> uuid == player.getUniqueId());

        if (anyMatch) {
            return playerDataMap.get(player.getUniqueId());
        }

        return new PlayerData(player);
    }
}
