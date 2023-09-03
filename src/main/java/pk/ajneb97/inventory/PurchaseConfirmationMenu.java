package pk.ajneb97.inventory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pk.ajneb97.PlayerKits;
import pk.ajneb97.model.CurrentPlayerInventory;
import pk.ajneb97.util.ItemStackBuilder;
import pk.ajneb97.util.MessageHandler;
import pk.ajneb97.util.MessageUtils;
import pk.ajneb97.util.Placeholder;
import pk.ajneb97.util.Utils;

public class PurchaseConfirmationMenu implements Listener {


    public static void openInventory(Player player, PlayerKits plugin, double price, String kit, int page) {
        MessageHandler messageHandler = plugin.getMessageHandler();

        Inventory inv = Bukkit.createInventory(null, 9, MessageUtils.translateColor(messageHandler.getRawMessage("purchase.inventory-name")));

        ItemStack yesPanel = new ItemStackBuilder(Utils.isLegacy() ? Material.valueOf("STAINED_GLASS_PANE") : Material.LIME_STAINED_GLASS_PANE)
                .name(messageHandler.getRawMessage("purchase.buy.name"))
                .lore(messageHandler.getRawStringList("purchase.buy.lore"));
        inv.setItem(0, yesPanel);
        inv.setItem(1, yesPanel);
        inv.setItem(2, yesPanel);
        inv.setItem(3, yesPanel);


        ItemStack noPanel = new ItemStackBuilder(Utils.isLegacy() ? Material.valueOf("STAINED_GLASS_PANE") : Material.RED_STAINED_GLASS_PANE)
                .name(messageHandler.getRawMessage("purchase.cancel.name"))
                .lore(messageHandler.getRawStringList("purchase.cancel.lore"));
        inv.setItem(5, noPanel);
        inv.setItem(6, noPanel);
        inv.setItem(7, noPanel);
        inv.setItem(8, noPanel);


        ItemStack confirmation = new ItemStackBuilder(Material.COAL_BLOCK)
                .name(messageHandler.getRawMessage("purchase.confirmation.name"))
                .lore(messageHandler.getRawStringList("purchase.confirmation.lore",
                        new Placeholder("%price%", price)));
        inv.setItem(4, confirmation);

        player.openInventory(inv);
        plugin.agregarInventarioJugador(new CurrentPlayerInventory(player, page, null, "buying: " + kit));
    }
}
