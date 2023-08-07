package pk.ajneb97.listeners;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pk.ajneb97.PlayerKits;
import pk.ajneb97.managers.KitManager;
import pk.ajneb97.managers.PlayerManager;

public class PlayerListeners implements Listener {

    private final PlayerKits plugin;

    public PlayerListeners(PlayerKits plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        PlayerManager playerManager = plugin.getPlayerManager();

        giveFirstJoinKits(player);

        playerManager.loadPlayer(player);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerManager playerManager = plugin.getPlayerManager();

        playerManager.savePlayer(player);
        playerManager.removePlayer(player);
    }

    public void giveFirstJoinKits(Player jugador) {
        FileConfiguration kitConfig = plugin.getKits();
        KitManager kitManager = plugin.getKitManager();
        if (kitConfig.contains("Kits")) {
            for (String key : kitConfig.getConfigurationSection("Kits").getKeys(false)) {
                if (kitConfig.contains("Kits." + key + ".first_join") && kitConfig.getBoolean("Kits." + key + ".first_join")) {
                    kitManager.claimKit(jugador, key, false, false);
                }
            }
        }
    }
}
