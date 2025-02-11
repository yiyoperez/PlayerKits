package pk.ajneb97.inventory;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pk.ajneb97.PlayerKits;
import pk.ajneb97.manager.KitManager;
import pk.ajneb97.model.CurrentPlayerInventory;
import pk.ajneb97.util.MessageUtils;

public class InventoryPreview {

    public static void openInventory(PlayerKits plugin, Player player, String kit, int page) {
        FileConfiguration kits = plugin.getKits();
        FileConfiguration config = plugin.getConfig();
        FileConfiguration messages = plugin.getMessages();

        int slots = config.getInt("preview-inventory.size");
        Inventory inv = Bukkit.createInventory(null, slots, MessageUtils.translateColor(messages.getString("preview.inventory-name")));

        if (config.getBoolean("preview-inventory.back-item")) {
            ItemStack item = new ItemStack(Material.ARROW);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(MessageUtils.translateColor(messages.getString("preview.back-item-name")));
            item.setItemMeta(meta);
            inv.setItem(config.getInt("preview-inventory.back-item-slot"), item);
        }

        if (kits.getConfigurationSection("Kits." + kit + ".Items").getKeys(false).isEmpty()) {
            String prefix = messages.getString("prefix");
            player.sendMessage(MessageUtils.translateColor(prefix + messages.getString("preview.no-preview-error")));
            return;
        }

        int slot = 0;
        for (String n : kits.getConfigurationSection("Kits." + kit + ".Items").getKeys(false)) {
            String path = "Kits." + kit + ".Items." + n;
            ItemStack item = KitManager.getItem(kits, path, player);
            try {
                if (kits.contains(path + ".preview_slot")) {
                    inv.setItem(kits.getInt(path + ".preview_slot"), item);
                } else {
                    inv.setItem(slot, item);
                    slot++;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', PlayerKits.pluginPrefix + "&cThere is an error. Items for this kits are set on an invalid slot of the preview inventory. Change the &7previewInventorySize &coption in the config.yml file!"));
                return;
            }
        }

        player.openInventory(inv);

        plugin.agregarInventarioJugador(new CurrentPlayerInventory(player, page, null, "preview"));
    }
}
