package pk.ajneb97.listeners;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import pk.ajneb97.PlayerKits;
import pk.ajneb97.models.CurrentPlayerInventory;
import pk.ajneb97.managers.InventarioManager;
import pk.ajneb97.managers.KitManager;
import pk.ajneb97.utils.MessageUtils;

public class PurchaseListener implements Listener {

    private final PlayerKits plugin;

    public PurchaseListener(PlayerKits plugin) {
        this.plugin = plugin;
    }

    //TODO: THIS
    @EventHandler
    public void clickInventario(InventoryClickEvent event) {
        FileConfiguration config = plugin.getConfig();
        FileConfiguration messages = plugin.getMessages();
        Player player = (Player) event.getWhoClicked();
        KitManager kitManager = plugin.getKitManager();
        CurrentPlayerInventory inv = plugin.getInventarioJugador(player.getName());

        if (inv != null) {
            if (event.getCurrentItem() == null) {
                event.setCancelled(true);
                return;
            }
            // HMM ?
            if (event.getSlotType() == null) {
                event.setCancelled(true);
                return;
            }

            String prefix = messages.getString("prefix");
            int slot = event.getSlot();
            event.setCancelled(true);
            if (event.getClickedInventory() == player.getOpenInventory().getTopInventory()) {
                String tipoInventario = inv.getInventoryType();
                if (tipoInventario.startsWith("buying")) {
                    if (slot >= 0 && slot <= 3) {
                        FileConfiguration configKits = plugin.getKits();
                        String kit = tipoInventario.replace("buying: ", "");
                        double price = configKits.getDouble("Kits." + kit + ".price");
                        Economy econ = plugin.getEconomy();
                        double balance = econ.getBalance(player);
                        if (balance < price) {
                            player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("noMoneyError")
                                    .replace("%current_money%", String.valueOf(balance)).replace("%required_money%", String.valueOf(price))));
                        } else {
                            //kitManager.claimKit(player, kit, true, false, true);
                            kitManager.attemptBuyKit(player, kit);
                            int pag = inv.getPage();
                            if (pag != -1) {
                                InventarioManager.openMainInventory(config, plugin, player, pag);
                            } else {
                                player.closeInventory();
                            }

                        }
                    } else if (slot >= 5 && slot <= 8) {
                        int pag = inv.getPage();
                        if (pag != -1) {
                            InventarioManager.openMainInventory(config, plugin, player, pag);
                        } else {
                            player.closeInventory();
                        }
                    }
                }
            }
        }
    }
}
