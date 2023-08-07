package pk.ajneb97.inventory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pk.ajneb97.PlayerKits;
import pk.ajneb97.models.CurrentPlayerInventory;
import pk.ajneb97.utils.MessageHandler;
import pk.ajneb97.utils.MessageUtils;
import pk.ajneb97.utils.Placeholder;
import pk.ajneb97.utils.Utils;

import java.util.List;

public class PurchaseConfirmationMenu implements Listener {


    //TODO: THIS
    @SuppressWarnings("deprecation")
    public static void openInventory(Player player, PlayerKits plugin, double price, String kit, int page) {
        MessageHandler messageHandler = plugin.getMessageHandler();

        Inventory inv = Bukkit.createInventory(null, 9, MessageUtils.getMensajeColor(messageHandler.getRawMessage("moneyInventoryName")));
        List<String> lore = messageHandler.getRawStringList("moneyInventoryConfirmationLore", new Placeholder("%price%", price));


        ItemStack item = applyMetaChanges(Utils.isLegacy() ? Material.valueOf("STAINED_GLASS_PANE") : Material.LIME_STAINED_GLASS_PANE,
                MessageUtils.getMensajeColor(messageHandler.getRawMessage("moneyInventoryYes")), lore);

        inv.setItem(0, item);
        inv.setItem(1, item);
        inv.setItem(2, item);
        inv.setItem(3, item);


        item = applyMetaChanges(Utils.isLegacy() ? Material.valueOf("STAINED_GLASS_PANE") : Material.RED_STAINED_GLASS_PANE,
                MessageUtils.getMensajeColor(messageHandler.getRawMessage("moneyInventoryNo")), lore);
        inv.setItem(5, item);
        inv.setItem(6, item);
        inv.setItem(7, item);
        inv.setItem(8, item);


        item = applyMetaChanges(Material.COAL_BLOCK, MessageUtils.getMensajeColor(messageHandler.getRawMessage("moneyInventoryConfirmationName")), lore);
        inv.setItem(4, item);

        player.openInventory(inv);

        plugin.agregarInventarioJugador(new CurrentPlayerInventory(player, page, null, "buying: " + kit));
    }

    private static ItemStack applyMetaChanges(Material material, String displayName, List<String> lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

}
