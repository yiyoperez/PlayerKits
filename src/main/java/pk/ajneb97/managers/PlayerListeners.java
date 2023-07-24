package pk.ajneb97.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pk.ajneb97.PlayerKits;
import pk.ajneb97.model.JugadorDatos;
import pk.ajneb97.mysql.MySQL;
import pk.ajneb97.mysql.MySQLJugadorCallback;

import java.util.ArrayList;

public class PlayerListeners implements Listener {

    private final PlayerKits plugin;

    public PlayerListeners(PlayerKits plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final JugadorManager manager = plugin.getJugadorManager();

        if (!MySQL.isEnabled(plugin.getConfig())) {
            JugadorDatos j = manager.getJugadorPorUUID(player.getUniqueId().toString());
            if (j != null) {
                j.setPlayer(player.getName());
                return;
            }

            j = new JugadorDatos(player.getName(), player.getUniqueId().toString(), new ArrayList<>());
            manager.agregarJugadorDatos(j);
            giveFirstJoinKits(player);
        } else {

            // Wont handle mysql rn...

            MySQL.actualizarNombre(plugin, player.getName(), player.getUniqueId().toString());
            MySQL.getJugadorByUUID(player.getUniqueId().toString(), plugin, new MySQLJugadorCallback() {
                @Override
                public void alTerminar(JugadorDatos j) {
                    manager.removerJugadorDatos(player.getName());
                    if (j != null) {
                        manager.agregarJugadorDatos(j);
                    } else {
                        //Lo crea si no existe
                        MySQL.crearKitJugador(plugin, player.getName(), player.getUniqueId().toString(), null);
                        manager.agregarJugadorDatos(new JugadorDatos(player.getName(), player.getUniqueId().toString(), new ArrayList<>()));
                        giveFirstJoinKits(player);
                    }
                }
            });
        }
    }

    public void giveFirstJoinKits(Player jugador) {
        FileConfiguration kitConfig = plugin.getKits();
        if (kitConfig.contains("Kits")) {
            for (String key : kitConfig.getConfigurationSection("Kits").getKeys(false)) {
                if (kitConfig.contains("Kits." + key + ".first_join") && kitConfig.getBoolean("Kits." + key + ".first_join")) {
                    KitManager.claimKit(jugador, key, plugin, false, false, false);
                }
            }
        }
    }
}
