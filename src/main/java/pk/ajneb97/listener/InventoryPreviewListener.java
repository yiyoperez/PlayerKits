package pk.ajneb97.listener;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import pk.ajneb97.PlayerKits;
import pk.ajneb97.model.CurrentPlayerInventory;
import pk.ajneb97.manager.InventarioManager;

public class InventoryPreviewListener implements Listener {

    private final PlayerKits plugin;

    public InventoryPreviewListener(PlayerKits plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        FileConfiguration config = plugin.getConfig();
        Player player = (Player) event.getWhoClicked();

        CurrentPlayerInventory inv = plugin.getInventarioJugador(player.getName());
        if (inv == null) return;

        if (event.getCurrentItem() == null) {
            event.setCancelled(true);
            return;
        }

        int slot = event.getSlot();
        event.setCancelled(true);
        if (event.getClickedInventory() == player.getOpenInventory().getTopInventory()) {
            if (inv.getInventoryType().equals("preview")) {
                int slotAClickear = config.getInt("preview-inventory.back-item-slot");
                if (slot == slotAClickear && event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
                    InventarioManager.openMainInventory(config, plugin, player, inv.getPage());
                }
            }
        }
    }
}
