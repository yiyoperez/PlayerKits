package pk.ajneb97.managers;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pk.ajneb97.PlayerKits;
import pk.ajneb97.inventory.PlayerInventory;
import pk.ajneb97.utils.MessageUtils;

public class InventoryPreview implements Listener {

    private final PlayerKits plugin;

    public InventoryPreview(PlayerKits plugin) {
        this.plugin = plugin;
    }

    public static void abrirInventarioPreview(PlayerKits plugin, Player player, String kit, int page) {
        FileConfiguration kits = plugin.getKits();
        FileConfiguration config = plugin.getConfig();
        FileConfiguration messages = plugin.getMessages();

        int slots = config.getInt("preview-inventory.size");
        Inventory inv = Bukkit.createInventory(null, slots, MessageUtils.getMensajeColor(messages.getString("previewInventoryName")));

        if (config.getBoolean("preview-inventory.back-item")) {
            ItemStack item = new ItemStack(Material.ARROW);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(MessageUtils.getMensajeColor(messages.getString("backItemName")));
            item.setItemMeta(meta);
            inv.setItem(config.getInt("preview-inventory.back-item-slot"), item);
        }

        if (!kits.contains("Kits." + kit + ".Items")) {
            String prefix = messages.getString("prefix");
            player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("noPreviewError")));
            return;
        }

        int slot = 0;
        for (String n : kits.getConfigurationSection("Kits." + kit + ".Items").getKeys(false)) {
            String path = "Kits." + kit + ".Items." + n;
            ItemStack item = KitManager.getItem(kits, path, config, player);
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

        plugin.agregarInventarioJugador(new PlayerInventory(player, page, null, "preview"));
    }

    @EventHandler
    public void clickInventario(InventoryClickEvent event) {
        FileConfiguration config = plugin.getConfig();
        Player jugador = (Player) event.getWhoClicked();
        PlayerInventory inv = plugin.getInventarioJugador(jugador.getName());
        if (inv == null) {
            return;
        }

        if (event.getCurrentItem() == null) {
            event.setCancelled(true);
            return;
        }

        int slot = event.getSlot();
        event.setCancelled(true);
        if (event.getClickedInventory() == jugador.getOpenInventory().getTopInventory()) {
            if (inv.getTipoInventario().equals("preview")) {
                int slotAClickear = config.getInt("preview-inventory.back-item-slot");
                if (slot == slotAClickear && event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
                    InventarioManager.openMainInventory(config, plugin, jugador, inv.getPagina());
                }
            }
        }
    }
}
