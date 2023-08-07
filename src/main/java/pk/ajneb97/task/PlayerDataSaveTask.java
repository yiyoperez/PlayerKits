package pk.ajneb97.task;

import org.bukkit.scheduler.BukkitRunnable;
import pk.ajneb97.PlayerKits;
import pk.ajneb97.util.PluginLogger;

public class PlayerDataSaveTask {

    private final PlayerKits plugin;
    private boolean stop;

    public PlayerDataSaveTask(PlayerKits plugin) {
        this.plugin = plugin;
        this.stop = false;
    }

    public void end() {
        this.stop = true;
    }

    public void start() {
        int timeSeconds = plugin.getConfig().getInt("player_data_save_time");
        new BukkitRunnable() {
            @Override
            public void run() {
                if (stop) {
                    this.cancel();
                    return;
                }

                execute();
            }
        }.runTaskTimerAsynchronously(plugin, 0L, 20L * timeSeconds);
    }

    public void execute() {
        PluginLogger.info("Saving player data automatically.");
        plugin.getPlayerManager().saveServerPlayerData();
    }
}
