package pk.ajneb97.listener;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import pk.ajneb97.PlayerKits;
import pk.ajneb97.manager.InventarioManager;
import pk.ajneb97.manager.KitManager;
import pk.ajneb97.model.CurrentPlayerInventory;
import pk.ajneb97.util.MessageHandler;
import pk.ajneb97.util.Placeholder;

public class PurchaseListener implements Listener {

    private final PlayerKits plugin;

    public PurchaseListener(PlayerKits plugin) {
        this.plugin = plugin;
    }

    //TODO: THIS
    @EventHandler
    public void inventoryClick(InventoryClickEvent event) {
        FileConfiguration config = plugin.getConfig();
        Player player = (Player) event.getWhoClicked();
        KitManager kitManager = plugin.getKitManager();
        MessageHandler messageHandler = plugin.getMessageHandler();
        CurrentPlayerInventory inv = plugin.getInventarioJugador(player.getName());

        if (inv == null) return;

        if (event.getCurrentItem() == null) {
            event.setCancelled(true);
            return;
        }

        if (event.getClickedInventory() != player.getOpenInventory().getTopInventory()) return;

        String tipoInventario = inv.getInventoryType();
        if (!tipoInventario.startsWith("buying")) return;

        event.setCancelled(true);

        String kit = tipoInventario.replace("buying: ", "");
        int slot = event.getSlot();
        // If slots are in "NO" item, It returns player to main inventory, or it gets closed.
        if (slot >= 5 && slot <= 8) {
            int pag = inv.getPage();
            if (pag != -1) {
                InventarioManager.openMainInventory(config, plugin, player, pag);
            } else {
                player.closeInventory();
            }
            messageHandler.sendMessage(player, "purchase.failed", new Placeholder("%name%", kit));
        }

        // If slots are in "YES" item.
        if (slot >= 0 && slot <= 3) {

            kitManager.attemptBuyKit(player, kit);

            int pag = inv.getPage();
            if (pag != -1) {
                InventarioManager.openMainInventory(config, plugin, player, pag);
            } else {
                player.closeInventory();
            }
        }
    }
}
